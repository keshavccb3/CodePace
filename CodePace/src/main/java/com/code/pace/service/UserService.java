package com.code.pace.service;

import org.springframework.stereotype.Service;

import com.code.pace.model.Task;
import com.code.pace.model.User;

@Service
public interface UserService {
	public User saveUser(User user);

	public User findByEmail(String email);

	public String isCfHandleExist(String codeForcesId);

	public int generateRandomProblem();

	public String verifyProblem(String codeForcesId, Integer contestId);

	public User updateUser(User user1);

}
