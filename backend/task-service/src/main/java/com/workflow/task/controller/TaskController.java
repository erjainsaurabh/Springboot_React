package com.workflow.task.controller;

import com.workflow.task.dto.TaskCompletionRequest;
import com.workflow.task.dto.TaskDto;
import com.workflow.task.service.WorkflowTaskService;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private WorkflowTaskService taskService;
    
    @Autowired
    private Tracer tracer;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasksForUser(@PathVariable String userId) {
        logger.debug("getTasksForUser called for userId: {}", userId);
        List<TaskDto> tasks = taskService.getTasksForUser(userId);
        logger.debug("Returning {} tasks for user: {}", tasks.size(), userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks() {
        // Add business context to current span
        if (tracer.currentSpan() != null) {
            tracer.currentSpan().tag("business.operation", "get-all-tasks");
            tracer.currentSpan().tag("business.endpoint", "task-listing");
            tracer.currentSpan().tag("business.service", "task-service");
        }
        
        logger.debug("getAllTasks called");
        List<TaskDto> tasks = taskService.getAllTasks();
        logger.debug("Returning {} total tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable String taskId) {
        TaskDto task = taskService.getTaskById(taskId);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Task not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/complete")
    public ResponseEntity<?> completeTask(@RequestBody TaskCompletionRequest request) {
        // Add business context to current span
        if (tracer.currentSpan() != null) {
            tracer.currentSpan().tag("business.operation", "complete-task");
            tracer.currentSpan().tag("business.endpoint", "task-completion");
            tracer.currentSpan().tag("business.service", "task-service");
            tracer.currentSpan().tag("business.taskId", request.getTaskId());
        }
        
        logger.debug("completeTask called with request: {}", request);
        boolean success = taskService.completeTask(request);
        logger.debug("Task completion result: {}", success);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "Task completed successfully");
            logger.debug("Returning success response");
        } else {
            response.put("success", false);
            response.put("message", "Failed to complete task");
            logger.debug("Returning failure response");
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/claim")
    public ResponseEntity<?> claimTask(@PathVariable String taskId, @RequestParam String userId) {
        logger.debug("claimTask called for taskId: {}, userId: {}", taskId, userId);
        boolean success = taskService.claimTask(taskId, userId);
        logger.debug("Task claim result: {}", success);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "Task claimed successfully");
            logger.debug("Returning success response");
        } else {
            response.put("success", false);
            response.put("message", "Failed to claim task");
            logger.debug("Returning failure response");
        }
        return ResponseEntity.ok(response);
    }
}
