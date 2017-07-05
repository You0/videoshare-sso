package com.videoshare.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.videoshare.sso.service.UserService;

@RequestMapping(value = "back")
@Controller
public class BackStageUserApiController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "{token}")
	@ResponseBody
	public ResponseEntity<String> getUerByToken(@PathVariable("token") String token) {
		System.out.println("token" + token);
		String infor = this.userService.getUserByToken(token);
		try {
			if (infor != null) {
				System.out.println(infor);
				return ResponseEntity.ok(infor);
			}
			System.out.println("aaa");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("bbbbb");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
