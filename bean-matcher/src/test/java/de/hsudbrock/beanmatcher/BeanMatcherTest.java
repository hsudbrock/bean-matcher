package de.hsudbrock.beanmatcher;

import org.junit.*;

import com.google.common.collect.*;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link BeanMatcher}.
 */
public class BeanMatcherTest {

	@Test
	public void test() {
		TestBean testBean = new TestBean();
		testBean.setString("check");
		testBean.setStringList(Lists.<String>newArrayList("hullo", "hallo"));
		testBean.setStringSet(Sets.newHashSet("hello", "hullo"));
		testBean.setLongValue(Long.valueOf(13));
		testBean.setInnerBean(new TestInnerBean(36, true));
		
		TestBean otherBean = new TestBean();
		otherBean.setString("check");
		otherBean.setStringList(Lists.<String>newArrayList("hullo", "hallo"));
		otherBean.setStringSet(Sets.newHashSet("hullo", "hello"));
		otherBean.setInnerBean(new TestInnerBean(36, true));
		otherBean.setLongValue(Long.valueOf(132));
		
		assertThat(testBean, BeanMatcher.matchesBean(otherBean, TestBean.class, IgnoreBeanMatcherSpec.ignoreProperty("longVale")));
	}
	
}
