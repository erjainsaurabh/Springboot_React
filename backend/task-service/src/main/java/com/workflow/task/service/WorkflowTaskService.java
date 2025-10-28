package com.workflow.task.service;

import com.workflow.task.dto.TaskCompletionRequest;
import com.workflow.task.dto.TaskDto;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowTaskService.class);
    
    @Value("${camunda.rest.url:http://localhost:8080/engine-rest}")
    private String camundaRestUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Tracer tracer;
    
    @NewSpan("get-tasks-for-user")
    public List<TaskDto> getTasksForUser(@SpanTag("userId") String userId) {
        try {
            logger.debug("camundaRestUrl: {}", camundaRestUrl);
            String url = camundaRestUrl + "/task?assignee=" + userId;
            logger.debug("Making request to URL: {}", url);
            
            // Create a manual span for the Camunda API call
            Span span = tracer.nextSpan()
                .name("camunda-get-tasks-for-user")
                .tag("service", "camunda")
                .tag("operation", "get-tasks-for-user")
                .tag("userId", userId)
                .tag("url", url)
                .start();
            
            ResponseEntity<List<Map<String, Object>>> response;
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
                response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
            } finally {
                span.end();
            }
            logger.debug("Response status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());
            
            List<TaskDto> taskDtos = new ArrayList<>();
            List<Map<String, Object>> responseBody = response.getBody();
            if (responseBody != null) {
                for (Map<String, Object> taskData : responseBody) {
                    TaskDto dto = convertToDto(taskData);
                    taskDtos.add(dto);
                }
            }
            
            return taskDtos;
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @NewSpan("get-all-tasks")
    public List<TaskDto> getAllTasks() {
        try {
            // Add business context to the current span
            if (tracer.currentSpan() != null) {
                tracer.currentSpan().tag("business.operation", "get-all-tasks");
                tracer.currentSpan().tag("business.service", "task-service");
                tracer.currentSpan().tag("business.scope", "all-users");
            }
            
            logger.debug("Starting to fetch all tasks");
            String url = camundaRestUrl + "/task";
            logger.debug("Making request to URL: {}", url);
            
            // Create a manual span for the Camunda API call
            Span span = tracer.nextSpan()
                .name("camunda-get-tasks")
                .tag("service", "camunda")
                .tag("operation", "get-tasks")
                .tag("url", url)
                .start();
            
            ResponseEntity<List<Map<String, Object>>> response;
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
                response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
            } finally {
                span.end();
            }
            logger.debug("Response status: {}", response.getStatusCode());
            List<Map<String, Object>> responseBody = response.getBody();
            logger.debug("Number of tasks found: {}", (responseBody != null ? responseBody.size() : 0));
            
            List<TaskDto> taskDtos = new ArrayList<>();
            if (responseBody != null) {
                for (Map<String, Object> taskData : responseBody) {
                    logger.debug("Processing task: {} - {}", taskData.get("id"), taskData.get("name"));
                    TaskDto dto = convertToDto(taskData);
                    taskDtos.add(dto);
                }
            }
            
            logger.debug("Returning {} tasks", taskDtos.size());
            return taskDtos;
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @NewSpan("get-task-by-id")
    public TaskDto getTaskById(@SpanTag("taskId") String taskId) {
        try {
            // Add business context to the current span
            if (tracer.currentSpan() != null) {
                tracer.currentSpan().tag("business.operation", "get-task-by-id");
                tracer.currentSpan().tag("business.taskId", taskId);
                tracer.currentSpan().tag("business.service", "task-service");
            }
            
            logger.debug("Fetching task with ID: {}", taskId);
            String url = camundaRestUrl + "/task/" + taskId;
            logger.debug("Making request to URL: {}", url);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            logger.debug("Response status: {}", response.getStatusCode());
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                logger.debug("Task found: {}", responseBody.get("name"));
                return convertToDto(responseBody);
            }
            
            logger.debug("No task found with ID: {}", taskId);
            return null;
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public boolean completeTask(TaskCompletionRequest request) {
        try {
            // Add business context to the current span
            if (tracer.currentSpan() != null) {
                tracer.currentSpan().tag("business.operation", "complete-task");
                tracer.currentSpan().tag("business.taskId", request.getTaskId());
                tracer.currentSpan().tag("business.service", "task-service");
                if (request.getVariables() != null) {
                    tracer.currentSpan().tag("business.variables", request.getVariables().toString());
                }
            }
            
            // Log trace ID for correlation
            String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "no-trace";
            logger.info("Starting task completion for task ID: {} with traceId: {}", request.getTaskId(), traceId);
            
            logger.debug("Variables provided: {}", request.getVariables());
            
            String url = camundaRestUrl + "/task/" + request.getTaskId() + "/complete";
            logger.debug("Making request to URL: {}", url);
            
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
                logger.debug("Request body with variables: {}", requestBody);
            } else {
                logger.debug("No variables provided, sending empty request body");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            logger.debug("Sending POST request to complete task...");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            logger.debug("Response status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());
            
            logger.info("Task completion successful for task ID: {} with traceId: {}", request.getTaskId(), traceId);
            return true;
        } catch (Exception e) {
            logger.error("Exception occurred: {}, type: {}", e.getMessage(), e.getClass().getSimpleName(), e);
            return false;
        }
    }
    
    public boolean claimTask(String taskId, String userId) {
        try {
            logger.debug("Starting task claim for task ID: {} by user: {}", taskId, userId);
            
            String url = camundaRestUrl + "/task/" + taskId + "/claim";
            logger.debug("Making request to URL: {}", url);
            
            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("userId", userId);
            logger.debug("Request body: {}", requestBody);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            logger.debug("Sending POST request to claim task...");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            logger.debug("Response status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());
            
            logger.debug("Task claim successful!");
            return true;
        } catch (Exception e) {
            logger.error("Exception occurred: {}, type: {}", e.getMessage(), e.getClass().getSimpleName(), e);
            return false;
        }
    }
    
    private TaskDto convertToDto(Map<String, Object> taskData) {
        logger.debug("Converting task data: {}", taskData);
        
        TaskDto dto = new TaskDto();
        dto.setId((String) taskData.get("id"));
        dto.setName((String) taskData.get("name"));
        dto.setAssignee((String) taskData.get("assignee"));
        dto.setProcessInstanceId((String) taskData.get("processInstanceId"));
        dto.setProcessDefinitionKey((String) taskData.get("processDefinitionId"));
        
        logger.debug("Basic task info - ID: {}, Name: {}, Assignee: {}", dto.getId(), dto.getName(), dto.getAssignee());
        
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
                logger.debug("Created date parsed successfully: {}", dto.getCreated());
            } catch (Exception e) {
                logger.warn("Error parsing created date: {}, Date string was: {}", e.getMessage(), createdStr);
            }
        } else {
            logger.debug("No created date found");
        }
        
        // Parse due date
        String dueStr = (String) taskData.get("due");
        if (dueStr != null) {
            try {
                dto.setDue(new Date(java.time.Instant.parse(dueStr).toEpochMilli()));
                logger.debug("Due date parsed successfully: {}", dto.getDue());
            } catch (Exception e) {
                logger.warn("Error parsing due date: {}", e.getMessage());
            }
        } else {
            logger.debug("No due date found");
        }
        
        dto.setDescription((String) taskData.get("description"));
        
        // Set task status - all tasks from Camunda are "pending" by default
        // In a real system, you might check if task is completed based on other criteria
        dto.setStatus("pending");
        
        // Get task variables
        try {
            String taskId = (String) taskData.get("id");
            logger.debug("Fetching variables for task ID: {}", taskId);
            String url = camundaRestUrl + "/task/" + taskId + "/variables";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            if (response.getBody() != null) {
                dto.setVariables(response.getBody());
                logger.debug("Variables fetched successfully: {}", response.getBody());
            } else {
                logger.debug("No variables found for task");
            }
        } catch (Exception e) {
            logger.warn("Error fetching variables: {}", e.getMessage());
        }
        
        logger.debug("Task DTO conversion completed for task: {}", dto.getName());
        return dto;
    }
}
