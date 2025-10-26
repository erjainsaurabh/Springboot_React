package com.workflow.workflow.controller;

import com.workflow.workflow.dto.WorkflowRequest;
import com.workflow.workflow.dto.WorkflowResponse;
import com.workflow.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @PostMapping("/start")
    public ResponseEntity<?> startWorkflow(@Valid @RequestBody WorkflowRequest request) {
        WorkflowResponse response = workflowService.startProcess(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/processes")
    public ResponseEntity<?> getAvailableProcesses() {
        Map<String, Object> processes = new HashMap<>();
        processes.put("approval-process", "Simple approval workflow");
        processes.put("review-process", "Document review workflow");
        return ResponseEntity.ok(processes);
    }
    
    @PostMapping("/init")
    public ResponseEntity<?> initializeProcesses() {
        workflowService.createDefaultProcesses();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Default processes initialized");
        return ResponseEntity.ok(response);
    }
}
