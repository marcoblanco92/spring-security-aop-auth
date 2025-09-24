package com.marbl.spring_security_aop_auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    @Pointcut("serviceMethods() || controllerMethods()")
    public void serviceOrControllerMethods() {
    }

    @Around("serviceOrControllerMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Retrieve existing correlationId (e.g., from HTTP filter) or generate a new one
        String correlationId = MDC.get("correlationId");

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Entering {} | args: {}", methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("Exiting {} | result: {} | duration={}ms ", methodName, result, duration);
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            log.error("Exception in {} | message: {} | duration={}ms ", methodName, t.getMessage(), duration, t);
            throw t;
        } finally {
            // Important: clear the MDC to avoid leaking values across threads
            MDC.remove("correlationId");
        }

        return result;
    }
}
