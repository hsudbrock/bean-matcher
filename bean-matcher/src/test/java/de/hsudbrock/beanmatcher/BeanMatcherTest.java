package de.hsudbrock.beanmatcher;

import static de.hsudbrock.beanmatcher.BeanMatcher.matchesBean;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.*;

import com.google.common.collect.*;

/**
 * Unit tests for the {@link BeanMatcher}.
 */
public class BeanMatcherTest {

	@Test
	public void testDefaultBeanMatcher() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		assertThat(actual, matchesBean(reference, TestBean.class));
	}
	
	
	private TestBean createTestBean() {
		TestBean reference = new TestBean();
		reference.setBooleanValue(Boolean.TRUE);
		reference.setLongValue(Long.valueOf(10));
		reference.setPrimitiveBooleanValue(false);
		reference.setString("hello world");
		reference.setStringList(Lists.newArrayList("a", "b"));
		reference.setStringSet(Sets.newHashSet("x", "y"));
		
		return reference;
	}
}
