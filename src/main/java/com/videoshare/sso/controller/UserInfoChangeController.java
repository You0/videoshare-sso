package com.videoshare.sso.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.util.EventReaderDelegate;

import org.apache.http.HttpRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.videoshare.sso.dao.UserInfoChangeDao;
import com.videoshare.sso.enumpack.InfoChangeType;
import com.videoshare.sso.pojo.UserDetal;
import com.videoshare.sso.service.UserInfoChangeService;
import com.videoshare.sso.service.UserRelationService;
import com.videoshare.sso.service.UserService;

@RequestMapping("info")
@Controller
public class UserInfoChangeController {
	
	@Autowired
    private UserService userService;

	@Autowired
	private UserInfoChangeService mUserInfoChangeService;
	
	@Autowired
	private UserRelationService mRelationService;

	@Value("#{configProperties['site.site']}")
	private String Site;
	
	@Value("#{configProperties['site.backgound']}")
	private String backgound;
	
	@Value("#{configProperties['site.userhead']}")
	private String userhead;
	
	@RequestMapping(value = "detail", method = RequestMethod.POST)
	@ResponseBody
	public String DetailChange(UserDetal detal) {
		System.out.println(detal.toString());
		int i = mUserInfoChangeService.SetUserDetial(detal);
		if (i != 0) {
			return "success";
		}

		return "false";
	}
	
	
	@RequestMapping(value="getParam",method=RequestMethod.POST)
	@ResponseBody
	public String GetBackground(@RequestParam("username")String username,
			@RequestParam("param")String param){
		
		UserDetal uDetal = mUserInfoChangeService.getUserDetialByUserName(username);
		
		switch(param)
		{
		 case "imageurl":{
			 return uDetal.getImageurl();
		 }
		 
		 case "headurl":{
			 return uDetal.getHeadurl();
		 }
		 
		}
		
		return param;
	}
	
	@ResponseBody
	@RequestMapping(value="userinfo",method=RequestMethod.POST)
	public UserDetal GetUserInfo(@RequestParam(value="userid",required=false) String uid,
			@RequestParam(value="contact",required = false)String contact,
			@RequestParam(value="nickname",required=false)String nickname,
			@RequestParam(value="Token")String Token,
			@RequestParam(value="username",required=false)String username)
	{
		UserDetal userDetal = null;
		String contacts = null;
		//String json = null;
		if(uid!=null){
			System.out.println("userinfo uid");
			userDetal = mUserInfoChangeService.getUserDetialByUid(uid);
			contacts = userService.getUserName(uid);
		}else if(contact!=null){
			System.out.println("userinfo contact");
			userDetal = mUserInfoChangeService.getUserDetialByUserName(contact);
			contacts = contact;
		}else if(nickname!=null){
			System.out.println("userinfo nickname");
			try {
				nickname = URLDecoder.decode(nickname,"UTF-8");
				nickname = URLDecoder.decode(nickname,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contacts = mRelationService.queryUsername(nickname);
			userDetal = mUserInfoChangeService.getUserDetialByNickname(nickname);
		}
		Boolean bool=false;
		if(username!=null&&!username.equals("admin")){
			bool = mRelationService.queryContacts(username,contacts);
		}
		userDetal.setAlready(bool);
		return userDetal;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="userinfolist",method=RequestMethod.POST)
	public List<UserDetal> GetUserInfoList(@RequestParam(value="userid",required=false) String uid,
			@RequestParam(value="contact",required = false)String contact)
	{
		List<UserDetal> userDetals = new ArrayList<>();
		UserDetal userDetal = null;
		//String json = null;
		if(uid!=null){
			String[] uids = uid.split(";");
			for (int i = 0; i < uids.length; i++) {
				userDetal = mUserInfoChangeService.getUserDetialByUid(uids[i]);
				userDetals.add(userDetal);
			}
		}else if(contact!=null){
			String[] contacts = contact.split(";");
			for (int i = 0; i < contacts.length; i++) {
				userDetal = mUserInfoChangeService.getUserDetialByUserName(contacts[i]);
				userDetals.add(userDetal);
			}
		}
		return userDetals;
	}
	
	
	
	
	
	
	
	
	
	
	

	@RequestMapping(value = "backgound", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> ChangeBackground(
			@RequestParam(value="img") CommonsMultipartFile[] files,
			@RequestParam("username")String username) {
			
		String filePath = null;
		
		for (int i = 0; i < files.length; i++) {
			String fileName = null;
			InputStream inputStream = null;
			FileOutputStream out = null;
			try {
				fileName = files[i].getFileItem().getName();
				inputStream = files[i].getInputStream();
				int len = 0;
				byte[] bs = new byte[512];

				filePath = "C:\\IdeaRes\\Background\\" + fileName;
				
				File file = new File(filePath);
				
				out =new FileOutputStream(file);
//				out = new FileOutputStream(filePath);
				while ((len = inputStream.read(bs)) != -1) {
					out.write(bs, 0, len);
				}
				out.flush();
				int influence = mUserInfoChangeService.UpdateParam(username, fileName, InfoChangeType.InnerEnum.IMAGEURL);
				if (influence > 0) {
					return ResponseEntity.status(HttpStatus.OK).body(null);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@RequestMapping(value = "nickname", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> ChangeNickName(@RequestParam("username") String username,
			@RequestParam("nickname") String nickname) {
		int influence = mUserInfoChangeService.UpdateParam(username, nickname, InfoChangeType.InnerEnum.NICKNAME);
		if (influence > 0) {
			
			//发帖模块：自己关注自己
			int uid = getUserId(username);
			URL url;
			try {
				url = new URL("http://115.159.27.70:8084/rest/feed/addFriend");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				String sb = "myId="+uid+"&friendId="+uid+";&token=123";
				OutputStream os = connection.getOutputStream();
				os.write(sb.toString().getBytes("utf-8"));
				connection.getResponseCode();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}
	
	
	
	@RequestMapping(value = "userhead", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> ChangeUserHead(
			@RequestParam(value="img") CommonsMultipartFile[] files,
			@RequestParam("username")String username) {
			
		String filePath = null;
		
		for (int i = 0; i < files.length; i++) {
			String fileName = null;
			InputStream inputStream = null;
			FileOutputStream out = null;
			try {
				fileName = files[i].getFileItem().getName();
				inputStream = files[i].getInputStream();
				int len = 0;
				byte[] bs = new byte[512];

				filePath = "C:\\IdeaRes\\UserHead\\" + fileName;
				
				File file = new File(filePath);
				
				out =new FileOutputStream(file);
//				out = new FileOutputStream(filePath);
				while ((len = inputStream.read(bs)) != -1) {
					out.write(bs, 0, len);
				}
				out.flush();
				int influence = mUserInfoChangeService.UpdateParam(username, fileName, InfoChangeType.InnerEnum.HEADURL);
				if (influence > 0) {
					return ResponseEntity.status(HttpStatus.OK).body(null);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}
	
	
	
	
	
    public int getUserId(@Param("username")String username)
    {
    	int uid = -1;
    	uid = userService.getUserId(username);
    	return uid;
    }
	
	
	
	
	

}
