package com.code.pace.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;

import com.code.pace.model.User;
import com.code.pace.service.UserService;

@Controller
public class homeController {
	
	@Autowired
	private UserService userService;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		
		if (p != null) {
			String email = p.getName();
			User user = userService.findByEmail(email);
			m.addAttribute("user", user);
		}
	}
	
	@GetMapping("/")
	public String index(){
		return "signin";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user) {
		User user1 = userService.saveUser(user);
		return "redirect:/user/cfProblemInfo";
	}
	@GetMapping("/register")
	public String register(){
		return "register";
	}
	@GetMapping("/signin")
	public String signin(){
		return "signin";
	}
}
