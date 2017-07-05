package com.videoshare.sso.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.videoshare.common.service.RedisService;
import com.videoshare.sso.service.UserService;

@RequestMapping("feed")
@Controller
public class FeedController {
	@Autowired
	RedisService redisService;
	
	@Autowired
    private UserService userService;

	/*
	 * 无需关心浏览记录是否插入成功，因为不是很重要
	 * 
	 * content内容如下： xxxxxx图片;xx;xx;xx
	 */
	@RequestMapping(value="insertHistory",method = RequestMethod.POST)
	private void InsertHistory(@RequestParam("feedId") String feedId, @RequestParam("content") String content,
			@RequestParam("media") String media, @RequestParam("username") String username) {

		long len = redisService.llen(username + "history");
		
		List<String> infos = redisService.lrenge(username+"history"
				, 0l, len);
		
		for(int i=0;i<infos.size();i++)
		{
			if(infos.get(i).contains(feedId)){
				return;
			}
		}
		
		
		// 如果缓存大于了100条则删掉50条，然后再放入
		if (len > 100) {
			List<String> list = redisService.lrenge(username + "history", 0l, 50l);
			redisService.del(username + "history");
			for (int i = 0; i < list.size(); i++) {
				redisService.lpush(username + "history", list.get(i));
			}
		}
		redisService.lpush(username + "history", content + ";" + media + ";" + feedId);
	}

	// 收藏记录是否成功需要返回
	@RequestMapping(value="insertCache",method = RequestMethod.POST)
	private ResponseEntity<Void> CacheFeed(@RequestParam("feedId") String feedId,
			@RequestParam("content") String content, @RequestParam("media") String media,
			@RequestParam("username") String username) {

		long reVal = redisService.lpush(username + "cache", content + ";" + media + ";" + feedId);

		if (reVal != 0) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}
	
	
	
	
	// 是否已经收藏200表示已经收藏，403表示没有收藏
	@RequestMapping(value="isCache",method = RequestMethod.POST)
	private ResponseEntity<Void> IsCache(@RequestParam("feedId") String feedId,
			@RequestParam("username") String username) {
		Long len = redisService.llen(username+"cache");
		List<String> infos = redisService.lrenge(username+"cache", 0l, len);
			
		
		for(int i=0;i<len;i++)
		{
			String[] splits = infos.get(i).split(";");
			if(splits[2].contains(feedId)){
				return ResponseEntity.status(HttpStatus.OK).body(null);
			}
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}
	
	@RequestMapping(value="cacelCache",method = RequestMethod.POST)
	private ResponseEntity<Void> cacelCache(@RequestParam("feedId") String feedId,
			@RequestParam("username") String username) {
		
		Long len = redisService.llen(username+"cache");
		List<String> infos = redisService.lrenge(username+"cache", 0l, len);
			
		
		for(int i=0;i<infos.size();i++)
		{
			String[] splits = infos.get(i).split(";");
			if(splits[2].contains(feedId)){
				infos.remove(i);
			}
		}
		
		redisService.del(username+"cache");
		for(int i=0;i<infos.size();i++){
			redisService.lpush(username+"cache", infos.get(i));
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(null);

	}	
	
	
	
	
	
	
	
	
	@RequestMapping(value="incFeed",method = RequestMethod.POST)
	private void IncFeedCount(@RequestParam("username") String username) {
		
		try{
			 String num = redisService.get(username+"feedNums");
			 if(num!=null){
				 int  intnum = Integer.valueOf(num);
				 redisService.set(username+"feedNums", String.valueOf(intnum+1));
			 }else{
				 redisService.set(username+"feedNums","1");
			 }
		}catch (Exception e) {
			redisService.set(username+"feedNums","1");
		}
	}
	
	
	//因为回复记录和帖子的数据库是分开保存的，所以我允许遗漏，但是这边遗漏了，但是主帖子服务器那边没有遗漏
	@RequestMapping(value="cacheAnswer",method = RequestMethod.POST)
	@ResponseBody
	private void CacheFeedAnswer(@RequestParam("feedId") String feedId,
			@RequestParam("content") String content, @RequestParam("media") String media,
			@RequestParam("username") String username,
			@RequestParam("answer")String answer) {

		long reVal = redisService.lpush(username + "cacheAnswer", content +";"+ answer + ";" + media + ";" + feedId);
	}
	
	
	@RequestMapping(value="userHistory",method = RequestMethod.POST)
	@ResponseBody
	private String QueryHistoryInfoByUsername(@RequestParam("uid") String uid, @RequestParam("page") int page,
			@RequestParam("pageSize") int pageSize) {

		String username = userService.getUserName(uid);
		long start = ((page - 1) * pageSize);
		long end = page * pageSize;
		long lsize = redisService.llen(username+"history");
		if(start>lsize){
			return "";
		}else if(end > lsize){
			end = lsize;
		}
		
		JSONArray jsonArray = new JSONArray();

		List<String> infos = redisService.lrenge(username + "history", start ,
				end);

		for (int i = 0; i < infos.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			String info = infos.get(i);
			String[] strs = new String[3];
			int count = 0;
			int j = 0;
			for (j = info.length() - 1; j >= 0; j--) {
				if (info.charAt(j) == ';') {
					strs[count++] = info.substring(j+1, info.length());
					info = info.substring(0, j);
					if (count == 3) {
						break;
					}
				}
			}
			strs[2] = info;
			jsonObject.put("feedId", strs[0]);
			jsonObject.put("media", strs[1]);
			jsonObject.put("content", strs[2]);
			
			System.out.println(jsonObject.toString());
			jsonArray.put(jsonObject);
		}
		System.out.println(jsonArray.toString());
		return jsonArray.toString();
	}
	
	
	

	
	//获取用户回复信息
	@RequestMapping(value="userAnswer",method = RequestMethod.POST)
	@ResponseBody
	private String QueryAnswerInfoByUsername(@RequestParam("uid") String uid, @RequestParam("page") int page,
			@RequestParam("pageSize") int pageSize) {

		String username = userService.getUserName(uid);
		long start = ((page - 1) * pageSize);
		long end = page * pageSize;
		long lsize = redisService.llen(username+"cacheAnswer");
		if(start>lsize){
			return "";
		}else if(end > lsize){
			end = lsize;
		}
		
		JSONArray jsonArray = new JSONArray();

		List<String> infos = redisService.lrenge(username + "cacheAnswer", start ,
				end);

		for (int i = 0; i < infos.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			String info = infos.get(i);
			String[] strs = new String[4];
			int count = 0;
			for (int j = info.length() - 1; j >= 0; j--) {
				if (info.charAt(j) == ';') {
					strs[count++] = info.substring(j+1, info.length());
					info = info.substring(0, j);
					if (count == 4) {
						break;
					}
				}
			}
			strs[3] = info;
			jsonObject.put("feedId", strs[0]);
			jsonObject.put("media", strs[1]);
			jsonObject.put("answer", strs[2]);
			jsonObject.put("content", strs[3]);
			jsonArray.put(jsonObject);
		}
		System.out.println(jsonArray.toString());
		return jsonArray.toString();
	}
	
	
	
	

	@RequestMapping(value="userCache",method = RequestMethod.POST)
	@ResponseBody
	private String QueryCacheInfoByUsername(@RequestParam("uid") String uid, @RequestParam("page") int page,
			@RequestParam("pageSize") int pageSize) {

		String username = userService.getUserName(uid);
		long start = ((page - 1) * pageSize);
		long end = page * pageSize;
		long lsize = redisService.llen(username+"cache");
		if(start>lsize){
			return "";
		}else if(end > lsize){
			end = lsize;
		}
		
		JSONArray jsonArray = new JSONArray();

		List<String> infos = redisService.lrenge(username + "cache", start ,
				end);

		for (int i = 0; i < infos.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			String info = infos.get(i);
			String[] strs = new String[3];
			int count = 0;
			for (int j = info.length() - 1; j >= 0; j--) {
				if (info.charAt(j) == ';') {
					strs[count++] = info.substring(j+1, info.length());
					info = info.substring(0, j);
					if (count == 3) {
						break;
					}
				}
			}
			strs[2] = info;
			jsonObject.put("feedId", strs[0]);
			jsonObject.put("media", strs[1]);
			jsonObject.put("content", strs[2]);
			jsonArray.put(jsonObject);
		}

		return jsonArray.toString();

	}

}
