package me.ehp246.aufrest.api.spi;

import java.lang.annotation.Annotation;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ReifyingBody;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Defines the details of a value object outside of the value itself. It could
 * include the declared type, annotations, and etc. E.g., the object is the body
 * of a {@linkplain RestRequest}. Used to support serialization features. E.g.,
 * {@linkplain JsonView} support, and declared type recognition for
 * {@linkplain ObjectMapper#writerFor(Class)}.
 *
 * @author Lei Yang
 *
 */
public sealed class DeclarationDescriptor {
    protected final Class<?> type;
    protected final Map<Class<? extends Annotation>, Annotation> map;

    public DeclarationDescriptor(final Class<?> type, final Annotation[] annotations) {
        this.type = type;
        this.map = annotations == null ? Map.of()
                : Arrays.asList(annotations).stream()
                        .collect(Collectors.toUnmodifiableMap(Annotation::annotationType, Function.identity()));
    }

    /**
     * The declared type of the targeted object. Could be different from the runtime
     * type.
     */
    public Class<?> type() {
        return this.type;
    }

    public Map<Class<? extends Annotation>, Annotation> map() {
        return this.map;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T get(final Class<T> annotationType) {
        return (T) this.map.get(annotationType);
    }

    public sealed static class JsonViewDescriptor extends DeclarationDescriptor {
        private final Class<?> viewValue;

        public JsonViewDescriptor(final Class<?> type, final Annotation[] annotations) {
            super(type, annotations);
            this.viewValue = Optional.ofNullable(map.get(JsonView.class)).map(ann -> (JsonView) ann)
                    .map(JsonView::value).filter(value -> value.length > 0).map(value -> value[0]).orElse(null);
        }

        public Class<?> view() {
            return this.viewValue;
        }
    }

    /**
     * Defines the type information needed for de-serialization of the response
     * body. Mostly to be used to support a generic container, e.g., {@link List}.
     * <p>
     * The {@linkplain DeclarationDescriptor#type} might not be the de-serialization
     * target type. E.g., it could be {@linkplain HttpResponse} from the return of a
     * {@linkplain ByRest} method. In such cases, the
     * {@linkplain ReifyingBodyDescriptor#reifying} must define the actual response
     * body type.
     *
     * @author Lei Yang
     * @since 3.2
     */
    public final static class ReifyingBodyDescriptor extends JsonViewDescriptor {
        // Could be null
        private final Class<?> errorType;
        // null if annotation is missing
        private final List<Class<?>> reifying;
        private final Class<?> bodyType;

        public ReifyingBodyDescriptor(final Class<?> type) {
            this(type, null, null);
        }

        public ReifyingBodyDescriptor(final Class<?> type, final Class<?> errorType, final Annotation[] annotations) {
            super(type, annotations);
            this.errorType = errorType;
            this.reifying = Optional.ofNullable(this.get(ReifyingBody.class)).map(ReifyingBody::value).map(List::of)
                    .orElse(null);
            this.bodyType = this.reifying == null ? this.type : this.reifying.get(0);
        }

        public Class<?> bodyType() {
            return this.bodyType;
        }

        /**
         * Returns un-modifiable list of the value of {@linkplain ReifyingBody} or
         * <code>null</code> if there is no such annotation.
         * <p>
         * If the list is not <code>null</code>, it should contain at least one value
         * defining the {@linkplain ReifyingBodyDescriptor#bodyType}.
         */
        public List<Class<?>> reifying() {
            return this.reifying;
        }

        /**
         * The type used to de-serialize the response body when the status code is
         * larger than 300.
         *
         */
        public Class<?> errorType() {
            return this.errorType;
        }
    }
}