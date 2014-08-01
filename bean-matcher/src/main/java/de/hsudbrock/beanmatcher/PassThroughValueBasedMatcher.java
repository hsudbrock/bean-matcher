package de.hsudbrock.beanmatcher;


public abstract class PassThroughValueBasedMatcher<T> implements ValueBasedMatcher<T> {
	
	@SuppressWarnings("rawtypes")
	private BeanMatcherConfigurer beanMatcherConfigurer;

	@SuppressWarnings("unchecked")
	protected <S> BeanMatcher<S> configuredBeanMatcher(BeanMatcher<S> unconfiguredBeanMatcher) {
		return (BeanMatcher<S>) beanMatcherConfigurer.apply(unconfiguredBeanMatcher);
	}
	
	public void setBeanMatcherConfigurer(@SuppressWarnings("rawtypes") BeanMatcherConfigurer beanMatcherConfigurer) {
		this.beanMatcherConfigurer = beanMatcherConfigurer;
	}
}
