package com.techmania.designprinciples.assignments.services;

import com.techmania.designprinciples.models.ServiceMarker;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ServiceLoader {
    private final Map<String, ServiceHandler> SERVICE_HANDLERS = new ConcurrentHashMap<>();

    public ServiceLoader(Class... classes) {
        loadServices(Arrays.asList(classes));
    }

    public <T> ServiceHandler getHandler(Class<T> service) {
        ServiceMarker annotation = (ServiceMarker) service.getAnnotation(ServiceMarker.class);
        return SERVICE_HANDLERS.get(annotation.serviceName());
    }

    private void loadServices(List<Class> classes) {
        classes.stream().
                map(clazz -> {
                    return Stream.of(clazz.getInterfaces()).
                            map(filtered -> {
                                ServiceHandler serviceHandler = null;
                                try {
                                    ServiceMarker annotation =
                                            (ServiceMarker) filtered.getAnnotation(ServiceMarker.class);
                                    if (annotation == null) {
                                        return null;
                                    }
                                    serviceHandler = new ServiceHandler(annotation.serviceName(),
                                            clazz.newInstance());
                                } catch (IllegalAccessException e) {
                                    System.out.println("********* Unable to create service***************");
                                } catch (InstantiationException e) {
                                    System.out.println("********* Unable to Instantiate service**********");
                                }
                                return serviceHandler;
                            }).findAny().get();
                }).filter(Objects::nonNull).
                forEach(serviceHandler -> {
                    SERVICE_HANDLERS.put(serviceHandler.getName(), serviceHandler);
                });
    }
}
