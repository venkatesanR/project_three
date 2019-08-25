package com.techmania.designprinciples.assignments.services;

import com.techmania.designprinciples.assignments.scheduler.SchedulerManager;

import java.lang.reflect.Proxy;

public class ServiceClient {
    private static ServiceLoader loader;

    static {
        loader = new ServiceLoader(SchedulerManager.class);
    }

    public static <T> T getProxyService(Class<T> service) {
        return service.cast(Proxy.newProxyInstance(
                ServiceHandler.class.getClassLoader(),
                new Class[]{service},
                loader.getHandler(service)));
    }
}
