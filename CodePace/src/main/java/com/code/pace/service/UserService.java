package com.code.pace.service;

import org.springframework.stereotype.Service;

import com.code.pace.model.User;

@Service
public interface UserService {
	public User saveUser(User user);
}
