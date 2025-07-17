package com.code.pace.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.code.pace.common.CommonUtil;
import com.code.pace.model.AtCoderProblem;
import com.code.pace.model.Notes;
import com.code.pace.model.Problem;
import com.code.pace.model.Task;
import com.code.pace.model.User;
import com.code.pace.service.NotesService;
import com.code.pace.service.ProblemService;
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
	private ProblemService problemService;
	@Autowired
	private NotesService notesService;
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
//	@DeleteMapping("/deleteTask/{id}")
//	public String deteteTask(@PathVariable Integer id) {
//		Task task1 = taskService.findById(id);
//		if(task1 == null) {
//			return "Task Not Present";
//		}else {
//			taskService.deleteById(id);
//			return "Task Deleted";
//		}
//	}
	@PostMapping("/connectWithCodeforces")
	public String connectWithCodeforces(@RequestBody User user) {
		String status = userService.isCfHandleExist(user.getCodeForcesId());
		if(status.equals("OK")) {
			int probId = userService.generateRandomProblem();
			if(probId==-1) {
				return "Internal Error";
			}
			String problem = "https://codeforces.com/problemset/problem/"+probId+"/A";
			return problem;

		}
		return "ID Not found";
		
	}
	@GetMapping("/verifyProblem")
	public String verifyProblem(@RequestParam String codeForcesId, @RequestParam Integer contestId, Principal p) {
	    String status = userService.verifyProblem(codeForcesId,contestId);
	    if("OK".equals(status)) {
	    	User user = commonUtil.getLoggedInUserDetails(p);
	    	User user1 = userService.findByEmail(user.getEmail());
	    	user1.setCodeForcesId(codeForcesId);
	    	User user2 = userService.updateUser(user1);
	    	if(!ObjectUtils.isEmpty(user2)) {
	    		return "Succefully Connected";
	    	}else {
	    		return "Internal error";
	    	}
	    	
	    }else if("FAIL".equals(status)) {
	    	return "Please Verify Again";
	    }
	    return "Internal Error";
	}
	
	@GetMapping("/disconnectWithCodeforces")
	public String disconnectWithCodeforces(@RequestBody User user, Principal p) {
		User user1 = commonUtil.getLoggedInUserDetails(p);
		user1.setCodeForcesId(null);
		User user2 = userService.updateUser(user1);
		if(!ObjectUtils.isEmpty(user2)) {
    		return "Succefully Disconnected";
    	}else {
    		return "Internal error";
    	}
	}
	
	@PostMapping("/getCfProblem")
	public String getCfProblem(@RequestBody Problem problem, Principal p) {
		String prob = problemService.findCfProblem(problem,p);
		if(prob.equals("Error")) {
			return "Internal Error";
		}
		return prob;
	}
	@PostMapping("/getAtCoderProblem")
	public String getAtCoderProblem(@RequestBody AtCoderProblem problem) {
		String prob = problemService.findAtCoderProblem(problem.getDifficulty());
		if(prob.equals("Error")) {
			return "Internal Error";
		}
		return prob;
	}
	@PostMapping("/addNotes")
	public String addNotes(@ModelAttribute Notes notes, @RequestParam("file") MultipartFile file, Principal p) throws IOException {
		User user = commonUtil.getLoggedInUserDetails(p);
		String fileName = file!=null ? file.getOriginalFilename() : "default.jpg";
		notes.setFileName(fileName);
		notes.setUser(user);
		Notes saveNotes = notesService.saveNotes(notes);
		if(!ObjectUtils.isEmpty(saveNotes)) {
			File saveFile = new ClassPathResource("static/pdf").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			return "File saved";
		}else {
			return "Internal error";
		}
	}
	@DeleteMapping("/deleteNotes/{id}")
	public String deteteNotes(@PathVariable Integer id) {
		Notes notes = notesService.findById(id);
		if(notes == null) {
			return "Notes Not Present";
		}else {
			notesService.deleteById(id);
			return "Notes Deleted";
		}
	}
	@PutMapping("/updateNotes/{id}")
	public String updateNotes(@PathVariable Integer id, @RequestBody Notes notes, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		Notes notes1 = notesService.findById(id);
		if(notes1 == null) {
			return "Notes Not Present";
		}else {
			notes1.setTitle(notes.getTitle());
			notesService.saveNotes(notes1);
			return "Notes Updated";
		}
	}
}
