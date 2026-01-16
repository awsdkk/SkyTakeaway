package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//1 自定义注解 AutoFi，用于标识需要进行公共字段自动填自定义切面类
//2 AutoFillAspect，统一拦截加入了 AutoFill
//3 在 Mapper 的方法上加入 AutoFil 注解

/**
 * 自定义注解，用于标识需要自动填充的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库操作类型 update insert
    OperationType value();


}
