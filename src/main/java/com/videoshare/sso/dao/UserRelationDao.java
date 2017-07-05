package com.videoshare.sso.dao;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.videoshare.sso.pojo.SimpleUser;
import com.videoshare.sso.pojo.User;
public interface UserRelationDao {

	//同班
	List<SimpleUser> getRelationLevelByOrder(String username);
	
	//用户关注者
	List<SimpleUser> getContacts(String username);
	
	//被关注者
	List<SimpleUser> getRevContacts(String username);
	
	int insertContacts(@Param("username")String username,@Param("contact")String contact );
	
	int insertRevContacts(@Param("username")String username,@Param("revcontact")String contact);
	
	String queryContacts(@Param("username")String username); 
	
	String queryRevContacts(@Param("username")String username); 
	
	void IncLove(@Param("username")int username);
	
	void DecLove(@Param("username")int username);
	
	int queryLove(@Param("username")String username);
	
	int ChangeContacts(@Param("text")String text,@Param("username")String username);
	
	int ChangeRevContacts(@Param("text")String text,@Param("username")String username);
	
	String queryUsernameBynickname(@Param("nickname")String nickname);
	
	
	
}
