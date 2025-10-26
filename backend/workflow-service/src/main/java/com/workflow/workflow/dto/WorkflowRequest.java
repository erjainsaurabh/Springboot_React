package com.workflow.workflow.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkflowRequest {
    
    @NotBlank
    private String processKey;
    
    @NotBlank
    private String businessKey;
    
    private String userId;
    
    private String description;
    
    public WorkflowRequest() {}
    
    public WorkflowRequest(String processKey, String businessKey, String userId, String description) {
        this.processKey = processKey;
        this.businessKey = businessKey;
        this.userId = userId;
        this.description = description;
    }
    
    public String getProcessKey() {
        return processKey;
    }
    
    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
