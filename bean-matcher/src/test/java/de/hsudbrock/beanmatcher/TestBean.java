package de.hsudbrock.beanmatcher;

import java.util.*;

/**
 * Testbean for testing the bean matcher.
 */
public class TestBean {

	private String string;
	private Long longValue;
	private Boolean booleanValue;
	
	private boolean primitiveBooleanValue;
	
	private List<String> stringList;
	private Set<String> stringSet;
	
	private TestInnerBean innerBean;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public boolean getPrimitiveBooleanValue() {
		return primitiveBooleanValue;
	}

	public void setPrimitiveBooleanValue(boolean primitiveBooleanValue) {
		this.primitiveBooleanValue = primitiveBooleanValue;
	}

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}

	public Set<String> getStringSet() {
		return stringSet;
	}

	public void setStringSet(Set<String> stringSet) {
		this.stringSet = stringSet;
	}

	public TestInnerBean getInnerBean() {
		return innerBean;
	}

	public void setInnerBean(TestInnerBean innerBean) {
		this.innerBean = innerBean;
	}
}
