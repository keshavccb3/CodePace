package com.code.pace.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.code.pace.model.User;
import com.code.pace.repository.UserRepository;
import com.code.pace.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
    private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public User saveUser(User user) {
		user.setRole("ROLE_USER");
		user.setAtCoderId(null);
		user.setCodeChefId(null);
		user.setCodeForcesId(null);
		user.setCodeForcesId(null);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

}
