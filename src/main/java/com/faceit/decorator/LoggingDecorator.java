package com.faceit.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Supplier;

public class LoggingDecorator<T> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingDecorator.class);

    public static <R> R log(String operationName, Supplier<R> supplier) {
        long startTime = System.currentTimeMillis();
        logger.info("[ДЕКОРАТОР] Начало операции: {}", operationName);

        try {
            R result = supplier.get();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("[ДЕКОРАТОР] Успешное завершение: {}. Время: {} ms", operationName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("[ДЕКОРАТОР] Ошибка в операции: {}. Время до ошибки: {} ms. Ошибка: {}",
                    operationName, executionTime, e.getMessage(), e);
            throw e;
        }
    }

    public static void logVoid(String operationName, Runnable runnable) {
        long startTime = System.currentTimeMillis();
        logger.info("[ДЕКОРАТОР] Начало операции: {}", operationName);

        try {
            runnable.run();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("[ДЕКОРАТОР] Успешное завершение: {}. Время: {} ms", operationName, executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("[ДЕКОРАТОР] Ошибка в операции: {}. Время до ошибки: {} ms. Ошибка: {}",
                    operationName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}
