package me.ehp246.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

/**
 * @author Lei Yang
 *
 */
public class InvocationUtil {
    @SuppressWarnings("unchecked")
    public static <T> T newProxy(final Class<T> t, final Consumer<Invocation> consumer) {
        return (T) (Proxy.newProxyInstance(InvocationUtil.class.getClassLoader(), new Class[] { t },
                (proxy, method, args) -> {
                    consumer.accept(new Invocation() {

                        @Override
                        public Object target() {
                            return proxy;
                        }

                        @Override
                        public Method method() {
                            return method;
                        }

                        @Override
                        public Object[] args() {
                            return args;
                        }
                    });
                    return null;
                }));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInvocation(final Class<T> t, final Invocation[] captured) {
        return (T) (Proxy.newProxyInstance(InvocationUtil.class.getClassLoader(), new Class[] { t },
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
    }

    @SuppressWarnings("unchecked")
    public static <T> InvocationCaptor<T> newCaptor(final Class<T> t) {
        final var captured = new Invocation[1];
        final var proxy = (T) (Proxy.newProxyInstance(InvocationUtil.class.getClassLoader(), new Class[] { t },
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

    public static Invocation toInvocation(Object proxy, Method method, Object[] args) {
        return new Invocation() {

            @Override
            public Object target() {
                return proxy;
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
    }

}
