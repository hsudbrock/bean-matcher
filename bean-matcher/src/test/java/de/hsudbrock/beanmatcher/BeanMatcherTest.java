package de.hsudbrock.beanmatcher;

import static de.hsudbrock.beanmatcher.BeanMatcher.matchesBean;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import org.junit.*;

import com.google.common.collect.*;

/**
 * Unit tests for the {@link BeanMatcher}.
 */
public class BeanMatcherTest {
	
    // -----------------------------------------------------------------------------------------------------------------
    // Tests Match
    // -----------------------------------------------------------------------------------------------------------------

	@Test
	public void testDefaultBeanMatcherMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		assertThat(actual, matchesBean(reference, TestBean.class));
	}
	
    // -----------------------------------------------------------------------------------------------------------------
    // Tests no match
    // -----------------------------------------------------------------------------------------------------------------
	
	@Test
	public void testDefaultBeanMatcherNoStringMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setString("other String");
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoBooleanMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setBooleanValue(Boolean.FALSE);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoLongMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setLongValue(Long.valueOf(-1));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoPrimitiveBooleanMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setPrimitiveBooleanValue(false);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoListMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setStringList(Lists.newArrayList("b", "a"));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoSetMatch() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		actual.setStringSet(Sets.newHashSet("x", "y", "z"));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoMatchingInnerBean() {
		TestBean reference = createTestBean();
		TestBean actual = createTestBean();
		
		TestInnerBean otherInnerBean = createTestInnerBean();
		otherInnerBean.setString("blabla");
		actual.setInnerBean(otherInnerBean);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
    // -----------------------------------------------------------------------------------------------------------------
    // Implementation
    // -----------------------------------------------------------------------------------------------------------------
	
	
	private TestBean createTestBean() {
		return createTestBean(true);
	}
	
	private TestBean createTestBean(boolean includeInnerBean) {
		TestBean reference = new TestBean();
		reference.setBooleanValue(Boolean.TRUE);
		reference.setLongValue(Long.valueOf(10));
		reference.setPrimitiveBooleanValue(true);
		reference.setString("hello world");
		reference.setStringList(Lists.newArrayList("a", "b"));
		reference.setStringSet(Sets.newHashSet("x", "y"));
		
		if (includeInnerBean) {
			reference.setInnerBean(createTestInnerBean());
		}
		
		return reference;
	}

	private TestInnerBean createTestInnerBean() {
		TestInnerBean reference = new TestInnerBean();
		reference.setBooleanValue(Boolean.TRUE);
		reference.setPrimitiveLongValue(13);
		reference.setString("hello");
		reference.setTestBeanList(Lists.newArrayList(createTestBean(false), createTestBean(false)));
		
		return reference;
	}
}
