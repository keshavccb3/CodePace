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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import jakarta.servlet.http.HttpSession;


@Controller
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
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		
		if (p != null) {
			String email = p.getName();
			User user = userService.findByEmail(email);
			m.addAttribute("user", user);
		}
	}
	
	@GetMapping("/addTaskUI")
	public String add(){
		return "user/addTask";
	}
	@PostMapping("/addTask")
	public String addTask(@ModelAttribute Task task, Principal p, HttpSession session) {
		User user = commonUtil.getLoggedInUserDetails(p);
		Task task1 = taskService.saveTask(task,user);
		if(!ObjectUtils.isEmpty(task1)) {
			session.setAttribute("succMsg", "Task Added Successfully");
		}else {
			session.setAttribute("errorMsg", "Internal Error");
		}
		return "redirect:/user/addTaskUI";
	}
	@GetMapping("/loadEditTask/{id}")
	public String loadEditTask(@PathVariable Integer id, Model m, HttpSession session) {
		Task task1 = taskService.findById(id);
		if(task1 == null) {
			session.setAttribute("errorMsg", "Task not present");
			return "user/allTask";
		}else {
			m.addAttribute("task", task1);
			return "user/updateTask";
		}
	}
	@PostMapping("/updateTask")
	public String updateTask(@ModelAttribute Task task, Principal p, HttpSession session) {
		User user = commonUtil.getLoggedInUserDetails(p);
		Task task1 = taskService.findById(task.getId());
		task1.setDescription(task.getDescription());
		taskService.saveTask(task1, user);
		session.setAttribute("succMsg", "Task Updated Successfully");
		return "redirect:/user/allTask";
	}
	@GetMapping("/allTask")
	public String allTask(Principal p, Model m) {
	    User user = commonUtil.getLoggedInUserDetails(p);
	    List<Task> tasks=  taskService.getAllTaskByUser(user);
	    m.addAttribute("tasks", tasks);
	    return "user/allTask";
	}
	@GetMapping("/deleteTask/{id}")
	public String deteteTask(@PathVariable Integer id, HttpSession session) {
		Task task1 = taskService.findById(id);
		if(task1 == null) {
			return "Task Not Present";
		}else {
			taskService.deleteById(id);
			session.setAttribute("succMsg", "Deleted Successfully");
			return "redirect:/user/allTask";
		}
	}
	@GetMapping("/cfId")
	public String cdId() {
		return "user/cfId";
	}
	@GetMapping("/submitProblem")
	public String submitProblem() {
		return "user/submitProblem";
	}
	@PostMapping("/connectWithCodeforces")
	public String connectWithCodeforces(@ModelAttribute User user, HttpSession session, Model m) {
		String status = userService.isCfHandleExist(user.getCodeForcesId());
		if(status.equals("OK")) {
			int probId = userService.generateRandomProblem();
			if(probId==-1) {
				session.setAttribute("errorMsg", "Internal Error");
				return "redirect:/user/cfId";
			}
			String problem = "https://codeforces.com/problemset/problem/"+probId+"/A";
			m.addAttribute("prob", problem);
			m.addAttribute("codeForcesId",user.getCodeForcesId());
			m.addAttribute("contestId", probId);
			return "user/submitProblem";

		}
		session.setAttribute("errorMsg", "ID Not found");
		return "redirect:/user/cfId";
		
	}
	@GetMapping("/verifyProblem")
	public String verifyProblem(@RequestParam String codeForcesId, @RequestParam Integer contestId, Principal p, HttpSession session) {
	    String status = userService.verifyProblem(codeForcesId,contestId);
	    if("OK".equals(status)) {
	    	User user = commonUtil.getLoggedInUserDetails(p);
	    	User user1 = userService.findByEmail(user.getEmail());
	    	user1.setCodeForcesId(codeForcesId);
	    	User user2 = userService.updateUser(user1);
	    	if(!ObjectUtils.isEmpty(user2)) {
	    		session.setAttribute("succMsg", "Successfully Connected");
	    	}else {
	    		session.setAttribute("errorMsg", "Internal Error");
	    	}
	    	
	    }else if("FAIL".equals(status)) {
	    	session.setAttribute("errorMsg", "Please Verify Again");
	    }
	    session.setAttribute("errorMsg", "Please Verify Again");
	    return "redirect:/user/profile";
	}
	
	@GetMapping("/disconnectWithCodeforces")
	public String disconnectWithCodeforces(Principal p, HttpSession session) {
		User user1 = commonUtil.getLoggedInUserDetails(p);
		user1.setCodeForcesId(null);
		User user2 = userService.updateUser(user1);
		if(!ObjectUtils.isEmpty(user2)) {
			session.setAttribute("succMsg", "Successfully Disconnected");
    	}else {
    		session.setAttribute("errorMsg", "Internal Error");
    	}
		return "redirect:/user/profile";
	}
	@GetMapping("/cfProblemInfo")
	public String cfProblemInfo(Model m, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		List<String> tags = List.of(
		        "2-sat", "binary search", "bitmasks", "brute force", "combinatorics", "constructive algorithms",
		        "data structures", "dfs and similar", "divide and conquer", "dp", "dsu", "expression parsing",
		        "flows", "games", "geometry", "graph matchings", "graphs", "greedy", "hashing", "implementation",
		        "interactive", "math", "matrices", "meet-in-the-middle", "number theory", "probabilities",
		        "schedules", "shortest paths", "sortings", "strings", "ternary search", "trees", "two pointers"
		    );
		m.addAttribute("tags", tags);
		m.addAttribute("problem", new Problem());
		m.addAttribute("user",user);
		return "user/cfProblemInfo";
	}
	@PostMapping("/getCfProblem")
	public String getCfProblem(@ModelAttribute Problem problem, Principal p, Model m, HttpSession session) {
		String prob = problemService.findCfProblem(problem,p);
		List<String> tags = List.of(
		        "2-sat", "binary search", "bitmasks", "brute force", "combinatorics", "constructive algorithms",
		        "data structures", "dfs and similar", "divide and conquer", "dp", "dsu", "expression parsing",
		        "flows", "games", "geometry", "graph matchings", "graphs", "greedy", "hashing", "implementation",
		        "interactive", "math", "matrices", "meet-in-the-middle", "number theory", "probabilities",
		        "schedules", "shortest paths", "sortings", "strings", "ternary search", "trees", "two pointers"
		    );
		
		if ("".equals(problem.getTag())) {
		    problem.setTag(null);
		}
		if(prob.equals("Error")) {
			session.setAttribute("errorMsg", "Internal Error");
		}else {
			m.addAttribute("tags", tags);
			m.addAttribute("prob", prob);
			m.addAttribute("problem", problem);
		}
		return "user/cfProblemInfo";
	}
	@GetMapping("/atCoderProblemInfo")
	public String atCoderProblemInfo(Model m) {
		m.addAttribute("problem", new AtCoderProblem());
		return "user/atCoderProblemInfo";
	}
	@PostMapping("/getAtCoderProblem")
	public String getAtCoderProblem(@ModelAttribute AtCoderProblem problem, Model m, HttpSession session) {
		String prob = problemService.findAtCoderProblem(problem.getDifficulty());
		if(prob.equals("Error")) {
			session.setAttribute("errorMsg","Internal Error");
		}else {
			m.addAttribute("prob", prob);
			m.addAttribute("problem", problem);
		}
		return "user/atCoderProblemInfo";
	}
	@GetMapping("/addNotesUi")
	public String addNotesUi() {
		return "user/addNotes";
	}
	@PostMapping("/addNotes")
	public String addNotes(@ModelAttribute Notes notes, @RequestParam("file") MultipartFile file, Principal p, HttpSession session) throws IOException {
		User user = commonUtil.getLoggedInUserDetails(p);
		String fileName = file!=null ? file.getOriginalFilename() : "default.jpg";
		notes.setFileName(fileName);
		notes.setUser(user);
		Notes saveNotes = notesService.saveNotes(notes);
		if(!ObjectUtils.isEmpty(saveNotes)) {
			File saveFile = new ClassPathResource("static/pdf").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			session.setAttribute("succMsg", "File saved");
		}else {
			session.setAttribute("errorMsg", "Internal saved");
		}
		return "redirect:/user/addNotesUi";
	}
	@GetMapping("/deleteNotes/{id}")
	public String deteteNotes(@PathVariable Integer id, HttpSession session) {
		Notes notes = notesService.findById(id);
		notesService.deleteById(id);
		return "redirect:/user/allNotes";
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
	
	@GetMapping("/allNotes")
	public String allNotes(Model m, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		List<Notes> notes = notesService.findByUser(user);
		m.addAttribute("notes", notes);
		return "user/allNotes";
	}
	
	@GetMapping("/profile")
	public String profile(Model m, Principal p) {
		User user = commonUtil.getLoggedInUserDetails(p);
		m.addAttribute("user", user);
		return "user/profile";
	}
}
