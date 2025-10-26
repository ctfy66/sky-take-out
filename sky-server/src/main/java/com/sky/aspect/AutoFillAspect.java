package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 * 自动填充切面
 */
@Aspect
@Slf4j
@Component
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 前置通知
     * 给公共字段自动赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("正在执行自动填充的前置通知...");
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 约定：第一个参数是实体对象
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        log.info("自动填充的实体对象：{}", entity);
        //准备公共字段的值
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        // 后续根据不同的操作类型，给公共字段赋值
        // 获取操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        log.info("操作类型：{}", operationType);

        if (operationType == OperationType.INSERT) {
            //获取赋值方法并调用
            try {
                entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(entity, currentId);
                entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            //获取赋值方法并调用
            try {
                entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
