package com.code.pace.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.code.pace.model.Task;
import com.code.pace.model.User;
import com.code.pace.repository.UserRepository;
import com.code.pace.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private WebClient.Builder webClientBuilder;
	WebClient webClient = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)).build()).build();

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

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public String isCfHandleExist(String codeForcesId) {
		String url = "https://codeforces.com/api/user.info?handles=" + codeForcesId;
		try {
			String response = webClient.get().uri(url).header("Content-Type", "application/json").retrieve()
					.bodyToMono(String.class).block();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("status").asText();
		} catch (WebClientResponseException e) {
			return "FAILED";
		} catch (Exception e) {
			return "ERROR: " + e.getMessage();
		}
	}

	@Override
	public int generateRandomProblem() {
		String url = "https://codeforces.com/api/problemset.problems";
		try {
			String response = webClient.get().uri(url).header("Content-Type", "application/json").retrieve()
					.bodyToMono(String.class).block();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			List<Integer> l = new ArrayList<>();
			JsonNode problems = rootNode.path("result").path("problems");
			for (JsonNode p : problems) {
				String i = p.path("index").asText();
				if ("A".equals(i)) {
					l.add(p.path("contestId").asInt());
				}
			}
			Random rand = new Random();
			return l.get(rand.nextInt(l.size()));
		} catch (Exception e) {
			return -1;
		}

	}

	@Override
	public String verifyProblem(String codeForcesId, Integer contestId) {
		String url = "https://codeforces.com/api/user.status?handle=" + codeForcesId;
		try {
			String response = webClient.get().uri(url).header("Content-Type", "application/json").retrieve()
					.bodyToMono(String.class).block();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			JsonNode latestSubmission = rootNode.path("result").get(0);

			int cId = latestSubmission.path("contestId").asInt();
			String index = latestSubmission.path("problem").path("index").asText();
			String verdict = latestSubmission.path("verdict").asText();

			if (cId == contestId && "A".equalsIgnoreCase(index.trim())
					&& "COMPILATION_ERROR".equalsIgnoreCase(verdict.trim())) {
				return "OK";
			} else {
				return "FAIL";
			}
		} catch (Exception e) {
			return "Error";
		}
	}

	@Override
	public User updateUser(User user1) {
		return userRepository.save(user1);
	}

}
