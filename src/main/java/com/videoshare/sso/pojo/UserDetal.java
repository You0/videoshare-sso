package com.videoshare.sso.pojo;

import org.springframework.beans.factory.annotation.Value;


public class UserDetal {
	private int id;
	private String username;
	private String e_mail;
	private String nickname;
	private String sign;
	private String sex;
	private String grades;
	private String major;
	private String school;
	private String imageurl;
	private String headurl;
	

	private int coutFayan;
	private int countShouChang;
	private int countLove;
	
	private boolean already;
	
	public void setAlready(boolean already) {
		this.already = already;
	}

	public boolean getAlready() {
		return already;
	}
	
	public int getCountLove() {
		return countLove;
	}

	public int getCountShouChang() {
		return countShouChang;
	}

	public int getCoutFayan() {
		return coutFayan;
	}

	public void setCountLove(int countLove) {
		this.countLove = countLove;
	}

	public void setCountShouChang(int countShouChang) {
		this.countShouChang = countShouChang;
	}

	public void setCoutFayan(int coutFayan) {
		this.coutFayan = coutFayan;
	}

	public String getHeadurl() {
		return headurl;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	@Override
	public String toString() {
		return "UserDetal [id=" + id + ", username=" + username + ", e_mail=" + e_mail + ", nickname=" + nickname
				+ ", sign=" + sign + ", sex=" + sex + ", grades=" + grades + ", major=" + major + ", school=" + school
				+ "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getE_mail() {
		return e_mail;
	}

	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getGrades() {
		return grades;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

}
