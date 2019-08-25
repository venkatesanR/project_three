package com.techmania.designprinciples.assignments.scheduler;

public enum TaskStatusEnum {
    SCHEDULED(1, "Scheduled"),
    IN_PROGRESS(2, "InProgress"),
    PAUSED(3, "Paused"),
    COMPLETED(4, "Completed"),
    FAILED(5, "Error");

    private Integer id;
    private String message;

    TaskStatusEnum(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
