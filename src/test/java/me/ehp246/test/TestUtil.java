package me.ehp246.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Lei Yang
 *
 */
public class TestUtil {
    @SuppressWarnings("unchecked")
    public static <T> InvocationCaptor<T> newCaptor(final Class<T> t) {
        final var captured = new Invocation[1];
        final var proxy = (T) (Proxy.newProxyInstance(TestUtil.class.getClassLoader(), new Class[] { t },
                (target, method, args) -> {
                    captured[0] = new Invocation() {

                        @Override
                        public Object target() {
                            return target;
                        }

                        @Override
                        public Method method() {
                            return method;
                        }

                        @Override
                        public Object[] args() {
                            return args;
                        }
                    };
                    return null;
                }));

        return new InvocationCaptor<T>() {

            @Override
            public T proxy() {
                return proxy;
            }

            @Override
            public Invocation invocation() {
                return captured[0];
            }
        };
    }

    public interface InvocationCaptor<T> {
        T proxy();

        Invocation invocation();
    }
}
