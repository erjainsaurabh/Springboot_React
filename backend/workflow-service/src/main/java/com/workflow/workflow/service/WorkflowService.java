package com.workflow.workflow.service;

import com.workflow.workflow.dto.WorkflowRequest;
import com.workflow.workflow.dto.WorkflowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${camunda.rest.url:http://localhost:8080/engine-rest}")
    private String camundaRestUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public WorkflowResponse startProcess(WorkflowRequest request) {
        try {
            System.out.println("DEBUG: Starting process via REST API: " + request.getProcessKey());
            
            // Prepare variables for Camunda REST API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("businessKey", request.getBusinessKey());
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("userId", Map.of("value", request.getUserId(), "type", "String"));
            variables.put("description", Map.of("value", request.getDescription(), "type", "String"));
            requestBody.put("variables", variables);
            
            String url = camundaRestUrl + "/process-definition/key/" + request.getProcessKey() + "/start";
            System.out.println("DEBUG: Making request to URL: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            System.out.println("DEBUG: Response status: " + response.getStatusCode());
            System.out.println("DEBUG: Response body: " + response.getBody());
            
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
            System.err.println("DEBUG: Error starting process: " + e.getMessage());
            e.printStackTrace();
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
        System.out.println("DEBUG: Default processes are managed by central Camunda instance");
    }
}
