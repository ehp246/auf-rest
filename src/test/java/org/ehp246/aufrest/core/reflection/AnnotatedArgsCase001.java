package org.ehp246.aufrest.core.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
interface AnnotatedArgsCase001 {
	void m001(@AnnotatedArg("arg1") String str1, @AnnotatedArg("arg2") String str2, @AnnotatedArg("arg3") String str3);

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface AnnotatedArg {
		String value();
	}
}
