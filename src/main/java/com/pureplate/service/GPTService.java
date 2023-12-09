package com.pureplate.service;

import com.pureplate.domain.dtos.ChatRequest;
import com.pureplate.domain.dtos.ChatResponse;
import com.pureplate.utils.GptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {

    @Autowired
    private GptUtil gptUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String chat(String prompt) {

        ChatRequest request = new ChatRequest(model, prompt);

        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        String answer = response.getChoices().get(0).getMessage().getContent();
        return answer;
    }

}
