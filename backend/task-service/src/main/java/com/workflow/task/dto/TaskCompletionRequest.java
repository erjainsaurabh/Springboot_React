package com.workflow.task.dto;

import java.util.Map;

public class TaskCompletionRequest {
    
    private String taskId;
    private Map<String, Object> variables;
    private String comment;
    
    public TaskCompletionRequest() {}
    
    public TaskCompletionRequest(String taskId, Map<String, Object> variables, String comment) {
        this.taskId = taskId;
        this.variables = variables;
        this.comment = comment;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}
