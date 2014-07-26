package de.hsudbrock.beanmatcher;

import java.util.*;

/**
 * Testbean for testing the bean matcher.
 */
public class TestBean {

	private String string;
	private List<String> stringList;
	private Set<String> stringSet;
	private TestInnerBean innerBean;
	private Long longValue;

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public TestInnerBean getInnerBean() {
		return innerBean;
	}

	public void setInnerBean(TestInnerBean innerBean) {
		this.innerBean = innerBean;
	}

	public Set<String> getStringSet() {
		return stringSet;
	}

	public void setStringSet(Set<String> stringSet) {
		this.stringSet = stringSet;
	}

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> strings) {
		this.stringList = strings;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}
