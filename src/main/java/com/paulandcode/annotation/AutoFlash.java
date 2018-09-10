package com.paulandcode.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @description: 添加此注解后可以启动自动刷新缓存, 该注解不能单独使用, 
 * 	需要配合 @Cacheable 或 @CachePut 一起使用.  
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月10日 下午4:57:43
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoFlash {
}