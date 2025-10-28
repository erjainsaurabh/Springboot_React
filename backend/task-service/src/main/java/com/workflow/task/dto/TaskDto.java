package com.workflow.task.dto;

import java.util.Date;
import java.util.Map;

public class TaskDto {
    
    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private String processDefinitionKey;
    private String businessKey;
    private Date created;
    private Date due;
    private String description;
    private String status; // "pending", "completed", "in_progress"
    private Map<String, Object> variables;
    
    public TaskDto() {}
    
    public TaskDto(String id, String name, String assignee, String processInstanceId, 
                   String processDefinitionKey, String businessKey, Date created, 
                   Date due, String description, String status, Map<String, Object> variables) {
        this.id = id;
        this.name = name;
        this.assignee = assignee;
        this.processInstanceId = processInstanceId;
        this.processDefinitionKey = processDefinitionKey;
        this.businessKey = businessKey;
        this.created = created;
        this.due = due;
        this.description = description;
        this.status = status;
        this.variables = variables;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public Date getDue() {
        return due;
    }
    
    public void setDue(Date due) {
        this.due = due;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
