package com.datastructures.queue;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SyncQueueTest {
    private static final long timeout = 10;
    private volatile String currentRequestId = null;
    private final Object syncLock = new Object();
    private final SynchronousQueue<Response> queue = new SynchronousQueue<>();

    public Response request(Request request) throws InterruptedException {
        synchronized (syncLock) {
            this.currentRequestId = request.getRequestID();
        }
        //Wait for response
        try {
            queue.poll(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException iex) {
            System.out.println("Interrupted exception");
        }
        return null;
    }

    public void offer(Response response) throws InterruptedException {
        System.out.println(String.format("Started calling [time:%s,system:%s]", System.currentTimeMillis(),
                response.system));
        synchronized (syncLock) {
            if (response.getRequestID().equals(currentRequestId)) {
                System.out.println("Passed system Unlocked request ID: " + response.system);

                long startTime = System.currentTimeMillis();
                System.out.println("Currently locked thread" + Thread.currentThread().getName());
                if (queue.offer(response,
                        timeout, TimeUnit.SECONDS)) {
                    System.out.println("Queue Offred By: " + response.system);
                }
                System.out.println("Unlocked thread:" + Thread.currentThread().getName());
                System.out.println("Offer Running time: " + (System.currentTimeMillis() - startTime) / 1000);
            }
        }
    }

    static class Response {
        private String requestID;
        private String system;

        public Response(String requestID, String system) {
            this.requestID = requestID;
            this.system = system;
        }

        public String getRequestID() {
            return requestID;
        }
    }

    static class Request {
        private String requestID;

        public Request(String requestID) {
            this.requestID = requestID;
        }

        public String getRequestID() {
            return requestID;
        }
    }

    public void startProcess() throws InterruptedException {
        SyncQueueTest queueTest = new SyncQueueTest();
        new Thread(() -> {
            try {
                queueTest.request(new Request("1234"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000L);
        new Thread(() -> {
            try {
                queueTest.offer(new Response("1234", "A"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2000L);
        new Thread(() -> {
            try {
                queueTest.offer(new Response("1234", "B"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        System.out.println(15 % 10);
    }
}
