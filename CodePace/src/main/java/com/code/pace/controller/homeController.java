package com.code.pace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code.pace.model.User;
import com.code.pace.service.UserService;

@RestController
public class homeController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/")
	public String index(){
		return "Index Page";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@RequestBody User user) {
		User user1 = userService.saveUser(user);
		if(!ObjectUtils.isEmpty(user1)) {
			return "User Saved Successfully";
		}else {
			return "Server Error";
		}
	}
	@GetMapping("/register")
	public String register(){
		return "Register Page";
	}
	@GetMapping("/signin")
	public String signin(){
		return "SignIn Page";
	}
}
