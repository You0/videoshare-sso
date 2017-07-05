package com.videoshare.sso.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.lf5.LogLevelFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.videoshare.common.service.RedisService;
import com.videoshare.sso.controller.UserRelationController;
import com.videoshare.sso.dao.UserInfoChangeDao;
import com.videoshare.sso.dao.UserRelationDao;
import com.videoshare.sso.pojo.SimpleUser;
import com.videoshare.sso.pojo.User;
import com.videoshare.sso.pojo.UserDetal;

@Controller
public class UserRelationService {

	// 同班
	static public final int LEVEL_1 = 1;
	// 同专业
	static public final int LEVEL_2 = 2;
	// 同院但同年级
	static public final int LEVEL_3 = 3;
	// 同院不同年级
	static public final int LEVEL_4 = 4;
	// 全校(不开放。。)
	static public final int LEVEL_5 = 5;

	@Autowired
	private RedisService redisService;

	@Autowired
	private UserRelationDao muserRelationDao;
	
	@Value("#{configProperties['site.backgound']}")
	private String backgound;
	
	@Value("#{configProperties['site.userhead']}")
	private String userhead;
	
	
	@Autowired
	private UserInfoChangeDao mUserInfoChangeDao;

	public List<SimpleUser> getRelationByLevel(String username, int level) {
		// E41414005
		String query = null;

		// 同班
		// E414
		if (level == LEVEL_1) {
			query = username.substring(0, 4);
			query = query + '%';
			return muserRelationDao.getRelationLevelByOrder(query);

		}
		// 同专业
		// E4
		if (level == LEVEL_2) {
			query = username.substring(0, 2);
			query = query + '%';
			return muserRelationDao.getRelationLevelByOrder(query);
		}

		// 同院但同年级
		// E41414005
		// E_14%
		if (level == LEVEL_3) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(username.charAt(0));
			buffer.append("_");
			buffer.append(username.substring(2, 4));
			buffer.append("%");
			return muserRelationDao.getRelationLevelByOrder(buffer.toString());
		}

		// 同院不同年级
		// E%
		if (level == LEVEL_4) {
			query = username.charAt(0) + "%";
			return muserRelationDao.getRelationLevelByOrder(query);
		}

		return muserRelationDao.getRelationLevelByOrder(username);
	}

	public boolean insertContacts(String username, String contact) {
		
		if(queryContacts(username, contact)){
			return false;
		}
		int count = muserRelationDao.insertContacts(username, contact);
			
		return count > 0 ? true : false;
	}

	public boolean insertRevContacts(String username, String revcontact) {
		int count = muserRelationDao.insertRevContacts(username, revcontact);
		return count > 0 ? true : false;
	}
	
	public boolean queryContacts(String username,String contact)
	{
		if(username.equals(contact)){
			return true;
		}
		
		String contact1 =  muserRelationDao.queryContacts(username);
		
		String[] contacts1= contact1.split(";");
		
		for(String each : contacts1)
		{
			if(contact.equals(each)){
				return true;
			}
		}
		return false;
	}
	
	
	
	public ArrayList<SimpleUser> queryContactsDetail(String username)
	{
		ArrayList<SimpleUser> users =new ArrayList<>();
		
		String[] contacts = queryContacts(username);
		for(int i=0;i<contacts.length;i++)
		{
			UserDetal userDetal =  mUserInfoChangeDao.SelectUserDetal(contacts[i]);
			SimpleUser user = new SimpleUser();
			user.setHeadurl(userhead+"/"+userDetal.getHeadurl());
			user.setNickname(userDetal.getNickname());
			user.setUsername(userDetal.getUsername());
			user.setSign(userDetal.getSign());
			users.add(user);
		}
		return users;
	}
	
	public ArrayList<SimpleUser> queryRevContactsDetail(String username)
	{
		ArrayList<SimpleUser> users =new ArrayList<>();
		
		String[] contacts = queryRevContacts(username);
		for(int i=0;i<contacts.length;i++)
		{
			UserDetal userDetal =  mUserInfoChangeDao.SelectUserDetal(contacts[i]);
			SimpleUser user = new SimpleUser();
			user.setHeadurl(userhead+"/"+userDetal.getHeadurl());
			user.setNickname(userDetal.getNickname());
			user.setUsername(userDetal.getUsername());
			user.setSign(userDetal.getSign());
			users.add(user);
		}
		return users;
	}
	
	public long[] queryNums(String username)
	{
		long history = 0;
		long cache = 0;
		long cacheAnswer = 0;
		long feednums = 0;
		long contacts= 0;
		long rev_contacts = 0;
		long loveCount = 0;
		try{
			 history = redisService.llen(username+"history");
			 cache = redisService.llen(username+"cache");
			 cacheAnswer = redisService.llen(username+"cacheAnswer");
			 contacts = queryContacts(username).length;
			 rev_contacts = queryRevContacts(username).length;
			 loveCount = muserRelationDao.queryLove(username);
			 feednums = Long.valueOf(redisService.get(username+"feedNums"));
			 
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("history:"+history+"cache:"+cache +"cacheAnswer:"+cacheAnswer
				+"feednums:"+feednums+"contacts:"+contacts+"rev_contents:"+rev_contacts
				+"loveCount:"+loveCount);
		
		long[] nums = new long[]{
			history,cache,	cacheAnswer,feednums,
			contacts,rev_contacts,loveCount
		};
		
		return nums;
		
		
		
	}
	
	
	public String[] queryContacts(String username)
	{
		String contact1 =  muserRelationDao.queryContacts(username);
		
		String[] contacts1= contact1.split(";");
		
		return contacts1;
	}
	
	
	public String[] queryRevContacts(String username)
	{
		String contact1 =  muserRelationDao.queryRevContacts(username);
		
		String[] contacts1= contact1.split(";");
		
		return contacts1;
	}
	
	public void IncLove(int username)
	{
		muserRelationDao.IncLove(username);	
	}
	
	public void DecLove(int username)
	{
		muserRelationDao.DecLove(username);	
	}
	
	@Transactional
	public void ChangeContactFiled(String text,String username)
	{
		muserRelationDao.ChangeContacts(text, username);
	}
	
	
	@Transactional
	public void ChangeRevContactFiled(String text,String username)
	{
		muserRelationDao.ChangeRevContacts(text, username);
	}
	
	
	

	public String queryUsername(String nickname) {
		
		return muserRelationDao.queryUsernameBynickname(nickname);
		
		//return null;
	}
	
	

}
