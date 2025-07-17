package com.code.pace.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.code.pace.model.Problem;

@Service
public interface ProblemService {

	String findCfProblem(Problem problem, Principal principal);

	String findAtCoderProblem(String difficulty);

}
