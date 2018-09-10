package com.paulandcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Description: 用于启动Spring_boot项目
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月7日 上午8:36:52
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}