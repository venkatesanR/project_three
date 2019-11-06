package com.techmania.designprinciples.assignments.services;

import com.techmania.designprinciples.models.ServiceMarker;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceLoader {
    private final Map<String, ServiceHandler> SERVICE_HANDLERS = null;

    public ServiceLoader(Class... classes) {
/*        this.SERVICE_HANDLERS = Arrays.asList(classes).stream().
                reduce(clazz -> Arrays.asList(clazz.getInterfaces())).
      //  filter(clazz -> Objects.nonNull(clazz. (ServiceMarker.class))).
        map(serviceable -> {
            ServiceMarker annotation =
                    (ServiceMarker) serviceable.getAnnotation(ServiceMarker.class);
            return new ServiceHandler(annotation.serviceName(), serviceable.newInstance());
        }).collect(
                Collectors.toMap(ServiceHandler::getName, Function.identity()));*/
    }

    public <T> ServiceHandler getHandler(Class<T> service) {
        ServiceMarker annotation = (ServiceMarker) service.getAnnotation(ServiceMarker.class);
        if (annotation == null) {
            throw new RuntimeException("Service not defined for the Given interface");
        }
        return SERVICE_HANDLERS.get(annotation.serviceName());
    }
}
