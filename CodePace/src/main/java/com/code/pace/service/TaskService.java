package com.code.pace.service;

import org.springframework.stereotype.Service;

import com.code.pace.model.Task;
import com.code.pace.model.User;

@Service
public interface TaskService {
	public Task saveTask(Task task, User user); 
}
