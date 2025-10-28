package com.workflow.workflow.service;

import com.workflow.workflow.dto.WorkflowRequest;
import com.workflow.workflow.dto.WorkflowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkflowService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);
    
    @Value("${camunda.rest.url:http://localhost:8080/engine-rest}")
    private String camundaRestUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public WorkflowResponse startProcess(WorkflowRequest request) {
        try {
            logger.info("Starting process via REST API: {}", request.getProcessKey());
            
            // Prepare variables for Camunda REST API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("businessKey", request.getBusinessKey());
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("userId", Map.of("value", request.getUserId(), "type", "String"));
            variables.put("description", Map.of("value", request.getDescription(), "type", "String"));
            requestBody.put("variables", variables);
            
            String url = camundaRestUrl + "/process-definition/key/" + request.getProcessKey() + "/start";
            logger.debug("Making request to URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            logger.debug("Response status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());
            
            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return new WorkflowResponse(
                    (String) responseBody.get("id"),
                    (String) responseBody.get("businessKey"),
                    (String) responseBody.get("processDefinitionId"),
                    "STARTED",
                    "Process started successfully"
                );
            } else {
                return new WorkflowResponse(
                    null,
                    request.getBusinessKey(),
                    request.getProcessKey(),
                    "ERROR",
                    "No response from Camunda"
                );
            }
        } catch (Exception e) {
            logger.error("Error starting process: {}", e.getMessage(), e);
            return new WorkflowResponse(
                null,
                request.getBusinessKey(),
                request.getProcessKey(),
                "ERROR",
                "Failed to start process: " + e.getMessage()
            );
        }
    }
    
    public void createDefaultProcesses() {
        // BPMN files are deployed to the central Camunda instance
        // This method can be used for any initialization logic
        logger.info("Default processes are managed by central Camunda instance");
    }
}
