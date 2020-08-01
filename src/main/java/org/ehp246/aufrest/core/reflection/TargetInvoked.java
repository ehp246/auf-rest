package org.ehp246.aufrest.core.reflection;

import java.lang.reflect.Method;

public interface TargetInvoked {
	Object getTarget();

	Method getMethod();

	Object[] getArguments();

	InvocationOutcome getResult();
}
