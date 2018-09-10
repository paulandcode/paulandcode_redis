package com.paulandcode.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * 
 * @Description: Redis缓存配置 
 *	缓存注解: @Cacheable 与 @CachePut, 可以作用在类和方法上. (可以在类上统一指定缓存注解后, 在方法上自定义key)
 * 		@Cacheable: Spring在每次执行前都会检查Cache中是否存在相同Key的缓存元素, 
 * 			如果存在就不再执行该方法, 而是直接从缓存中获取结果进行返回, 否则才会执行并将返回结果存入指定的缓存中. 
 * 			若统一指定在类上, 则不可以在类注解上自定义key, 否则多个不同返回值类型的方法调用注解时, 会报错类型转换错误.  
 * 		@CachePut: 方法在执行前不会去检查缓存中是否存在之前执行过的结果, 而是每次都会执行该方法, 
 * 			并将执行结果存入指定的缓存中. 
 * 			若统一指定在类上, 则不建议在类注解上自定义key, 否则不同方法调用后, 会冲掉其他方法的缓存. 
 *	条件化缓存: unless与condition, 用法如下: 
 *		@CachePut(value = "long_cache", unless = "#result.desc.contains('nocache')")
 *		unless: 只能阻止将对象放进缓存, 但是在这个方法调用的时候, 依然会去缓存中进行查找, 
 *			如果找到了匹配的值, 就会返回找到的值. 
 *		condition: 如果表达式计算结果为false, 那么在这个方法调用的过程中, 缓存是被禁用的. 
 *			就是说, 不会去缓存进行查找, 同时返回值也不会放进缓存中. 
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月7日 下午8:47:38
 */
@Configuration // 指定该类为配置类
@EnableCaching // 启用缓存 
public class RedisCacheConfig extends CachingConfigurerSupport {
	/** 默认时间缓存, 单位: 秒. **/
	@Value("${spring.redis.default_cache_time}")
	private int DEFAULT_CACHE_TIME;
	
	/** 短时间缓存, 单位: 秒. **/
	@Value("${spring.redis.short_cache_time}")
	private int SHORT_CACHE_TIME;
	
	/** 长时间缓存, 单位: 秒. **/
	@Value("${spring.redis.long_cache_time}")
	private int LONG_CACHE_TIME;

	/**
	 * 
	 * @Description: 指定默认Key生成策略 , 生成后, key如: 
	 *  short_cache::com.paulandcode.service.TestService.executeSQL("sql","params")
	 * 	
	 *	也可以自定义Key, 如: @Cacheable(value = "long_cache", key = "'mykey'")
	 *	也可以将方法参数或其属性当作Key, 如: @Cacheable(value = "long_cache", key = "#param.id"),
	 *		param为方法参数名, id为参数param的一个属性
	 *	若方法参数为数字, 需要转换为字符串, 如: @Cacheable(value = "long_cache", key = "#id + ''")
	 *  @CachePut 与 @Cacheable 不同, @CachePut 还可以将返回值作为key, 如:
	 *  	@CachePut(value = "long_cache", key = "#result.id + ''"), 这里的result就是方法的返回值
	 *  target: 调用方法的对象
	 * 	method: 所调用的方法
	 * 	params: 方法的参数
	 * @see org.springframework.cache.annotation.CachingConfigurerSupport#keyGenerator()
	 */
	@Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(target.getClass().getCanonicalName());
            stringBuilder.append(":");
            stringBuilder.append(method.getName());
            stringBuilder.append(":");
            for (int i = 0; i < params.length; i++) {
            	stringBuilder.append(params[i]);
            	if (i < params.length - 1) {
            		stringBuilder.append(",");
            	}
            }
            return stringBuilder.toString();
        };
    }
	
    /**
     * 
     * @Description: 申明缓存管理器, 会创建一个切面(Aspect)并触发Spring缓存注解的切点(Pointcut)
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                this.getRedisCacheConfigurationWithTtl(DEFAULT_CACHE_TIME), // 默认缓存时间策略
                this.getRedisCacheConfigurationMap()); // 指定缓存时间策略
    }
    
    /**
     * 
     * @description: 不适合添加缓存注解的地方, 可以手动使用RedisTemplate来对Redis数据库进行增删改查
     * 		注意: 使用RedisTemplate的地方, 其配置与RedisCacheConfiguration中配置的不一样, 所以, 
     * 			此处需要设置RedisTemplate自己的Key与Value的序列化. 同时, 还需设置RedisTemplate自己的
     * 			缓存过期时间. 然而, RedisTemplate不支持批量设置缓存过期时间, 需要针对每个Key分别设置. 例如: 
     * 				redisTemplate.expire("yourKey", 10, TimeUnit.SECONDS);
     * 					设置了Key为"yourKey"的缓存过期时间为: 10秒. 
     *			若不设置过期时间, 则缓存永不过期.
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setKeySerializer(new FastJsonRedisSerializer<>(String.class));
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
	
	/**
	 * 
	 * @Description: 设置缓存时间, 设置key与value的序列化. 
	 * 		可设置禁止缓存null: redisCacheConfiguration.disableCachingNullValues(), 若有null缓存, 则会报错. 
	 * @param seconds 缓存时间, 单位: 秒. 
	 * @return
	 */
	private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        return RedisCacheConfiguration.defaultCacheConfig()
        		.serializeKeysWith(RedisSerializationContext.SerializationPair
        				.fromSerializer(new FastJsonRedisSerializer<>(String.class)))
        		.serializeValuesWith(RedisSerializationContext.SerializationPair
        				.fromSerializer(new FastJsonRedisSerializer<>(Object.class)))
        		.entryTtl(Duration.ofSeconds(seconds));
    }
	
	/**
	 * 
	 * @Description: 可以自定义缓存时间, 使用如下: 
	 * 		@Cacheable(value = "short_cache") // 缓存时间: SHORT_CACHE_TIME
     *		@Cacheable(value = "long_cache") // 缓存时间: LONG_CACHE_TIME
     *		@Cacheable(value = "others") // 默认缓存时间: DEFAULT_CACHE_TIME
     *		还可以根据需要再增加缓存时间策略
	 * @return
	 */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("short_cache", this.getRedisCacheConfigurationWithTtl(SHORT_CACHE_TIME));
        redisCacheConfigurationMap.put("long_cache", this.getRedisCacheConfigurationWithTtl(LONG_CACHE_TIME));
        return redisCacheConfigurationMap;
    }
}