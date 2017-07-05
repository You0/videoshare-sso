package com.videoshare.sso.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.videoshare.sso.dao.UserInfoChangeDao;
import com.videoshare.sso.enumpack.InfoChangeType;
import com.videoshare.sso.pojo.User;
import com.videoshare.sso.pojo.UserDetal;

@Service
public class UserInfoChangeService {
	
	@Autowired
	UserInfoChangeDao userInfoChangeDao;
	
	@Value("#{configProperties['site.site']}")
	private String Site;
	
	@Value("#{configProperties['site.backgound']}")
	private String backgound;
	
	@Value("#{configProperties['site.userhead']}")
	private String userhead;
	
	
	
	public UserDetal getUserDetialByUid(String uid)
	{
		UserDetal detal = userInfoChangeDao.SelectUserDetalById(uid);
		if(detal!=null){
			detal.setImageurl(backgound+"/"+detal.getImageurl());
			detal.setHeadurl(userhead+"/"+detal.getHeadurl());
			return detal;
		}
		return null;
	} 
	
	
	public UserDetal getUserDetialByNickname(String nickname)
	{
		UserDetal detal = userInfoChangeDao.SelectUserDetalByNickname(nickname);
		if(detal!=null){
			detal.setImageurl(backgound+"/"+detal.getImageurl());
			detal.setHeadurl(userhead+"/"+detal.getHeadurl());
			return detal;
		}
		return null;
		
		
	}
	
	
	
	
	public UserDetal getUserDetialByUserName(String username)
	{
		UserDetal detal = userInfoChangeDao.SelectUserDetal(username);
		if(detal!=null){
			detal.setImageurl(backgound+"/"+detal.getImageurl());
			detal.setHeadurl(userhead+"/"+detal.getHeadurl());
			return detal;
		}
		return null;
	}
	
	public int SetUserDetial(UserDetal detal)
	{
		
		try {
			detal.setE_mail(URLDecoder.decode(URLDecoder.decode(detal.getE_mail(), "UTF-8"), "UTF-8"));
			detal.setGrades(URLDecoder.decode(URLDecoder.decode(detal.getGrades(), "UTF-8"), "UTF-8"));
			detal.setMajor(URLDecoder.decode(URLDecoder.decode(detal.getMajor(), "UTF-8"), "UTF-8"));
			detal.setNickname(URLDecoder.decode(URLDecoder.decode(detal.getNickname(), "UTF-8"), "UTF-8"));
			detal.setSchool(URLDecoder.decode(URLDecoder.decode(detal.getSchool(), "UTF-8"), "UTF-8"));
			detal.setSign(URLDecoder.decode(URLDecoder.decode(detal.getSign(), "UTF-8"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = userInfoChangeDao.UpdateDetal(detal);
		return i;
	}
	
	
	

	public int UpdateParam(String username,String param,InfoChangeType.InnerEnum type)
	{
		try {
			param = URLDecoder.decode(URLDecoder.decode(param, "UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int influence = 0;
		switch (type) {
		case NICKNAME:{			
			influence = userInfoChangeDao.UpdateNickname(username, param);
			System.out.println(param);
			break;
		}
		
		case IMAGEURL:{
			System.out.println(param);
			influence = userInfoChangeDao.UpdateBackground(username, param);
			break;
		}
		
		case HEADURL:{
			System.out.println(param);
			influence = userInfoChangeDao.UpdateHeadUrl(username, param);
			break;
		}
		
		
		default:
			break;
		}
		
		
		return influence;
	}

	
	
//	int UpdateAll(User user){
//		
//	}
	
	

}
