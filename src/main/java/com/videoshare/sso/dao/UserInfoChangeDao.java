package com.videoshare.sso.dao;

import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.INTERNAL;

import com.videoshare.sso.pojo.User;
import com.videoshare.sso.pojo.UserDetal;

public interface UserInfoChangeDao {

	
	int UpdateNickname(@Param("username")String username,@Param("nickname")String nickname);

	int UpdateHeadUrl(@Param("username")String username,@Param("headurl")String url);
	
	int UpdateBackground(@Param("username")String username,@Param("imageurl")String url);
	
	int UpdatePassword(@Param("username")String username,@Param("password")String password);

	int UpdateSign(@Param("username")String username,@Param("sign")String sign);
	
	int UpdateEMail(@Param("username")String username,@Param("emal")String emal);
	
	int UpdateAll(User user);
	
	int SelectNickName(@Param("nickname")String nickname);
	
	
	UserDetal SelectUserDetal(@Param("username")String username);
	
	UserDetal SelectUserDetalById(@Param("uid")String uid);
	
	UserDetal SelectUserDetalByNickname(@Param("nickname")String nickname);
	
	
	int UpdateDetal(UserDetal userDetal);
	
	
	
	
	
	
}
