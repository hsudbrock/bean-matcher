package de.hsudbrock.beanmatcher;

import static de.hsudbrock.beanmatcher.BeanMatcher.matchesBean;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.hamcrest.*;
import org.junit.*;

import com.google.common.collect.*;

/**
 * Unit tests for the {@link BeanMatcher}.
 */
public class BeanMatcherTest {
	
	private TestBean reference;
	private TestBean actual;
	
	@Before
	public void setup() {
		this.reference = createTestBean();
		this.actual = createTestBean();
	}
	
    // -----------------------------------------------------------------------------------------------------------------
    // Tests Match
    // -----------------------------------------------------------------------------------------------------------------

	@Test
	public void testDefaultBeanMatcherMatch() {
		assertThat(actual, matchesBean(reference, TestBean.class));
	}
	
    // -----------------------------------------------------------------------------------------------------------------
    // Tests no match
    // -----------------------------------------------------------------------------------------------------------------
	
	@Test
	public void testDefaultBeanMatcherNoStringMatch() {
		actual.setString("other String");
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoBooleanMatch() {
		actual.setBooleanValue(Boolean.FALSE);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoLongMatch() {
		actual.setLongValue(Long.valueOf(-1));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoPrimitiveBooleanMatch() {
		actual.setPrimitiveBooleanValue(false);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoListMatch() {
		actual.setStringList(Lists.newArrayList("b", "a"));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoSetMatch() {
		actual.setStringSet(Sets.newHashSet("x", "y", "z"));
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	@Test
	public void testDefaultBeanMatcherNoMatchingInnerBean() {
		TestInnerBean otherInnerBean = createTestInnerBean();
		otherInnerBean.setString("blabla");
		actual.setInnerBean(otherInnerBean);
		assertThat(actual, not(matchesBean(reference, TestBean.class)));
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // Tests class-based matcher
    // -----------------------------------------------------------------------------------------------------------------
	
	@Test
	public void testClassBasedMatch() {
		actual.setLongValue(Long.valueOf(532));
		for (TestBean innerBean : actual.getInnerBean().getTestBeanList()) {
			innerBean.setLongValue(Long.valueOf(532));
		}
		assertThat(actual, matchesBean(reference, TestBean.class).withClassSpecificMatcher(equalTo(Long.valueOf(532)), Long.class));
	}
	
	@Test
	public void testClassBasedNoMatch() {
		assertThat(actual, not(matchesBean(reference, TestBean.class).withClassSpecificMatcher(equalTo(Long.valueOf(532)), Long.class)));
	}
	
	@Test
	public void testValueSpecificMatch() {
		assertThat(actual, matchesBean(reference, TestBean.class).withValueSpecificMatcher(new ValueBasedMatcher<TestInnerBean>() {
			@Override public Matcher<TestInnerBean> apply(TestInnerBean input) {
				return matchesBean(reference.getInnerBean(), TestInnerBean.class);
			}
		}, TestInnerBean.class));
	}
	
	@Test
	public void testValueSpecificNoMatch() {
		assertThat(actual, not(matchesBean(reference, TestBean.class).withValueSpecificMatcher(new ValueBasedMatcher<TestInnerBean>() {
			@Override public Matcher<TestInnerBean> apply(TestInnerBean input) {
				TestInnerBean innerBean = new TestInnerBean();
				innerBean.setTestBeanList(null);
				return matchesBean(innerBean, TestInnerBean.class);
			}
		}, TestInnerBean.class)));
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
