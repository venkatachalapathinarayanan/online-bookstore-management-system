package com.bookstore.usermanagement.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {
    private val logger = LoggerFactory.getLogger(LoggingAspect::class.java)

    @Around("execution(* com.bookstore.usermanagement.service..*(..)) || execution(* com.bookstore.usermanagement.controller..*(..))")
    fun logServiceAndControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = "${joinPoint.signature.declaringTypeName}.${joinPoint.signature.name}"
        logger.info("Entering $methodName with args: ${joinPoint.args.joinToString()}")
        val start = System.currentTimeMillis()
        try {
            val result = joinPoint.proceed()
            val duration = System.currentTimeMillis() - start
            logger.info("Exiting $methodName; execution time: ${duration}ms")
            return result
        } catch (ex: Throwable) {
            logger.error("Exception in $methodName: ${ex.message}", ex)
            throw ex
        }
    }
}

