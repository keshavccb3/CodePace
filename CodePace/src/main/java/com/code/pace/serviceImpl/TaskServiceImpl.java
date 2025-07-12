package com.code.pace.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.code.pace.model.Task;
import com.code.pace.model.User;
import com.code.pace.repository.TaskRepository;
import com.code.pace.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{
	
	@Autowired
	private TaskRepository taskRepository;

	@Override
	public Task saveTask(Task task, User user) {
		task.setUser(user);
		taskRepository.save(task);
		return task;
	}

	@Override
	public Task findById(Integer id) {
		return taskRepository.findById(id).orElse(null);
	}

	@Override
	public List<Task> getAllTaskByUser(User user) {
		return taskRepository.findByUserId(user.getId());
	}

	@Override
	public void deleteById(Integer id) {
		taskRepository.deleteById(id);
	}

}
