package com.code.pace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.pace.model.Task;
import com.code.pace.model.User;

@Service
public interface TaskService {
	public Task saveTask(Task task, User user);

	public Task findById(Integer id);

	public List<Task> getAllTaskByUser(User user);

	public void deleteById(Integer id); 
}
