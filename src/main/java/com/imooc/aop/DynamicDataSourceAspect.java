package com.imooc.aop;

import com.imooc.config.datasource.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DynamicDataSourceAspect
{
    
    @Pointcut("execution(* com.imooc.service..*.*(..))")
    private void aspect()
    {
        
    }
    
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        String method = joinPoint.getSignature().getName();
        
        if(method.startsWith("find") || method.startsWith("select") || method.startsWith("query") || method.startsWith("search"))
        {
            DataSourceContextHolder.slaveDataSourceKeys.add("slaveDataSource");
            DataSourceContextHolder.slaveDataSourceKeys.add("slave2DataSource");
            DataSourceContextHolder.useSlaveDataSource();
            
            log.info("switch to slave datasource...");
        }
        else
        {
            DataSourceContextHolder.useMasterDataSource();
            log.info("switch to master datasource...");
        }
        
        try
        {
            return joinPoint.proceed();
        }
        finally
        {
            log.info("清除 datasource router...");
            DataSourceContextHolder.clear();
        }
        
    }
    
}
