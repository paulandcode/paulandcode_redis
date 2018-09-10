package com.paulandcode.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.paulandcode.entity.Data;

/**
 * 
 * @description: 将要定时刷新缓存的数据的相关信息存入缓存中, 
 * 	方便定时任务对Redis缓存的数据进行更新 
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月10日 下午4:58:10
 */
@Aspect
@Component
public class AutoFlashAspect {
	/** 默认时间缓存, 单位: 秒. **/
	@Value("${spring.redis.long_cache_time}")
	private int LONG_CACHE_TIME;
	
	private static RedisTemplate<String, Object> redisTemplate;

	/**
	 * 
	 * @description: 静态方法中注入Been 
	 * @param redisTemplate
	 */
	@Autowired
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		AutoFlashAspect.redisTemplate = redisTemplate;
	}
	
	/**
	 * 
	 * @description: 切入点
	 */
	@Pointcut("@annotation(com.paulandcode.annotation.AutoFlash)")
	public void pointCut() {

	}

	/**
	 * 
	 * @description: 方法执行后, 将方法相关信息存起来, 方便定时任务利用反射执行方法. 
	 * @param joinPoint 切入点
	 */
	@After("pointCut()")
	public void after(JoinPoint joinPoint) {
		MethodSignature sign = (MethodSignature) joinPoint.getSignature();
		Object[] params = joinPoint.getArgs();
		Method method = sign.getMethod();
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		String paramsString = "";
		for (int i = 0; i < params.length; i++) {
			paramsString += params[i] + ",";
		}
		Cacheable cacheable = method.getAnnotation(Cacheable.class);
		String key = cacheable.value()[0] + "::" + className + ":" + methodName + ":"
				+ paramsString.substring(0, paramsString.length() - 1);
		Data data = new Data(key, className, methodName, 
				Arrays.asList(method.getParameterTypes()), Arrays.asList(params));
		// 将要定时刷新缓存的数据的相关信息存入缓存
		redisTemplate.boundSetOps("dataInfo").add(data);
		// 设置过期时间, 可以不设置, 默认为不过期
		redisTemplate.expire(key, LONG_CACHE_TIME, TimeUnit.SECONDS);
	}
}