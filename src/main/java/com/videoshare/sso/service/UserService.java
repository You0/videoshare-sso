package com.videoshare.sso.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.stereotype.Service;

import com.vedioshare.image.JwUtils;
import com.videoshare.common.service.RedisService;
import com.videoshare.common.utils.IndexFactory;
import com.videoshare.sso.dao.UserDao;
import com.videoshare.sso.mapper.UserMapper;
import com.videoshare.sso.pojo.User;

@Service
public class UserService {

	// @Autowired
	// private UserMapper userMapper;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RedisService redisService;

	@Autowired
	private JwUtils jwUtils;

	@Autowired
	private IndexFactory indexFactory;

	private final static String LOGIN_CACHE_PRE = "LOGIN_CACHE";

	private final static String CARE_USER_PRE = "CARE_USER";

	private final static String USER_INDEX_NAME = "USER_INDEX_NAME";

	private final static String USER_TOKEN = "USER_TOKEN";

	// 0表示登录，1表示登出
	private final static int LOGIN = 0;
	private final static int LOGOUT = 1;

	/**
	 * 
	 * @param userName
	 * @param pwd
	 * @return 登陆成功返回token
	 */
	public String login(String userName, String pwd) {
		int i = userDao.Login(userName, pwd);
		if (i != 0) {
			System.out.println(userName + "登录");
			return UpDateToken(userName, LOGIN);
		}
		return "false";
	}

	/**
	 * 
	 * @param Token
	 * @param pwd
	 * @return 成功返回"success" 失败返回"false"
	 */
	public String loginBytoken(String Token) {
		String Token1 = redisService.get(Token);
		if (Token1.equals(Token)) {
			return "success";
		} else {
			String username = userDao.LoginByToken(Token);
			System.out.println(username + "登录");
			if (username == null) {
				return "false";
			} else {
				return "success";
			}
		}
	}

	/**
	 * 通过教务系统进行验证
	 * 
	 * @param userName
	 * @param pwd
	 * @return
	 */
	private String checkByJw(String userName, String pwd) {
		jwUtils.setPassword(pwd);
		jwUtils.setUsername(userName);
		jwUtils.setSite("2");
		jwUtils.initUrl();
		System.out.println("教务系统登录");
		String orcImage_Login = jwUtils.OrcImage_Login();
		return orcImage_Login;
	}

	/**
	 * 
	 * 退出登录
	 * 
	 * @param userId
	 * @return 退出登录是否成功
	 */
	public Boolean logout(String Token) {
		Long result = this.redisService.del(Token);
		userDao.Logout(Token);
		return true;
	}

	public boolean insertRelation(String username) {
		int count = userDao.InsertRelation(username);
		return count > 0 ? true : false;
	}
	
	public int insertDetail(String username){
		int i = userDao.InsertDetalInRegister(username);
		if(i!=0){
			return i;
		}
		return 0;
	}
	

	public String register(String userName, String pwd) {
		System.out.println("register------------------------->");
		String realname = checkByJw(userName, pwd);
		System.out.println("register------"+realname);
		Boolean checkByJw = "".equals(realname)||realname==null ? false : true;
		System.out.println("CheckByjw" + checkByJw);
		String Token = "false";
		
		if(checkByJw){
			int count = userDao.QueryByUserName(userName);
			if (count != 0) {
				String nickname = userDao.QueryUserNickname(userName);
				if(nickname.equals("")){
					Token = UpDateToken(userName, LOGIN);
				}
				return Token;
			}	
		}

		System.out.println(checkByJw);
		if (checkByJw) {
			User u = new User();
			u.setUsername(userName);
			u.setPassword(pwd);
			u.setRealname(realname);
			System.out.println("写入数据库");
			userDao.Register(u);
			Token = UpDateToken(userName, LOGIN);
			//插入用户关系表
			insertRelation(userName);
			//插入用户详情表
			insertDetail(userName);
			return Token;
		}
		return Token;

	}

	public String UpDateToken(String userName, int state) {
		System.out.println("更新ToKEN");
		String Token = "";
		if (state == LOGIN) {
			Token = DigestUtils.md5Hex(System.currentTimeMillis() + userName);
		} else {
			Token = "";
		}

		userDao.UpdateToken(userName, Token);
		redisService.set(Token, Token);
		redisService.set(userName, Token);
		return Token;
	}

	// public Boolean checkFiled(String filed, String type) {
	// User u = new User();
	// if (type.equalsIgnoreCase("nickName")) {
	// u.setNickName(filed);
	// // u.getClass().getDeclaredMethod("get", String.class,Long.class);
	// }
	// int selectCount = this.userMapper.selectCount(u);
	// if (selectCount != -1) {
	// return true;
	// }
	// return false;
	// }
	//
	/**
	 * 根据token 拿到基本信息
	 * 
	 * @param token
	 * @return
	 */
	public String getUserByToken(String token) {
		String string = this.redisService.get(token);
		return string;
	}
	
	
	public int getUserId(String username){
		return userDao.QueryUidByUserName(username);
	}
	
	public String getUserName(String uid){
		return userDao.QueryUserNameByUid(Integer.valueOf(uid));
	}

}
