package com.paulandcode.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @description: 存放需要放入Redis缓存的数据的信息, 根据这些信息, 再通过Java反射获取数据.
 * @author: paulandcode
 * @email: paulandcode@gmail.com
 * @since: 2018年9月8日 下午2:45:51
 */
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Redis缓存数据时的Key **/
	private String key;

	/** 所要执行方法所在类的全名称, 包括包名和类名 **/
	private String className;

	/** 所要执行方法的方法名 **/
	private String methodName;

	/** 所要执行方法的参数类型列表, 不能用数组, 否则FastJSON解析时会报错 **/
	private List<Class<?>> paramTypes;

	/** 所要执行方法的参数值列表, 不能用数组, 否则FastJSON解析时会报错 **/
	private List<Object> params;

	public Data() {
		super();
	}

	public Data(String key, String className, String methodName, List<Class<?>> paramTypes, List<Object> params) {
		super();
		this.key = key;
		this.className = className;
		this.methodName = methodName;
		this.paramTypes = paramTypes;
		this.params = params;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<Class<?>> getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(List<Class<?>> paramTypes) {
		this.paramTypes = paramTypes;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}
}