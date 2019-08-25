package com.techmania.designprinciples.assignments.scheduler;

public class SchedulerResponse {
    private String taskId;
    private String status;
    private String message;

    public SchedulerResponse(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
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

    @Override
    public String toString() {
        return "{ " + taskId + ", " + getStatus() + ", " + getMessage() + " }";
    }
}
