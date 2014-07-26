package de.hsudbrock.beanmatcher;

import org.hamcrest.*;

public interface BeanMatcherSpec {
	
	BeanMatcherSpec diveIntoProperty(String propertyName);
	
	Matcher<?> matcher(String propertyName);
	
}
