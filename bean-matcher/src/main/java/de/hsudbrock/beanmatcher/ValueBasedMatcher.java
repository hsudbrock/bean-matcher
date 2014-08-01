package de.hsudbrock.beanmatcher;

import org.hamcrest.*;

import com.google.common.base.*;

public interface ValueBasedMatcher<T> extends Function<T, Matcher<T>>{

}
