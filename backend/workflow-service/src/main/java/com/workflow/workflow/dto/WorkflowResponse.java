package com.workflow.workflow.dto;

public class WorkflowResponse {
    
    private String processInstanceId;
    private String businessKey;
    private String processKey;
    private String status;
    private String message;
    
    public WorkflowResponse() {}
    
    public WorkflowResponse(String processInstanceId, String businessKey, String processKey, String status, String message) {
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
        this.processKey = processKey;
        this.status = status;
        this.message = message;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    public String getProcessKey() {
        return processKey;
    }
    
    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
