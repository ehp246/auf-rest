package me.ehp246.aufrest.api.spi;

import java.util.Objects;

import me.ehp246.aufrest.api.rest.BodyOf;

/**
 * @author Lei Yang
 * @since 4.0
 */
public final class BodyOfBuilder {
    private BodyOfBuilder() {
        super();
    }

    public static <T> BodyOf<T> of(final Class<T> type) {
        return new BodyOf<>(null, type);
    }

    public static <T> BodyOf<T> ofView(final Class<?> view, final Class<T> type) {
        return new BodyOf<>(view, type);
    }

    public static <T> BodyOf<T> ofView(final Class<?> view, final Class<T> type, final Class<?>... parameters) {
        Objects.requireNonNull(parameters);

        final var all = new Class<?>[parameters.length + 1];
        all[0] = type;
        System.arraycopy(parameters, 0, all, 1, parameters.length);

        return new BodyOf<>(view, all);
    }
    /**
     *
     * @param parameters Required. Can not be <code>null</code>
     */
    public static <T> BodyOf<T> of(final Class<T> type, final Class<?>... parameters) {
        return ofView(null, type, parameters);
    }

}
