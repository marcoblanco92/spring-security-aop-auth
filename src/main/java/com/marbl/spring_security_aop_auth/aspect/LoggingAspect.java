package com.marbl.spring_security_aop_auth.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LoggingAspect {

    private final MeterRegistry meterRegistry;

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
    public Object logAndMeasure(ProceedingJoinPoint joinPoint) throws Throwable {
        // Retrieve or generate correlationId
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);
        }

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("Entering {} | args: {} | correlationId={}", methodName, Arrays.toString(args), correlationId);

        Timer.Sample sample = Timer.start(meterRegistry); // start Micrometer timer
        Object result = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            log.error("Exception in {} | message: {} | correlationId={}", methodName, t.getMessage(), correlationId, t);
            throw t;
        } finally {
            // Stop the timer and record in Micrometer/Prometheus
            sample.stop(Timer.builder("method.execution.seconds")
                    .description("Execution time of service/controller methods")
                    .tag("method", joinPoint.getSignature().getName())
                    .register(meterRegistry));

            log.info("Exiting {} | result: {} | correlationId={}", methodName, result, correlationId);

            // Clear MDC to avoid leaking values
            MDC.remove("correlationId");
        }
    }
}