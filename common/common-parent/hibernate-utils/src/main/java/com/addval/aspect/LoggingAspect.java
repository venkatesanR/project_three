package com.addval.aspect;

import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.addval.annotations.LogExecutionTime;
import com.addval.utils.StrUtl;

@Component
@Aspect
public class LoggingAspect {
	@Around("@annotation(logExecutionTime)")
	public Object logAround(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
		final Logger logger = !StrUtl.isEmptyTrimmed(logExecutionTime.loggerName()) ? Logger.getLogger(logExecutionTime.loggerName()) : Logger.getLogger(joinPoint.getTarget().getClass());
		final long startTime = System.currentTimeMillis(); // System.nanoTime();
		Object object = joinPoint.proceed();
		if (logger.isDebugEnabled()) {
			logger.debug("EXECUTION_TIME[" + joinPoint.getTarget().getClass().getSimpleName() + ":" + joinPoint.getSignature().getName() + "] = " + NumberFormat.getInstance().format((System.currentTimeMillis() - startTime)) + " ms");
		}
		return object;
	}
}
