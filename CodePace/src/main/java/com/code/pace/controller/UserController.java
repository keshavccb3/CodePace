package com.code.pace.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code.pace.common.CommonUtil;
import com.code.pace.model.Task;
import com.code.pace.model.User;
import com.code.pace.service.TaskService;
import com.code.pace.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private CommonUtil commonUtil;
	
	@GetMapping("/")
	public String userHome(Principal p){
	    return "Welcome, " + commonUtil.getLoggedInUserDetails(p).getEmail();
	}
	@PostMapping("/addTask")
	public String addTask(@RequestBody Task task, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		Task task1 = taskService.saveTask(task,user);
		if(!ObjectUtils.isEmpty(task1)) {
			return "Task Added Successfully";
		}else {
			return "Server Error";
		}
	}
	@PutMapping("/updateTask/{id}")
	public String updateTask(@PathVariable Integer id, @RequestBody Task task, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		Task task1 = taskService.findById(id);
		if(task1 == null) {
			return "Task Not Present";
		}else {
			task1.setDescription(task.getDescription());
			taskService.saveTask(task1, user);
			return "Task Updated";
		}
	}
	@GetMapping("/allTask")
	public List<Task> allTask(Principal p) {
	    User user = commonUtil.getLoggedInUserDetails(p);
	    return taskService.getAllTaskByUser(user);
	}
	@DeleteMapping("/deleteTask/{id}")
	public String deteteTask(@PathVariable Integer id) {
		Task task1 = taskService.findById(id);
		if(task1 == null) {
			return "Task Not Present";
		}else {
			taskService.deleteById(id);
			return "Task Deleted";
		}
	}
	
}
