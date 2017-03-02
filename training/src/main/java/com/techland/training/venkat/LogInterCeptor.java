package com.techland.training.venkat;

import java.lang.reflect.Method;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LogInterCeptor {
	private static final String METHOD_NAME = "METHOD_NAME :";
	private static final String METHOD_ARG = "METHOD_ARGUMENT :";
	private static final String METHOD_EXETIME = "TIME IN (Msec) :";

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LogInterCeptor.class);
	private static StopWatch watch = new StopWatch();

	@Around("execution(* *(..)) && @annotation(LogRunTime)")
	public Object around(ProceedingJoinPoint point) {
		LOGGER.setLevel(Level.DEBUG);
		Object result = null;
		try {
			watch.reset();
			watch.start();
			result = point.proceed();
			watch.stop();
			String response = "";
			response = response.concat(
					METHOD_NAME + " \t" + MethodSignature.class.cast(point.getSignature()).getMethod().getName());
			response = response.concat(METHOD_ARG + " \t " + point.getArgs());
			response = response.concat(METHOD_EXETIME + " \t " + watch.getTime());
			LOGGER.debug(response);
		} catch (Throwable e) {
			LOGGER.debug("Exception occured in LogInterCeptor", e);
		}
		return result;
	}

	@Around("execution(* * (..))" + " && @annotation(test.LogRunTime)")
	public Object intercept(final ProceedingJoinPoint point) throws Throwable {
		LOGGER.setLevel(Level.DEBUG);
		Object result = null;
		try {
			watch.reset();
			watch.start();
			result = point.proceed();
			watch.stop();
			String response = "";
			response = response.concat(
					METHOD_NAME + " \t" + MethodSignature.class.cast(point.getSignature()).getMethod().getName());
			response = response.concat(METHOD_ARG + " \t " + point.getArgs());
			response = response.concat(METHOD_EXETIME + " \t " + watch.getTime());
			LOGGER.debug(response);
		} catch (Throwable e) {
			LOGGER.debug("Exception occured in LogInterCeptor", e);
		}
		return result;
	}

}