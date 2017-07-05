package com.videoshare.sso.pojo;

import org.apache.ibatis.annotations.Param;

public class SimpleUser {
	private String username;
    private String realname;
    private String headurl;
    private String nickname;
    private String sign;
    private int id;
    
    public int getId() {
		return id;
	}
    
    public void setId(int id) {
		this.id = id;
	}
    
    public String getSign() {
		return sign;
	}
    
    public void setSign(String sign) {
		this.sign = sign;
	}
    
    public String getNickname() {
		return nickname;
	}
    
    public void setNickname(String nickname) {
		this.nickname = nickname;
	}
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getHeadurl() {
		return headurl;
	}
    
	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
    

}
