package com.techmania.common.util.misc;

import com.techmania.common.exceptions.XRuntime;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ObjectUtil {
    private ObjectUtil() {

    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        try {
            Class[] argTypes = new Class[args.length];
            Arrays.stream(args)
                    .map(arg -> arg.getClass())
                    .collect(Collectors.toList())
                    .toArray(argTypes);
            return clazz.getDeclaredConstructor(argTypes)
                    .newInstance(args);
        } catch (NoSuchMethodException |
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException ex) {
            throw new XRuntime("Unable to create Object for Class: " + clazz.toString());
        }
    }
}
