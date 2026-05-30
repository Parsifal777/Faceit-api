package com.faceit.aspect;

import com.faceit.annotation.Loggable;
import com.faceit.annotation.PerformanceLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Before("@annotation(loggable)")
    public void logMethodCall(Loggable loggable) throws Throwable { }

    @Around("@annotation(com.faceit.annotation.Loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable loggable = signature.getMethod().getAnnotation(Loggable.class);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        String timestamp = LocalDateTime.now().format(formatter);

        // Логируем вход в метод
        String logMessage = String.format("[%s] [%s] [%s.%s] Вход с аргументами: %s",
                timestamp, loggable.value(), className, methodName, Arrays.toString(args));

        switch (loggable.value()) {
            case DEBUG:
                logger.debug(logMessage);
                break;
            case INFO:
                logger.info(logMessage);
                break;
            case WARN:
                logger.warn(logMessage);
                break;
            case ERROR:
                logger.error(logMessage);
                break;
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Логируем выход из метода
            String exitMessage = String.format("[%s] [%s] [%s.%s] Выход. Результат: %s. Время выполнения: %d ms",
                    timestamp, loggable.value(), className, methodName,
                    result != null ? result.toString() : "null", executionTime);

            switch (loggable.value()) {
                case DEBUG:
                    logger.debug(exitMessage);
                    break;
                case INFO:
                    logger.info(exitMessage);
                    break;
                case WARN:
                    logger.warn(exitMessage);
                    break;
                case ERROR:
                    logger.error(exitMessage);
                    break;
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            String errorMessage = String.format("[%s] [ERROR] [%s.%s] Исключение: %s. Время выполнения до ошибки: %d ms",
                    timestamp, className, methodName, e.getMessage(), executionTime);
            logger.error(errorMessage, e);
            throw e;
        }
    }

    @Around("@annotation(com.faceit.annotation.PerformanceLog)")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        long startTime = System.nanoTime();
        Object result = joinPoint.proceed();
        long endTime = System.nanoTime();

        long executionTimeMs = (endTime - startTime) / 1_000_000;

        if (executionTimeMs > 100) {
            logger.warn("[PERFORMANCE] [{}:{}] Время выполнения: {} ms (ПРЕВЫШЕН ПОРОГ!)",
                    className, methodName, executionTimeMs);
        } else {
            logger.debug("[PERFORMANCE] [{}:{}] Время выполнения: {} ms",
                    className, methodName, executionTimeMs);
        }

        return result;
    }
}
