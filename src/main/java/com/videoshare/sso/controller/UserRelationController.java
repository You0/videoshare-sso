package com.videoshare.sso.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.videoshare.sso.pojo.SimpleUser;
import com.videoshare.sso.pojo.User;
import com.videoshare.sso.service.UserRelationService;
import com.videoshare.sso.service.UserService;

import net.sf.jsqlparser.parser.Token;

@RequestMapping("relation")
@Controller
public class UserRelationController {
	
	@Autowired
    private UserService userService;

	@Value("#{configProperties['site.userhead']}")
	private String userhead;
	
	@Autowired
	private UserRelationService mRelationService;

	@RequestMapping(value = "getrelation", method = RequestMethod.POST)
	@ResponseBody
	private List<SimpleUser> getRelationByLevel(@RequestParam("username") String username, @RequestParam("level") int level) {
		List<SimpleUser> temp = mRelationService.getRelationByLevel(username, level);
		String[] contacts = mRelationService.queryContacts(username);
		boolean contain = false;
		int index;
		List<SimpleUser> users = new ArrayList<>();
		//搜索是否已经关注，如果已经关注则不再提示
		for(int i=0;i<temp.size();i++)
		{
			for(int j=0;j<contacts.length;j++)
			{
				if(contacts[j].equals(temp.get(i).getUsername())||
						username.equals(temp.get(i).getUsername())){
					contain = true;
				}
			}
			if(!contain){
				System.out.println(temp.get(i).getHeadurl());
				temp.get(i).setHeadurl(userhead+"/"+temp.get(i).getHeadurl());
				users.add(temp.get(i));
			}
			contain = false;
		}
		
		return users;
	}

	@RequestMapping(value="getcontacts",method = RequestMethod.POST)
	@ResponseBody
	public List<SimpleUser> getContactRelation(@RequestParam("username")String username)
	{
		return mRelationService.queryContactsDetail(username);
	}
	
	
	
	@RequestMapping(value = "relationbuild", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ResponseEntity<Void> RelationBuild(@RequestParam("username") String username,
			@RequestParam("contact") String contact) {
		
		String[] users = contact.split(";");
		for(int i=0;i<users.length;i++){
			try {
				Boolean isSuccess = mRelationService.insertContacts(username, users[i]+";");
				if (!isSuccess) {
					throw new Exception("关系插入失败");
				} else {
					isSuccess = mRelationService.insertRevContacts(users[i], username+";");
					if (!isSuccess) {
						throw new Exception("关系插入失败");
					}
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
		
		
		//feed模块添加好友
		OutputStream os;
		try {
			System.out.println("为Feed模块建立好友关系");
			String uids = getUids(contact,true);
			int uid = getUserId(username);
			URL url = new URL("http://115.159.27.70:8084/rest/feed/addFriend");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			String sb = "myId="+uid+"&friendId="+uids+"&token=123";
			os = connection.getOutputStream();
			os.write(sb.toString().getBytes("utf-8"));
			connection.getResponseCode();
			System.out.println("Feed模块好友关系建立完毕");
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			
		}
		
		
		
		
		
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@RequestMapping(value = "contacts",method = RequestMethod.POST)
	@ResponseBody
	public String AlreadyContacts(@RequestParam("username")String username,
			@RequestParam("contacts")String contacts)
	{
		Boolean bool = mRelationService.queryContacts(username,contacts);
		return bool?"true":"false";
	}
	
	
	@RequestMapping(value = "incLove",method = RequestMethod.POST)
	@ResponseBody
	public void IncLove(@RequestParam("uid")String uid){
		mRelationService.IncLove(Integer.valueOf(uid));
		
	}
	
	@RequestMapping(value = "decLove",method = RequestMethod.POST)
	@ResponseBody
	public void DecLove(@RequestParam("uid")String uid){
		mRelationService.DecLove(Integer.valueOf(uid));
		
	}
	
	
	@RequestMapping(value="removeContact",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> RemoveContact(@RequestParam("username")String username
			,@RequestParam("uid")String uid)
	{
		String contact = userService.getUserName(uid);
		String[] contacts = mRelationService.queryContacts(username);
		//String[] rev_contacts = mRelationService.queryRevContacts(username);
		String[] t_rev_contacts = mRelationService.queryRevContacts(contact);
		String newContacts = "";
		String newRevContacts = "";
		for(int i=0;i<contacts.length;i++)
		{
			if(contacts[i].equals(contact)){
				
			}else{
				newContacts+=contacts[i]+";";
			}
		}
		
		for(int i=0;i<t_rev_contacts.length;i++){
			if (t_rev_contacts[i].equals(username)) {
			}else{
				newRevContacts += t_rev_contacts[i] + ";";
			}
		}
		
		try{
			mRelationService.ChangeContactFiled(newContacts, username);
			mRelationService.ChangeRevContactFiled(newRevContacts, contact);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}
		
		
		//feed模块删除好友
		OutputStream os;
		try {
			System.out.println("为Feed模块建立好友关系");
			//String uids = getUids(contact,true);
			int myid = getUserId(username);
			URL url = new URL("http://115.159.27.70:8084/rest/feed/removeFriend");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			String sb = "myId="+myid+"&friendId="+uid+"&token=123";
			os = connection.getOutputStream();
			os.write(sb.toString().getBytes("utf-8"));
			connection.getResponseCode();
			System.out.println("Feed模块好友关系建立完毕");
			} catch (IOException e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
					
		}
		
		
		
		
		
		
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	
	@RequestMapping(value="addContact",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> AddContact(
			@RequestParam("username")String username,
			@RequestParam(value = "uid")String uid)
	{
		String contact = userService.getUserName(uid);
		
		try {
			Boolean isSuccess = mRelationService.insertContacts(username, contact+";");
			if (!isSuccess) {
				throw new Exception("关系插入失败");
			} else {
				isSuccess = mRelationService.insertRevContacts(contact, username+";");
				if (!isSuccess) {
					throw new Exception("关系插入失败");
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
				
		
		//feed模块添加好友
		OutputStream os;
		try {
			System.out.println("为Feed模块建立好友关系");
			//String uids = getUids(contact,true);
			int myid = getUserId(username);
			URL url = new URL("http://115.159.27.70:8084/rest/feed/addFriend");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			String sb = "myId="+myid+"&friendId="+uid+"&token=123";
			os = connection.getOutputStream();
			os.write(sb.toString().getBytes("utf-8"));
			connection.getResponseCode();
			System.out.println("Feed模块好友关系建立完毕");
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			
		}
		
		
		
		
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="getRevContactsList",method = RequestMethod.POST)
	@ResponseBody
	public List<SimpleUser> getRevContactsInfoList(@RequestParam("username")String username)
	{
		return mRelationService.queryRevContactsDetail(username);
	}
	
	
	@RequestMapping(value="getDisPlayNums",method = RequestMethod.POST)
	@ResponseBody
	public long[] getDisPlayNums(@RequestParam(value="username",required=false)String username,
			@RequestParam(value="nickname",required=false)String nickname)
	{
		if(nickname!=null && !nickname.equals(""))
		{
			try {
				nickname = URLDecoder.decode(nickname,"UTF-8");
				nickname = URLDecoder.decode(nickname,"UTF-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			username = mRelationService.queryUsername(nickname);
			System.out.println("username:"+username+"<------>"+"nickanme:"+nickname);
		}
		
		return mRelationService.queryNums(username);
	}
	
	
	public int getUserId(@Param("username")String username)
    {
    	int uid = -1;
    	uid = userService.getUserId(username);
    	return uid;
    }
	
	public int[] getUids(String contacts)
	{
		String[] cs = contacts.split(";");
		int[] uids = new int[cs.length];
		for(int i=0;i<cs.length;i++)
		{
			uids[i] = getUserId(cs[i]);
		}
		
		return uids;
	}
	
	public String getUids(String contacts,boolean tag)
	{
		int[] uids = getUids(contacts);
		String suids = "";
		for(int i=0;i<uids.length;i++)
		{
			suids += uids[i]+";";
		}
		return suids;
	}
	
	
	
}
