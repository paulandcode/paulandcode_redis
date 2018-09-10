package com.paulandcode.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulandcode.entity.TestEntity;
import com.paulandcode.service.TestService;

@RestController
public class TestController {
	@Autowired
	TestService testService;

	@RequestMapping(value = "a")
	public String string() {
		TestEntity tt = new TestEntity();
		tt.setName("张三");
		tt.setAge(5);
		return testService.test("sql", "params", tt);
	}

	@RequestMapping(value = "b")
	public List<TestEntity> list() {
		return testService.list();
	}

	@RequestMapping(value = "c")
	public Set<TestEntity> set() {
		return testService.set();
	}

	@RequestMapping(value = "d")
	public Map<String, TestEntity> map() {
		return testService.map();
	}

	@RequestMapping(value = "e")
	public TestEntity object() {
		return testService.object();
	}
}