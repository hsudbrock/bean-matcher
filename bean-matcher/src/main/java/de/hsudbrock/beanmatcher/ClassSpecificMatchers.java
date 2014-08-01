package de.hsudbrock.beanmatcher;

import java.util.*;

import com.google.common.collect.*;

/**
 * Data structure containing class-specific matchers.
 */
public class ClassSpecificMatchers {
	
	private Map<ClassKey<?>, ValueBasedMatcher<?>> map = Maps.newHashMap();
	
	public <T> void put(Class<T> clazz, ValueBasedMatcher<T> valueBasedMatcher) {
		map.put(new ClassKey<T>(clazz), valueBasedMatcher);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ValueBasedMatcher<T> get(Class<T> clazz) {
		return ((ValueBasedMatcher<T>) map.get(new ClassKey<T>(clazz)));
	}
	
	public <T> boolean containsKey(Class<T> clazz) {
		return map.containsKey(new ClassKey<T>(clazz));
	}

	public class ClassKey<T> {
		
		private Class<T> clazz;
		
		public ClassKey(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		public int hashCode() {
			return clazz.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ClassKey) {
				ClassKey<?> other = (ClassKey<?>) o;
				return other.clazz.equals(this.clazz);
			} else {
				return false;
			}
		}
		
	}
	
}
