package de.hsudbrock.beanmatcher;

import org.junit.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;

/**
 * Unit tests for the {@link BeanMatcher}.
 */
public class BeanMatcherTest {

	@Test
	public void test() {
		TestBean reference = new TestBean();
		reference.setBooleanValue(Boolean.TRUE);
		reference.setLongValue(Long.valueOf(10));
		reference.setPrimitiveBooleanValue(false);
		reference.setString("hello world");
		reference.setStringList(Lists.newArrayList("a", "b"));
		reference.setStringSet(Sets.newHashSet("x", "y"));
		
		TestBean actual = new TestBean();
		actual.setBooleanValue(Boolean.TRUE);
		actual.setLongValue(Long.valueOf(10));
		actual.setPrimitiveBooleanValue(false);
		actual.setString("hello world");
		actual.setStringList(Lists.newArrayList("a", "b"));
		actual.setStringSet(Sets.newHashSet("x", "y"));
		
		assertThat(actual, BeanMatcher.matchesBean(reference, TestBean.class));
	}
	
}
