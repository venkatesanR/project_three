package com.techmania.designprinciples.assignments.services;

import com.techmania.designprinciples.models.ServiceMarker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceLoader {
    private final Map<String, ServiceHandler> SERVICE_HANDLERS = new HashMap<String, ServiceHandler>();

    public ServiceLoader(Class... classes) {
        Arrays.asList(classes)
                .stream()
                .flatMap(clazz -> Arrays.stream(clazz.getInterfaces()))
                .map(serviceable -> {
                    ServiceMarker annotation =
                            (ServiceMarker) serviceable.getAnnotation(ServiceMarker.class);
                    try {
                        return new ServiceHandler(annotation.serviceName(), serviceable.newInstance());
                    } catch (InstantiationException e) {
                        // e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // e.printStackTrace();
                    }
                    return null;
                }).forEach(serviceHandler -> SERVICE_HANDLERS.put(serviceHandler.getName(), serviceHandler));
    }

    public <T> ServiceHandler getHandler(Class<T> service) {
        ServiceMarker annotation = (ServiceMarker) service.getAnnotation(ServiceMarker.class);
        if (annotation == null) {
            throw new RuntimeException("Service not defined for the Given interface");
        }
        return SERVICE_HANDLERS.get(annotation.serviceName());
    }
}
