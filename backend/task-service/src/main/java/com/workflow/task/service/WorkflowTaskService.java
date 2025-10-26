package com.workflow.task.service;

import com.workflow.task.dto.TaskCompletionRequest;
import com.workflow.task.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowTaskService {
    
    @Value("${camunda.rest.url:http://localhost:8080/engine-rest}")
    private String camundaRestUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<TaskDto> getTasksForUser(String userId) {
        try {
            System.out.println("DEBUG: camundaRestUrl = " + camundaRestUrl);
            String url = camundaRestUrl + "/task?assignee=" + userId;
            System.out.println("DEBUG: Making request to URL: " + url);
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            System.out.println("DEBUG: Response status: " + response.getStatusCode());
            System.out.println("DEBUG: Response body: " + response.getBody());
            
            List<TaskDto> taskDtos = new ArrayList<>();
            if (response.getBody() != null) {
                for (Map<String, Object> taskData : response.getBody()) {
                    TaskDto dto = convertToDto(taskData);
                    taskDtos.add(dto);
                }
            }
            
            return taskDtos;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<TaskDto> getAllTasks() {
        try {
            System.out.println("DEBUG: [getAllTasks] Starting to fetch all tasks");
            String url = camundaRestUrl + "/task";
            System.out.println("DEBUG: [getAllTasks] Making request to URL: " + url);
            
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            System.out.println("DEBUG: [getAllTasks] Response status: " + response.getStatusCode());
            System.out.println("DEBUG: [getAllTasks] Number of tasks found: " + (response.getBody() != null ? response.getBody().length : 0));
            
            List<TaskDto> taskDtos = new ArrayList<>();
            if (response.getBody() != null) {
                for (Map<String, Object> taskData : response.getBody()) {
                    System.out.println("DEBUG: [getAllTasks] Processing task: " + taskData.get("id") + " - " + taskData.get("name"));
                    TaskDto dto = convertToDto(taskData);
                    taskDtos.add(dto);
                }
            }
            
            System.out.println("DEBUG: [getAllTasks] Returning " + taskDtos.size() + " tasks");
            return taskDtos;
        } catch (Exception e) {
            System.out.println("DEBUG: [getAllTasks] Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public TaskDto getTaskById(String taskId) {
        try {
            System.out.println("DEBUG: [getTaskById] Fetching task with ID: " + taskId);
            String url = camundaRestUrl + "/task/" + taskId;
            System.out.println("DEBUG: [getTaskById] Making request to URL: " + url);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            System.out.println("DEBUG: [getTaskById] Response status: " + response.getStatusCode());
            
            if (response.getBody() != null) {
                System.out.println("DEBUG: [getTaskById] Task found: " + response.getBody().get("name"));
                return convertToDto(response.getBody());
            }
            
            System.out.println("DEBUG: [getTaskById] No task found with ID: " + taskId);
            return null;
        } catch (Exception e) {
            System.out.println("DEBUG: [getTaskById] Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean completeTask(TaskCompletionRequest request) {
        try {
            System.out.println("DEBUG: [completeTask] Starting task completion for task ID: " + request.getTaskId());
            System.out.println("DEBUG: [completeTask] Variables provided: " + request.getVariables());
            
            String url = camundaRestUrl + "/task/" + request.getTaskId() + "/complete";
            System.out.println("DEBUG: [completeTask] Making request to URL: " + url);
            
            Map<String, Object> requestBody = new java.util.HashMap<>();
            if (request.getVariables() != null) {
                // Camunda expects variables in a specific format
                Map<String, Object> camundaVariables = new java.util.HashMap<>();
                for (Map.Entry<String, Object> entry : request.getVariables().entrySet()) {
                    Map<String, Object> variableValue = new java.util.HashMap<>();
                    variableValue.put("value", entry.getValue());
                    camundaVariables.put(entry.getKey(), variableValue);
                }
                requestBody.put("variables", camundaVariables);
                System.out.println("DEBUG: [completeTask] Request body with variables: " + requestBody);
            } else {
                System.out.println("DEBUG: [completeTask] No variables provided, sending empty request body");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            System.out.println("DEBUG: [completeTask] Sending POST request to complete task...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            System.out.println("DEBUG: [completeTask] Response status: " + response.getStatusCode());
            System.out.println("DEBUG: [completeTask] Response body: " + response.getBody());
            
            System.out.println("DEBUG: [completeTask] Task completion successful!");
            return true;
        } catch (Exception e) {
            System.out.println("DEBUG: [completeTask] Exception occurred: " + e.getMessage());
            System.out.println("DEBUG: [completeTask] Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean claimTask(String taskId, String userId) {
        try {
            System.out.println("DEBUG: [claimTask] Starting task claim for task ID: " + taskId + " by user: " + userId);
            
            String url = camundaRestUrl + "/task/" + taskId + "/claim";
            System.out.println("DEBUG: [claimTask] Making request to URL: " + url);
            
            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("userId", userId);
            System.out.println("DEBUG: [claimTask] Request body: " + requestBody);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            System.out.println("DEBUG: [claimTask] Sending POST request to claim task...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            System.out.println("DEBUG: [claimTask] Response status: " + response.getStatusCode());
            System.out.println("DEBUG: [claimTask] Response body: " + response.getBody());
            
            System.out.println("DEBUG: [claimTask] Task claim successful!");
            return true;
        } catch (Exception e) {
            System.out.println("DEBUG: [claimTask] Exception occurred: " + e.getMessage());
            System.out.println("DEBUG: [claimTask] Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }
    
    private TaskDto convertToDto(Map<String, Object> taskData) {
        System.out.println("DEBUG: [convertToDto] Converting task data: " + taskData);
        
        TaskDto dto = new TaskDto();
        dto.setId((String) taskData.get("id"));
        dto.setName((String) taskData.get("name"));
        dto.setAssignee((String) taskData.get("assignee"));
        dto.setProcessInstanceId((String) taskData.get("processInstanceId"));
        dto.setProcessDefinitionKey((String) taskData.get("processDefinitionId"));
        
        System.out.println("DEBUG: [convertToDto] Basic task info - ID: " + dto.getId() + ", Name: " + dto.getName() + ", Assignee: " + dto.getAssignee());
        
        // Parse created date
        String createdStr = (String) taskData.get("created");
        if (createdStr != null) {
            try {
                // Handle different date formats from Camunda
                if (createdStr.contains("+")) {
                    // Format: 2025-10-23T02:16:10.194+0000 -> convert to 2025-10-23T02:16:10.194+00:00
                    String normalizedDate = createdStr.replaceAll("\\+0000$", "+00:00");
                    dto.setCreated(new Date(java.time.OffsetDateTime.parse(normalizedDate).toInstant().toEpochMilli()));
                } else {
                    // Format: 2025-10-23T02:16:10.194Z
                    dto.setCreated(new Date(java.time.Instant.parse(createdStr).toEpochMilli()));
                }
                System.out.println("DEBUG: [convertToDto] Created date parsed successfully: " + dto.getCreated());
            } catch (Exception e) {
                System.out.println("DEBUG: [convertToDto] Error parsing created date: " + e.getMessage());
                System.out.println("DEBUG: [convertToDto] Date string was: " + createdStr);
            }
        } else {
            System.out.println("DEBUG: [convertToDto] No created date found");
        }
        
        // Parse due date
        String dueStr = (String) taskData.get("due");
        if (dueStr != null) {
            try {
                dto.setDue(new Date(java.time.Instant.parse(dueStr).toEpochMilli()));
                System.out.println("DEBUG: [convertToDto] Due date parsed successfully: " + dto.getDue());
            } catch (Exception e) {
                System.out.println("DEBUG: [convertToDto] Error parsing due date: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: [convertToDto] No due date found");
        }
        
        dto.setDescription((String) taskData.get("description"));
        
        // Get task variables
        try {
            String taskId = (String) taskData.get("id");
            System.out.println("DEBUG: [convertToDto] Fetching variables for task ID: " + taskId);
            String url = camundaRestUrl + "/task/" + taskId + "/variables";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null) {
                dto.setVariables(response.getBody());
                System.out.println("DEBUG: [convertToDto] Variables fetched successfully: " + response.getBody());
            } else {
                System.out.println("DEBUG: [convertToDto] No variables found for task");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: [convertToDto] Error fetching variables: " + e.getMessage());
        }
        
        System.out.println("DEBUG: [convertToDto] Task DTO conversion completed for task: " + dto.getName());
        return dto;
    }
}
