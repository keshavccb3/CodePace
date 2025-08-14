package com.code.pace.serviceImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.code.pace.common.CommonUtil;
import com.code.pace.model.Problem;
import com.code.pace.service.ProblemService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class ProblemServiceImpl implements ProblemService{
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	WebClient webClient = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)).build()).build();

	@Override
	public String findCfProblem(Problem problem, Principal principal) {
		
		String url = "https://codeforces.com/api/problemset.problems";
		String response1 = webClient
				.get()
				.uri(url)
				.header("Content-Type", "application/json")
				.retrieve()
				.bodyToMono(String.class)
				.block();
		List<Integer> totalProblemId = new ArrayList<>();
		List<String> totalProblemIndex = new ArrayList<>();
		try {
			ObjectMapper mapper1 = new ObjectMapper();
			JsonNode node1 = mapper1.readTree(response1);
			JsonNode problems =  node1.path("result").path("problems");
			for(JsonNode p : problems) {
				if (p.has("rating")) {
			        int currentRating = p.path("rating").asInt();
			        
			        if (problem.getRating() == currentRating) {
			        	if(problem.getTag()!=null && p.has("tags")) {
			        		for(JsonNode t : p.path("tags")) {
			        			if(problem.getTag().equalsIgnoreCase(t.asText())) {
			        				int contestId = p.path("contestId").asInt();
						            String index = p.path("index").asText();
						            totalProblemId.add(contestId);
						            totalProblemIndex.add(index);
						            break;
			        			}
			        		}
			        	}else {
			        		int contestId = p.path("contestId").asInt();
				            String index = p.path("index").asText();
				            totalProblemId.add(contestId);
				            totalProblemIndex.add(index);
			        	}
			            
			        }
			    }
				
			}
		}catch(Exception e) {
			return "Error";
		}
		String handle = commonUtil.getLoggedInUserDetails(principal).getCodeForcesId();
		List<Integer> resultProblemId = new ArrayList<>();
		List<String> resultProblemIndex = new ArrayList<>();
		if(handle != null) {
			String url1 = "https://codeforces.com/api/user.status?handle="+handle;
			String response2 = webClient
					.get()
					.uri(url1)
					.header("Content-Type", "application/json")
					.retrieve()
					.bodyToMono(String.class)
					.block();
			List<Integer> solvedProblemId = new ArrayList<>();
			List<String> solvedProblemIndex = new ArrayList<>();
			try {
				ObjectMapper mapper1 = new ObjectMapper();
				JsonNode node1 = mapper1.readTree(response1);
				JsonNode results =  node1.path("result");
				for(JsonNode r : results) {
					JsonNode p = r.path("problem");
					if (p.has("rating")) {
				        int currentRating = p.path("rating").asInt();
				        
				        if (problem.getRating() == currentRating) {
				        	if(problem.getTag()!=null && p.has("tags")) {
				        		for(JsonNode t : p.path("tags")) {
				        			if(problem.getTag().equalsIgnoreCase(t.asText())) {
				        				if(r.path("verdict").asText().equals("OK")) {
				        					Integer contestId = p.path("contestId").asInt();
								            String index = p.path("index").asText();
								            solvedProblemId.add(contestId);
								            solvedProblemIndex.add(index);
								            break;
				        				}
				        			}
				        		}
				        	}else {
				        		if(r.path("verdict").asText().equals("OK")) {
		        					Integer contestId = p.path("contestId").asInt();
						            String index = p.path("index").asText();
						            solvedProblemId.add(contestId);
						            solvedProblemIndex.add(index);
		        				}
				        	}
				            
				        }
				    }
					
				}
			}catch(Exception e) {
				return "Error";
			}
			
			for(int i = 0; i<totalProblemId.size(); i++) {
				boolean isSolved = false;
				for (int j = 0; j < solvedProblemId.size(); j++) {
					
				    if (totalProblemId.get(i).equals(solvedProblemId.get(j)) &&
				        totalProblemIndex.get(i).equals(solvedProblemIndex.get(j))) {
				        isSolved = true;
				    }
				}
				if (!isSolved) {
				    resultProblemId.add(totalProblemId.get(i));
				    resultProblemIndex.add(totalProblemIndex.get(i));
				}
			}
			
		}else {
			resultProblemIndex = totalProblemIndex;
			resultProblemId = totalProblemId;
		}
		
		if(resultProblemIndex.size()>0) {
			Random rand = new Random();
			int i =  rand.nextInt(resultProblemIndex.size());
			
			return "https://codeforces.com/problemset/problem/"+resultProblemId.get(i)+"/"+resultProblemIndex.get(i);
		}else {
			return "Empty";
		}
		
	}

	@Override
	public String findAtCoderProblem(String difficulty) {
		List<String> problemId = new ArrayList<>();
		List<String> contestId = new ArrayList<>();
		String url = "https://kenkoooo.com/atcoder/resources/problem-models.json";
		String response = webClient
							.get()
							.uri(url)
							.retrieve()
							.bodyToMono(String.class)
							.block();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
			while(fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();
				String probId = entry.getKey();
				String contId = probId.split("_")[0];
				JsonNode problemData = entry.getValue();
				if(problemData.path("difficulty")!=null) {
					Integer diff = problemData.path("difficulty").asInt();
					if(diff<=1000 && difficulty.equals("easy")) {
						problemId.add(probId);
						contestId.add(contId);
					}else if(diff<=2000 && diff>1000 && difficulty.equals("medium")) {
						problemId.add(probId);
						contestId.add(contId);
					}else if (diff>2000 && difficulty.equals("hard")){
						problemId.add(probId);
						contestId.add(contId);
					}
				}
			}
			if(problemId.size()>0) {
				Random rand = new Random();
				int i =  rand.nextInt(problemId.size());
				
				return "https://atcoder.jp/contests/"+contestId.get(i)+"/tasks/"+problemId.get(i);
			}else {
				return "Empty";
			}
			
		}catch(Exception e) {
			return "Error";
		}
	}

}
