package com.workflow.task.controller;

import com.workflow.task.dto.TaskCompletionRequest;
import com.workflow.task.dto.TaskDto;
import com.workflow.task.service.WorkflowTaskService;
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
    
    @Autowired
    private WorkflowTaskService taskService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasksForUser(@PathVariable String userId) {
        System.out.println("DEBUG: [TaskController] getTasksForUser called for userId: " + userId);
        List<TaskDto> tasks = taskService.getTasksForUser(userId);
        System.out.println("DEBUG: [TaskController] Returning " + tasks.size() + " tasks for user: " + userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks() {
        System.out.println("DEBUG: [TaskController] getAllTasks called");
        List<TaskDto> tasks = taskService.getAllTasks();
        System.out.println("DEBUG: [TaskController] Returning " + tasks.size() + " total tasks");
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
        System.out.println("DEBUG: [TaskController] completeTask called with request: " + request);
        boolean success = taskService.completeTask(request);
        System.out.println("DEBUG: [TaskController] Task completion result: " + success);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "Task completed successfully");
            System.out.println("DEBUG: [TaskController] Returning success response");
        } else {
            response.put("success", false);
            response.put("message", "Failed to complete task");
            System.out.println("DEBUG: [TaskController] Returning failure response");
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{taskId}/claim")
    public ResponseEntity<?> claimTask(@PathVariable String taskId, @RequestParam String userId) {
        System.out.println("DEBUG: [TaskController] claimTask called for taskId: " + taskId + ", userId: " + userId);
        boolean success = taskService.claimTask(taskId, userId);
        System.out.println("DEBUG: [TaskController] Task claim result: " + success);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "Task claimed successfully");
            System.out.println("DEBUG: [TaskController] Returning success response");
        } else {
            response.put("success", false);
            response.put("message", "Failed to claim task");
            System.out.println("DEBUG: [TaskController] Returning failure response");
        }
        return ResponseEntity.ok(response);
    }
}
