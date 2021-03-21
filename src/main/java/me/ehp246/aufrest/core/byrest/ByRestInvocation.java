package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InvokedOn;
import me.ehp246.aufrest.api.rest.RequestByRest;
import me.ehp246.aufrest.api.rest.ResponseByRest;
import me.ehp246.aufrest.core.reflection.AnnotatedArgument;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 1.0
 *
 */
class ByRestInvocation implements RequestByRest {
	private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(PathVariable.class,
			RequestParam.class, RequestHeader.class);

	private final String id = UUID.randomUUID().toString();
	private final ProxyInvoked<Object> invoked;
	private final Environment env;
	private final Optional<OfMapping> ofMapping;
	private final Optional<ByRest> byRest;
	private Supplier<ResponseByRest> responseSupplier = null;

	public ByRestInvocation(final ProxyInvoked<Object> invoked, final Environment env) {
		super();
		this.invoked = invoked;
		this.env = env;
		this.ofMapping = invoked.findOnMethod(OfMapping.class);
		this.byRest = invoked.findOnDeclaringClass(ByRest.class);
	}

	@Override
	public String method() {
		return ofMapping.map(OfMapping::method).filter(OneUtil::hasValue).or(() -> {
			final var invokedMethodName = invoked.getMethodName().toUpperCase();
			return HttpUtils.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny();
		}).map(String::toUpperCase).orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String accept() {
		return ofMapping.map(OfMapping::accept).orElseGet(RequestByRest.super::accept);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String uri() {
		final var base = env.resolveRequiredPlaceholders(byRest.map(ByRest::value).get());
		final var path = ofMapping.map(OfMapping::value).filter(OneUtil::hasValue).map(value -> "/" + value).orElse("");
		final var pathParams = invoked.mapAnnotatedArguments(PathVariable.class, PathVariable::value);
		final var unnamedPathMap = pathParams.get("");
		if (unnamedPathMap != null && unnamedPathMap instanceof Map) {
			((Map<String, Object>) unnamedPathMap).entrySet().stream()
					.forEach(entry -> pathParams.putIfAbsent(entry.getKey(), entry.getValue()));
		}

		final var queryParams = invoked.mapAnnotatedArguments(RequestParam.class, RequestParam::value);
		final var unnamedQueryMap = queryParams.get("");
		if (unnamedQueryMap != null && unnamedQueryMap instanceof Map) {
			queryParams.remove("");
			((Map<String, Object>) unnamedQueryMap).entrySet().stream()
					.forEach(e -> queryParams.putIfAbsent(e.getKey(), e.getValue()));
		}

		return UriComponentsBuilder.fromUriString(base + path).queryParams(CollectionUtils.toMultiValueMap(queryParams
				.entrySet().stream()
				.collect(Collectors.toMap(e -> OneUtil.orThrow(() -> URLEncoder.encode(e.getKey(), "UTF-8")),
						e -> OneUtil.orThrow(() -> List.of(URLEncoder.encode(e.getValue().toString(), "UTF-8")))))))
				.buildAndExpand(pathParams).toUriString();
	}

	@Override
	public Object body() {
		final var payload = invoked.filterPayloadArgs(PARAMETER_ANNOTATIONS);
		return payload.size() >= 1 ? payload.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> headers() {
		final var headers = new HashMap<String, List<String>>();

		invoked.streamOfAnnotatedArguments(RequestHeader.class)
				.forEach(new Consumer<AnnotatedArgument<RequestHeader>>() {
					@Override
					public void accept(final AnnotatedArgument<RequestHeader> annoArg) {
						newValue(annoArg.getAnnotation().value(), annoArg.getArgument());
					}

					private void newValue(final Object key, final Object newValue) {
						if (newValue == null) {
							return;
						}

						if (newValue instanceof Iterable) {
							((Iterable<Object>) newValue).forEach(v -> newValue(key, v));
							return;
						}

						if (newValue instanceof Map) {
							((Map<Object, Object>) newValue).entrySet().forEach(entry -> {
								newValue(entry.getKey(), entry.getValue());
							});
							return;
						}

						getMapped(key).add(newValue.toString());
					}

					private List<String> getMapped(final Object key) {
						return headers.computeIfAbsent(key.toString(), k -> new ArrayList<String>());
					}
				});

		return headers;
	}

	public ByRestInvocation setResponseSupplier(final Supplier<ResponseByRest> responseSupplier) throws Throwable {
		this.responseSupplier = responseSupplier;
		return this;
	}

	@Override
	public String contentType() {
		return ofMapping.map(OfMapping::contentType).orElse(RequestByRest.super.contentType());
	}

	public Object returnInvocation() throws Throwable {
		if (!invoked.getReturnType().isAssignableFrom(CompletableFuture.class)) {
			return onRestResponse(responseSupplier.get());
		}

		final var context = HeaderContext.map();
		return CompletableFuture.supplyAsync(() -> {
			HeaderContext.set(context);
			try {
				final var httpResponse = responseSupplier.get().httpResponse();
				final var reifying = invoked.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {});
				if (reifying.length > 0 && reifying[0].isAssignableFrom(HttpResponse.class)) {
					return httpResponse;
				}
				return httpResponse.body();
			} finally {
				HeaderContext.clear();
			}
		});
	}

	private Object onRestResponse(final ResponseByRest response) {
		final var returnType = invoked.getReturnType();
		final var httpResponse = response.httpResponse();

		if (returnType.isAssignableFrom(ResponseByRest.class)) {
			return response;
		}

		// If the return type is HttpResponse, returns it as is without any processing
		// regardless the status.
		if (returnType.isAssignableFrom(HttpResponse.class)) {
			return httpResponse;
		}

		if (httpResponse.statusCode() >= 300) {
			throw new UnhandledResponseException(this, httpResponse);
		}

		// Request still should go out but discarding the response.
		if (returnType == Void.class || returnType == void.class) {
			return null;
		}

		return httpResponse.body();
	}

	@Override
	public InvokedOn invokedOn() {
		return this.invoked;
	}
}
