package com.paulandcode.config;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @Description: 使用阿里巴巴的fastjson进行key和value的序列化 
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月7日 下午9:43:51
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null)
            return new byte[0];
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length <= 0)
            return null;
        return JSON.parseObject(new String(bytes, DEFAULT_CHARSET), clazz);
	}
}