package de.hsudbrock.beanmatcher;

public class TestInnerBean {
	
	public TestInnerBean(long l, boolean b) {
		this.longValue = l;
		this.bool = Boolean.valueOf(b);
	}

	private long longValue;
	private Boolean bool;

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
	
}
