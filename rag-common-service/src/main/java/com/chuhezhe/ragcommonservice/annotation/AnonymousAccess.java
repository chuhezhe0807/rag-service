package com.chuhezhe.ragcommonservice.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE}) // 注解可以在方法和类上使用
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留
@Documented
public @interface AnonymousAccess {
}
