package com.code.pace.common;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.code.pace.model.User;
import com.code.pace.service.UserService;

@Component 
public class CommonUtil {
	@Autowired
	private UserService userService;
	public User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		return userService.findByEmail(email);
	}
}
