package de.hsudbrock.beanmatcher;

import static org.hamcrest.Matchers.anything;

import java.util.regex.*;

import org.hamcrest.Matcher;

public class IgnoreBeanMatcherSpec implements BeanMatcherSpec {
	
	String prefix;
	Pattern regex;
	
	private IgnoreBeanMatcherSpec(String prefix, Pattern regex) {
		this.prefix = prefix;
		this.regex = regex;
	}

	@Override
	public BeanMatcherSpec diveIntoProperty(String propertyName) {
		return new IgnoreBeanMatcherSpec(prefix + propertyName + ".", regex);
	}

	@Override
	public Matcher<?> matcher(String propertyName) {
		if (regex.matcher(prefix + propertyName).matches()) {
			return anything();
		} else {
			return null;
		}
	}

	public static IgnoreBeanMatcherSpec ignoreMatching(String regex) {
		return new IgnoreBeanMatcherSpec("", Pattern.compile(regex));
	}
	
	public static IgnoreBeanMatcherSpec ignoreProperty(String propertyName) {
		return new IgnoreBeanMatcherSpec("", Pattern.compile(propertyName + "|" + ".*\\." + propertyName));
	}
}
