package com.dogac.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class LogExecutionTimeAspect {

    @Around("@annotation(com.dogac.logging.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        log.info(
                "logExecutionTime: " +
                        joinPoint.getSignature().getName()
                        + " took "
                        + (end - start)
                        + " ms");
        return result;
    }
}
