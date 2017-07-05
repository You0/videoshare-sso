package com.videoshare.sso.dao;

import org.apache.ibatis.annotations.Param;

import com.videoshare.sso.pojo.User;

public interface UserDao {

	//注册
	int Register(User user);
	
	//登录
	int Login(@Param("UserName") String UserName,@Param("PassWord") String PassWord);
	
	//生成Token之后更新进去
	int UpdateToken(@Param("UserName")String UserName,@Param("Token")String Token);
	
	//使用Token登录
	String LoginByToken(@Param("Token")String Token);
	
	//退出登录
	int Logout(@Param("Token")String Token);
	
	int QueryByUserName(@Param("UserName") String Username);
	
	int QueryUidByUserName(@Param("username")String username);
	
	int InsertRelation(@Param("UserName") String Username);
	
	String QueryUserNickname(@Param("username")String username);
	
	int InsertDetalInRegister(@Param("username")String username);
	
	String QueryUserNameByUid(@Param("uid")int uid);

	
}
