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
import java.util.regex.*;

import org.apache.commons.lang3.*;
import org.hamcrest.*;
import org.hamcrest.Matcher;
import org.reflections.*;

import com.google.common.collect.*;

@SuppressWarnings("unchecked")
public class BeanMatcher<T> extends BaseMatcher<T> {
	
	// -----------------------------------------------------------------------------------------------------------------
	// Constants
	// -----------------------------------------------------------------------------------------------------------------
	
	private static final ValueBasedMatcher<String> STRING_EQUALS_MATCHER = new ValueBasedMatcher<String>() {
		@Override public Matcher<String> apply(String input) {
			return equalTo(input);
		}
	};

	private static final ValueBasedMatcher<Number> NUMBER_EQUALS_MATCHER = new ValueBasedMatcher<Number>() {
		@Override public Matcher<Number> apply(Number input) {
			return equalTo(input);
		}
	};
	
	private static final ValueBasedMatcher<Boolean> BOOLEAN_EQUALS_MATCHER = new ValueBasedMatcher<Boolean>() {
		@Override public Matcher<Boolean> apply(Boolean input) {
			return equalTo(input);
		}
	};
	
	private static final PassThroughValueBasedMatcher<Iterable<?>> ITERABLE_PASSTHROUGH_MATCHER = new PassThroughValueBasedMatcher<Iterable<?>>() {
		@Override public Matcher<Iterable<?>> apply(Iterable<?> iterable) {
			List<Matcher<? super Object>> itemMatchers = Lists.newArrayList();
			for (Object object : iterable) {
				@SuppressWarnings("rawtypes")
				Class clazz = object.getClass();
				itemMatchers.add(super.configuredBeanMatcher(BeanMatcher.matchesBean(object, clazz)));
			}
			return contains(itemMatchers);
		}
	};
	
	private static final PassThroughValueBasedMatcher<Iterable<?>> SET_PASSTHROUGH_MATCHER = new PassThroughValueBasedMatcher<Iterable<?>>() {
		@Override public Matcher<Iterable<?>> apply(Iterable<?> Set) {
			List<Matcher<? super Object>> itemMatchers = Lists.newArrayList();
			for (Object object : Set) {
				@SuppressWarnings("rawtypes")
				Class clazz = object.getClass();
				itemMatchers.add(super.configuredBeanMatcher(BeanMatcher.matchesBean(object, clazz)));
			}
			return containsInAnyOrder(itemMatchers);
		}
	};
	
	// -----------------------------------------------------------------------------------------------------------------
	// Fields
	// -----------------------------------------------------------------------------------------------------------------

	private T bean;
	private Class<T> clazz;
	
	private ClassSpecificMatchers classSpecificMatchers = new ClassSpecificMatchers();
	private Map<Pattern, ClassSpecificMatchers> fieldSpecificMatchers = Maps.newHashMap();

	private Matcher<T> matcher;  
	
	// -----------------------------------------------------------------------------------------------------------------
	// Construction
	// -----------------------------------------------------------------------------------------------------------------
	
	private BeanMatcher(T bean, Class<T> clazz) {
		this.bean = bean;
		this.clazz = clazz;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Implementation of BaseMatcher
	// -----------------------------------------------------------------------------------------------------------------
	
	@Override
	public boolean matches(Object item) {
		if (matcher == null) {
			matcher = createMatcher();
		}
		return matcher.matches(item);
	}
	
	@Override
	public void describeTo(Description description) {
		if (matcher == null) {
			matcher = createMatcher();
		}
		matcher.describeTo(description);
	}
	
	@Override
	public void describeMismatch(Object item, Description mismatchDescription) {
		if (matcher == null) {
			matcher = createMatcher();
		}
		matcher.describeMismatch(item, mismatchDescription);
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Matcher setup methods
	// -----------------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public static <T> BeanMatcher<T> matchesBean(T bean, Class<T> clazz) {
		return new BeanMatcher<T>(bean, clazz)
				.withValueSpecificMatcher(STRING_EQUALS_MATCHER, String.class)
				.withValueSpecificMatcher(NUMBER_EQUALS_MATCHER, Number.class)
				.withValueSpecificMatcher(BOOLEAN_EQUALS_MATCHER, Boolean.class)
				.withValueSpecificMatcher((PassThroughValueBasedMatcher) ITERABLE_PASSTHROUGH_MATCHER, Iterable.class)
				.withValueSpecificMatcher((PassThroughValueBasedMatcher) SET_PASSTHROUGH_MATCHER, Set.class);
	}
	
	public BeanMatcher<T> withClassSpecificMatcher(final Matcher<T> matcher, Class<T> clazz) {
		this.classSpecificMatchers.put(clazz, new ValueBasedMatcher<T>() {
			@Override public Matcher<T> apply(T value) {
				return matcher;
			}
		});
		return this;
	}
	
	public <S> BeanMatcher<T> withValueSpecificMatcher(ValueBasedMatcher<S> valueBasedMatcher, Class<S> clazz) {
		this.classSpecificMatchers.put(clazz, valueBasedMatcher);
		return this;
	}
	
	public <S> BeanMatcher<T> withFieldMatcher(String fieldname, final Matcher<S> matcher, Class<S> clazz) {
		ValueBasedMatcher<S> valueBasedMatcher = new ValueBasedMatcher<S>() {
			@Override public Matcher<S> apply(S value) {
				return matcher;
			}
		};
		
		Pattern pattern = Pattern.compile(fieldname);
		
		if (! this.fieldSpecificMatchers.containsKey(pattern)) {
			this.fieldSpecificMatchers.put(pattern, new ClassSpecificMatchers());
		} 
		
		this.fieldSpecificMatchers.get(pattern).put(clazz, valueBasedMatcher);
		
		return this;
	}
	
	public BeanMatcher<T> withFieldMatcher(String fieldname, final ValueBasedMatcher<T> valueBasedMatcher, Class<T> clazz) {
		Pattern pattern = Pattern.compile(fieldname);
		
		if (! this.fieldSpecificMatchers.containsKey(pattern)) {
			this.fieldSpecificMatchers.put(pattern, new ClassSpecificMatchers());
		} 
		
		this.fieldSpecificMatchers.get(pattern).put(clazz, valueBasedMatcher);
		
		return this;
	}
	
	private BeanMatcher<T> withClassSpecificMatchers(ClassSpecificMatchers classSpecificMatchers) {
		this.classSpecificMatchers = classSpecificMatchers;
		return this;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	private <S> Matcher<T> createMatcher() {
		if (bean == null) {
			return nullValue(clazz);
		}
		
		Class<S> relevantSuperClass = (Class<S>) getRelevantSuperClass(clazz);
		
		if (relevantSuperClass != null && classSpecificMatchers.containsKey(relevantSuperClass)) {
			ValueBasedMatcher<S> valueBasedMatcher = classSpecificMatchers.get(relevantSuperClass);
			if (valueBasedMatcher instanceof PassThroughValueBasedMatcher) {
				((PassThroughValueBasedMatcher) valueBasedMatcher).setBeanMatcherConfigurer(createBeanMatcherConfigurer());
			}
			return (Matcher<T>) valueBasedMatcher.apply((S) bean);
		}
		
		if (clazz.isPrimitive()) {
			return equalTo(bean);
		} 
		
		return createAllFieldsMatcher();
	}
	
	private <S> BeanMatcherConfigurer<S> createBeanMatcherConfigurer() {
		return new BeanMatcherConfigurer<S>() {
			@Override public BeanMatcher<S> apply(BeanMatcher<S> beanMatcher) {
				return beanMatcher.withClassSpecificMatchers(classSpecificMatchers);
			}
		};
	}

	private Matcher<T> createAllFieldsMatcher() {
		List<Matcher<? super T>> fieldMatchers = Lists.newArrayList();
		for (Method getterMethod : getGetterMethods(clazz)) {
			fieldMatchers.add(createGetterMatcher(getterMethod));
		}
		return allOf(fieldMatchers);
	}

	private <S> Matcher<S> createGetterMatcher(Method getter)  {
		Class<S> returnType = (Class<S>) getter.getReturnType();
		S value = method(getter.getName()).withReturnType(returnType).in(bean).invoke();
		
		for (Pattern pattern : fieldSpecificMatchers.keySet()) {
			if (pattern.matcher(getPropertyName(getter)).matches()) {
				 ValueBasedMatcher<S> valueBasedMatcher = fieldSpecificMatchers.get(pattern).get(returnType);
				 Matcher<S> matcher = valueBasedMatcher.apply(value);
				 if (matcher != null) {
					 return hasProperty(
							 getPropertyName(getter),
							 matcher);
				 }
			}
		}
		
		return hasProperty(
				getPropertyName(getter),
				BeanMatcher.matchesBean(value, returnType).withClassSpecificMatchers(classSpecificMatchers));
	}

	private Class<?> getRelevantSuperClass(Class<?> clazz) {
		// first check whether class itself is relevant
		if (classSpecificMatchers.containsKey(clazz)) {
			return clazz;
		}
		
		// then check implemented interfaces
		for (Class<?> iface : clazz.getInterfaces()) {
			Class<?> relevantInterface = getRelevantSuperClass(iface);
			if (relevantInterface != null) {
				return relevantInterface;
			}
		}
		
		// finally, recurse to superclass
		if (clazz.getSuperclass() != null) {
			return getRelevantSuperClass(clazz.getSuperclass());
		} else {
			return null;
		}
	}

	private String getPropertyName(Method getter) {
		return StringUtils.uncapitalize(StringUtils.substring(getter.getName(), "get".length()));
	}

	private <S> Set<Method> getGetterMethods(Class<S> clazz) {
		return ReflectionUtils.getAllMethods(clazz, withModifier(Modifier.PUBLIC), withPrefix("get"), withParametersCount(0));
	}
}