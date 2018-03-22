package com.jmodules.threads;

public class ThreadDetail {
	public synchronized void printThreadDetails(String name) {
		System.out.println("User Output :" + name);
		System.out.println("Thread ID :" + Thread.currentThread().getId());
		System.out.println("Thread Name :" + Thread.currentThread().getName());
		System.out.println("Thread Priority :" + Thread.currentThread().getPriority());
		System.out.println(" Thead Time (Name):" + Thread.currentThread().getName() + " \t" + System.nanoTime());
	}
}
