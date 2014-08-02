package de.hsudbrock.beanmatcher;

import java.util.*;

public class TestInnerBean {

	private long primitiveLongValue;
	private Boolean booleanValue;
	private String string;
	private List<TestBean> testBeanList;
	
	public long getPrimitiveLongValue() {
		return primitiveLongValue;
	}
	
	public void setPrimitiveLongValue(long primitiveLongValue) {
		this.primitiveLongValue = primitiveLongValue;
	}
	
	public Boolean getBooleanValue() {
		return booleanValue;
	}
	
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public List<TestBean> getTestBeanList() {
		return testBeanList;
	}

	public void setTestBeanList(List<TestBean> testBeanList) {
		this.testBeanList = testBeanList;
	}
}
