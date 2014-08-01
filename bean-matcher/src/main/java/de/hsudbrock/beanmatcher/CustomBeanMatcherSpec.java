package de.hsudbrock.beanmatcher;

import java.util.regex.*;

import org.hamcrest.Matcher;

import com.google.common.base.*;

public class CustomBeanMatcherSpec implements BeanMatcherSpec {
	
	private String prefix;
	private Pattern regex;
	private Function matcherFunction;
	
	private CustomBeanMatcherSpec(String prefix, Pattern regex, Function matcherFunction) {
		this.prefix = prefix;
		this.regex = regex;
		this.matcherFunction = matcherFunction;
	}

	@Override
	public BeanMatcherSpec diveIntoProperty(String propertyName) {
		return new CustomBeanMatcherSpec(prefix + propertyName + ".", regex, matcherFunction);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Matcher<T> matcher(String propertyName, T item, Class<T> clazz) {
		if (regex.matcher(prefix + propertyName).matches()) {
			return (Matcher<T>) matcherFunction.apply(item);
		} else {
			return null;
		}
	}
	
	public static CustomBeanMatcherSpec propertyMatcher(String propertyRegex, Function matcherFunction) {
		return new CustomBeanMatcherSpec("", Pattern.compile(propertyRegex), matcherFunction);
	}

}
