package com.techmania.designprinciples.assignments;

import com.techmania.designprinciples.assignments.scheduler.IScheduler;
import com.techmania.designprinciples.assignments.scheduler.Task;
import com.techmania.designprinciples.assignments.scheduler.TaskBuilder;
import com.techmania.designprinciples.assignments.services.ServiceClient;

public class DriverClass {
    public static void main(String[] args) {
        IScheduler scheduler = ServiceClient.getProxyService(IScheduler.class);
        int i = 0;
        while (i < 2) {
            Task<String, String> task = TaskBuilder.<String, String>builder().
                    setInput("Hello").
                    onRead((input) -> {
                        System.out.println("Executing Read function, With Input:: " + input);
                        return "STEP1";
                    }).
                    onProcess((input) -> {
                        System.out.println("Executing Process function, With Input:: " + input);
                        return "STEP2";
                    }).
                    onWrite((input) -> {
                        System.out.println("Executing Write function, With Input:: " + input);
                        return "STEP3";
                    }).
                    onComplete((output) -> {
                        System.out.println("Executing complete function, With Output:: " + output);
                    }).
                    onError((e) -> {
                        System.out.println("Executing error block");
                    }).build();
            scheduler.addTask(task);
            i++;
        }
    }
}
