package com.poianitibaldizhou.trackme.sharedataservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * General logger of the system
 */
@Slf4j
@Component
@Aspect
public class Logger {

    /**
     * Logs methods regarding the call of services of share data service
     *
     * @param joinPoint the join point contains which the call happened
     * @return the object which the service call should return
     * @throws Throwable propagated exception from the service call
     */
    @Around("execution(public * com.poianitibaldizhou.trackme.sharedataservice.service.*.*(..))")
    public Object logShareDataServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retValue;
        log.info("Service call (before): " + joinPoint.getSignature().toString());
        try {
            retValue = joinPoint.proceed();
            log.info("Service call (after): " + joinPoint.getSignature().toString());
        } catch (Throwable throwable) {
            log.info("Service call (exception): " + joinPoint.getSignature().toString() + " caused by:" + throwable.toString());
            throw throwable;
        }
        return retValue;
    }

    /**
     * Logs methods regarding the call of query db of share data service
     *
     * @param joinPoint the join point contains which the call happened
     * @return the object which the service call should return
     * @throws Throwable propagated exception from the service call
     */
    @Around("execution(public * com.poianitibaldizhou.trackme.sharedataservice.repository.*.get*(..))")
    public Object logCustomRepositoryQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retValue;
        log.info("QueryDB (before): " + joinPoint.getSignature().toString());
        try {
            retValue = joinPoint.proceed();
            log.info("QueryDB (after): " + joinPoint.getSignature().toString());
        } catch (Throwable throwable) {
            log.info("QueryDB (exception): " + joinPoint.getSignature().toString() + " caused by:" + throwable.toString());
            throw throwable;
        }
        return retValue;
    }



}