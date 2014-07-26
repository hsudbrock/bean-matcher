package de.hsudbrock.beanmatcher;

import static org.fest.reflect.core.Reflection.method;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withParametersCount;
import static org.reflections.ReflectionUtils.withPrefix;

import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.tuple.*;
import org.hamcrest.*;
import org.reflections.*;

import com.google.common.base.*;
import com.google.common.collect.*;

@SuppressWarnings("unchecked")
public class BeanMatcher {
	
	// -----------------------------------------------------------------------------------------------------------------
	// Default matcher generation
	// -----------------------------------------------------------------------------------------------------------------
	
	private static Map matcherGenerator = Maps.newHashMap(); static {
		matcherGenerator.put(String.class, new Function<Pair<String, BeanMatcherSpec[]>, Matcher<String>>() {
			@Override public Matcher<String> apply(Pair<String, BeanMatcherSpec[]> input) {
				return equalTo(input.getLeft());
			}
		});
		
		matcherGenerator.put(Iterable.class, new Function<Pair<Iterable<?>, BeanMatcherSpec[]>, Matcher<Iterable<? extends Object>>>() {
			@Override public Matcher<Iterable<? extends Object>> apply(Pair<Iterable<?>, BeanMatcherSpec[]> input) {
				List<Matcher<? super Object>> itemMatchers = Lists.newArrayList();
				for (Object object : input.getLeft()) {
					Class clazz = object.getClass();
					itemMatchers.add(matchesBean(object, clazz, input.getRight()));
				}
				return contains(itemMatchers);
			}
			
		});
		
		matcherGenerator.put(Set.class, new Function<Pair<Set<?>, BeanMatcherSpec[]>, Matcher<Iterable<? extends Object>>>() {
			@Override public Matcher<Iterable<? extends Object>> apply(Pair<Set<?>, BeanMatcherSpec[]> input) {
				List<Matcher<? super Object>> itemMatchers = Lists.newArrayList();
				for (Object object : input.getLeft()) {
					itemMatchers.add(matchesBean(object, Object.class, input.getRight()));
				}
				return containsInAnyOrder(itemMatchers);
			}
			
		});
		
		matcherGenerator.put(Number.class, new Function<Pair<Number, BeanMatcherSpec[]>, Matcher<Number>>() {
			@Override public Matcher<Number> apply(Pair<Number, BeanMatcherSpec[]> input) {
				return equalTo(input.getLeft());
			}
		});
		
		matcherGenerator.put(Boolean.class, new Function<Pair<Boolean, BeanMatcherSpec[]>, Matcher<Boolean>>() {
			@Override public Matcher<Boolean> apply(Pair<Boolean, BeanMatcherSpec[]> input) {
				return equalTo(input.getLeft());
			}
		});
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Matcher setup methods
	// -----------------------------------------------------------------------------------------------------------------
	
	public static <T> Matcher<T> matchesBean(T bean, Class<T> clazz, BeanMatcherSpec... beanMatcherSpecs) {
		if (bean == null) {
			return nullValue(clazz);
		}
		
		Matcher<T> typeBasedMatcher = findTypeBasedMatcher(bean, clazz, beanMatcherSpecs);
		if (typeBasedMatcher != null) {
			return typeBasedMatcher;
		} else {
			return createBeanMatcher(bean, clazz, beanMatcherSpecs);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------------------------------------------------
	
	private static <T> Matcher<T> findTypeBasedMatcher(T bean, Class<T> clazz, BeanMatcherSpec... beanMatcherSpecs) {
		if (clazz.isPrimitive()) {
			return equalTo(bean);
		}
		
		Class<?> relevantSuperClass = getRelevantSuperClass(clazz);
		
		if (relevantSuperClass != null) {
			return (Matcher<T>) ((Function) matcherGenerator.get(relevantSuperClass)).apply(Pair.of(bean, beanMatcherSpecs));
		} else {
			return null;
		}
	}
	
	private static <T> Matcher<T> createBeanMatcher(T bean, Class<T> clazz, BeanMatcherSpec... beanMatcherSpecs) {
		List<Matcher<? super T>> fieldMatchers = Lists.newArrayList();
		
		for (Method getter : getGetters(clazz)) {
			try {
				fieldMatchers.add(getGetterMatcher(bean, getter, beanMatcherSpecs));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return allOf(fieldMatchers);
	}

	private static <T,S> Matcher<Object> getGetterMatcher(T bean, Method getter, BeanMatcherSpec... beanMatcherSpecs) throws IllegalAccessException, InvocationTargetException {
		Class<S> returnType = (Class<S>) getter.getReturnType();
		return hasProperty(
				getPropertyName(getter), 
				getPropertyMatcher(
						getPropertyName(getter), 
						method(getter.getName()).withReturnType(returnType).in(bean).invoke(),
						returnType,
						beanMatcherSpecs));
	}
	
	private static <T> Matcher<T> getPropertyMatcher(String propertyName, T object, Class<T> clazz, BeanMatcherSpec... beanMatcherSpecs) {
		for (BeanMatcherSpec beanMatcherSpec : beanMatcherSpecs) {
			if (beanMatcherSpec.matcher(propertyName) != null) {
				return (Matcher<T>) beanMatcherSpec.matcher(propertyName);
			}
		}
		
		BeanMatcherSpec[] diveInBeanMatcherSpecs = new BeanMatcherSpec[beanMatcherSpecs.length];
		for (int i = 0; i < beanMatcherSpecs.length; i++) {
			diveInBeanMatcherSpecs[i] = beanMatcherSpecs[i].diveIntoProperty(propertyName);
		}
		
		return matchesBean(object, clazz, diveInBeanMatcherSpecs);
	}

	private static Class<?> getRelevantSuperClass(Class<?> clazz) {
		
		// first, check implemented interface
		
		for (Class iface : clazz.getInterfaces()) {
			Class<?> relevantInterface = getRelevantSuperClass(iface);
			if (relevantInterface != null) {
				return relevantInterface;
			}
		}
		
		// if no interface is relevant, check class itself
		
		if (matcherGenerator.containsKey(clazz)) {
			return clazz;
		}
		
		if (clazz.getSuperclass() != null) {
			return getRelevantSuperClass(clazz.getSuperclass());
		} else {
			return null;
		}
	}

	private static String getPropertyName(Method getter) {
		return StringUtils.uncapitalize(StringUtils.substring(getter.getName(), "get".length()));
	}

	@SuppressWarnings("unchecked")
	private static <T> Set<Method> getGetters(Class<T> clazz) {
		return ReflectionUtils.getAllMethods(clazz, withModifier(Modifier.PUBLIC), withPrefix("get"), withParametersCount(0));
	} 
}	