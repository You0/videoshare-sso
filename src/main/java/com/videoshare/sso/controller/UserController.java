package com.videoshare.sso.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mysql.jdbc.log.Log;
import com.vedioshare.image.ImagePreProcess;
import com.videoshare.common.service.RedisService;
import com.videoshare.sso.dao.UserDao;
import com.videoshare.sso.enumpack.InfoChangeType;
import com.videoshare.sso.service.UserService;

import net.sf.jsqlparser.parser.Token;

@RequestMapping(value = "user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
	private RedisService redisService;

    
    @RequestMapping(value = "check", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> ChangeBackground(@RequestParam(value="Token")String 
			Token) {
    	if(redisService.get(Token)!=null){
    		return ResponseEntity.status(HttpStatus.OK).body(null);
    	}else{
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    	}		
	}
    
    

    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam(value= "username",required = false)  String UserName,
    		@RequestParam(value = "password", required = false) String passWord,
    		@RequestParam(value = "Token",required=false) String Token)
    {
    	String LoginInfo = null;
    	if(Token == null){
    		Token = "false";
        	Token = userService.login(UserName, passWord);
        	return Token;
    	}else{
    		LoginInfo = userService.loginBytoken(Token);
    		if(LoginInfo.equals("false")){
    			return "false";
    		}
    		return LoginInfo;
    		
    		
    	}
    	
    	
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> logout(@RequestParam("Token")String Token) {
        try {
            Boolean isLogout = this.userService.logout(Token);
            if (isLogout) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public String register(
    		@RequestParam(value="username") String userName, 
    		@RequestParam(value="password") String password,
    		HttpServletRequest request) {
        try {
        	//System.out.println(userName+password);
        	ImagePreProcess.srcPath = request.getSession().getServletContext().getRealPath("/img");
        	ImagePreProcess.trainPath = request.getSession().getServletContext().getRealPath("/train");
        	ImagePreProcess.tempPath = request.getSession().getServletContext().getRealPath("/temp");
        	ImagePreProcess.tempPath = request.getSession().getServletContext().getRealPath("/result");
        	System.out.println(ImagePreProcess.srcPath);
        	//System.out.println(request.getSession().getServletContext().getRealPath("/result"));
        	//System.out.println(request.getSession().getServletContext().getRealPath("/train"));
        	String register = this.userService.register(userName, password);
            return register;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }
    
   
    
    @RequestMapping(value = "uid", method = RequestMethod.POST)
    @ResponseBody
    public int getUserId(@Param("username")String username)
    {
    	int uid = -1;
    	uid = userService.getUserId(username);
    	return uid;
    }
    

//    @RequestMapping(value = "check/{filed}/{type}", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity<Void> checkFiled(@PathVariable("filed") String filed, @PathVariable("type") String type) {
//        try {
//            Boolean checkFiled = this.userService.checkFiled(filed, type);
//            if (checkFiled) {
//                return ResponseEntity.ok(null);
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }

    
  //  public void getUserByFiled
}
