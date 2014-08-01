package de.hsudbrock.beanmatcher;

import org.hamcrest.*;

public interface BeanMatcherSpec {
	
	BeanMatcherSpec diveIntoProperty(String propertyName);
	
	<T> Matcher<T> matcher(String propertyName, T item, Class<T> clazz);
	
}
