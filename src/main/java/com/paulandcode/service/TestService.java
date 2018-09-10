package com.paulandcode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.paulandcode.annotation.AutoFlash;
import com.paulandcode.entity.TestEntity;

@Service
public class TestService {
	public TestEntity t = new TestEntity();

	@Cacheable(value = "a")
	@AutoFlash 
	public String test(String a, String b, TestEntity tt) {
		System.out.println("a");
		return "result666";
	}

	@Cacheable(value = "b")
	@AutoFlash
	public List<TestEntity> list() {
		System.out.println("b");
		t.setName("张三7");
		t.setAge(6);
		TestEntity tt = new TestEntity();
		tt.setName("李四");
		tt.setAge(5);
		List<TestEntity> list = new ArrayList<>();
		list.add(t);
		list.add(tt);
		return list;
	}

	@Cacheable(value = "short_cache")
	public Set<TestEntity> set() {
		System.out.println("c");
		Set<TestEntity> set = new HashSet<>();
		t.setName("张三63");
		t.setAge(6);
		TestEntity tt = new TestEntity();
		tt.setName("李四");
		tt.setAge(5);
		set.add(t);
		set.add(tt);
		return set;
	}

	@Cacheable(value = "long_cache")
	public Map<String, TestEntity> map() {
		System.out.println("d");
		Map<String, TestEntity> map = new HashMap<>();
		t.setName("张三6");
		t.setAge(6);
		TestEntity tt = new TestEntity();
		tt.setName("李四");
		tt.setAge(5);
		map.put("1", t);
		map.put("2", tt);
		return map;
	}

	@Cacheable(value = "long_cache")
	public TestEntity object() {
		System.out.println("e");
		t.setName("张三6");
		t.setAge(6);
		return t;
	}
}