package com.paulandcode.schedule;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.paulandcode.entity.Data;

/**
 * 
 * @description: 定时任务
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月8日 下午3:35:13
 */
@Configuration // 声明类为系统配置类
@EnableScheduling // 开启调度任务
@Component
public class ScheduleJobs {
	/** 默认时间缓存, 单位: 秒. **/
	@Value("${spring.redis.short_cache_time}")
	private int SHORT_CACHE_TIME;

	@Autowired
	public RedisTemplate<String, Object> redisTemplate;

	/**
	 * 
	 * @description: 定时更新数据库数据
	 * 
	 */
	@Scheduled(cron = "${spring.schedule.cron2}")
	public void job() {
		Set<Object> set = redisTemplate.boundSetOps("dataInfo").members();
		Iterator<Object> it = set.iterator();
		while (it.hasNext()) {
			Data data = (Data) it.next();
			String key = data.getKey();
			String className = data.getClassName();
			String methodName = data.getMethodName();
			List<Class<?>> paramTypes = data.getParamTypes();
			List<Object> params = data.getParams();
			Object value = null;
			// 利用Java反射执行方法, 获得数据
			try {
				Class<?> clazz = Class.forName(className);
				Object object = clazz.newInstance();
				// 第一个参数写的是方法名, 第二个\第三个\...写的是方法参数列表中参数的类型
				Method method = clazz.getMethod(methodName, paramTypes.toArray(new Class<?>[0]));
				// invoke执行该方法, 并携带参数值
				value = method.invoke(object, params.toArray(new Object[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			redisTemplate.boundValueOps(key).set(value);
			// 设置过期时间, 可以不设置, 默认为不过期
			redisTemplate.expire(key, SHORT_CACHE_TIME, TimeUnit.SECONDS);
		}
	}
}