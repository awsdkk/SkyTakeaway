package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义切面类 实现公共字段自动填充逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 自定义前置通知 给公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        // 1.获取当前被拦截的方法上的数据库操作类型
        //解释一下 为什么要这样写
        // 因为 我们的 公共字段自动填充 是在 方法执行前 进行的 所以 我们可以通过 方法签名 来获取 方法上的 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType  operationType = autoFill.value();    // 获得数据库操作类型

        // 2.获取当前被拦截的方法的参数 实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        // 准备赋值的数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime localDateTime = LocalDateTime.now();

        // 3.根据当前不同的操作类型 为对应的属性赋值 通过反射来赋值
        if(operationType.equals(OperationType.INSERT)){
            // 为插入操作 赋值
            try {
                entity.getClass().getDeclaredField(AutoFillConstant.SET_CREATE_TIME).set(entity, localDateTime);
                entity.getClass().getDeclaredField(AutoFillConstant.SET_CREATE_USER).set(entity, localDateTime);
                entity.getClass().getDeclaredField(AutoFillConstant.SET_UPDATE_TIME).set(entity, currentId);
                entity.getClass().getDeclaredField(AutoFillConstant.SET_UPDATE_USER).set(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("为插入操作 赋值 失败", e);
            }
        }else if(operationType.equals(OperationType.UPDATE)){
            // 为更新操作 赋值
            try {
                entity.getClass().getDeclaredField(AutoFillConstant.SET_UPDATE_TIME).set(entity, localDateTime);
                entity.getClass().getDeclaredField(AutoFillConstant.SET_UPDATE_USER).set(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("为更新操作 赋值 失败", e);
            }
        }





    }

}
