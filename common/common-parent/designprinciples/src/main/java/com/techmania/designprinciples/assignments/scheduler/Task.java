package com.techmania.designprinciples.assignments.scheduler;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Task<Input, Output> {
    private String taskId;
    private Input input;
    private Function<Input, Output> combinedTask;
    private Consumer<Throwable> onError;
    private Consumer<Optional<Output>> onComplete;

    public Task(Input input, Function combinedTask,
                Consumer<Optional<Output>> completeCallBack,
                Consumer<Throwable> errorCallBack) {
        this.input = input;
        this.combinedTask = combinedTask;
        this.onComplete = completeCallBack;
        this.onError = errorCallBack;
    }

    public String executeTask() {
        Optional<Output> output = null;
        try {
            System.out.println("Started executing task");
            output = Optional.ofNullable((Output) combinedTask.apply(input));
            onComplete.accept(output);
            System.out.println("Completed Task succesfully");
            return TaskStatusEnum.COMPLETED.name();
        } catch (Exception ex) {
            System.out.println("Task execution failed,Calling call back function");
            onError.accept(ex);
            return TaskStatusEnum.FAILED.name();
        }
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
