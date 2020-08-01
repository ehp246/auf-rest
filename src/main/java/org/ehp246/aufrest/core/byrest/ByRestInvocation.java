package org.ehp246.aufrest.core.byrest;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.ehp246.aufrest.api.annotation.ByRest;
import org.ehp246.aufrest.api.annotation.OfMapping;
import org.ehp246.aufrest.api.rest.HttpUtil;
import org.ehp246.aufrest.api.rest.Request;
import org.ehp246.aufrest.api.rest.Response;
import org.ehp246.aufrest.core.reflection.ObjectToText;
import org.ehp246.aufrest.core.reflection.ProxyInvoked;
import org.ehp246.aufrest.core.reflection.TextToObject;
import org.ehp246.aufrest.core.util.InvocationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Lei Yang
 *
 */
class ByRestInvocation implements Request {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByRestInvocation.class);
	private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Set.of(PathVariable.class,
			RequestParam.class);
	private final ProxyInvoked<Object> invoked;
	private final ObjectToText fromObject;
	private final TextToObject fromText;
	private final Environment env;
	private final Optional<OfMapping> ofMapping;

	public ByRestInvocation(final ProxyInvoked<Object> invoked, final Environment env, final TextToObject fromText,
			final ObjectToText toText) {
		super();
		this.invoked = invoked;
		this.env = env;
		this.fromText = fromText;
		this.fromObject = toText;
		this.ofMapping = invoked.findOnMethod(OfMapping.class);
	}

	@Override
	public String method() {
		if (ofMapping.isPresent()) {
			return ofMapping.map(anno -> anno.method().name()).get();
		}

		final var invokedMethodName = invoked.getMethodName().toUpperCase();
		return HttpUtil.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny()
				.orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public String uri() {
		final var base = env
				.resolveRequiredPlaceholders(invoked.findOnDeclaringClass(ByRest.class).map(ByRest::value).get());
		final var path = ofMapping.map(OfMapping::value).orElse("");
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
				.collect(Collectors.toMap(e -> InvocationUtil.invoke(() -> URLEncoder.encode(e.getKey(), "UTF-8")),
						e -> InvocationUtil
								.invoke(() -> List.of(URLEncoder.encode(e.getValue().toString(), "UTF-8")))))))
				.buildAndExpand(pathParams).toUriString();
	}

	@Override
	public Object body() {
		final var payload = invoked.filterPayloadArgs(PARAMETER_ANNOTATIONS);
		return fromObject.apply(() -> payload.size() == 1 ? payload.get(0) : payload);
	}

	@Override
	public BodyHandler<?> bodyHandler() {
		final var returnType = invoked.getReturnType();
		if (returnType == Void.class || returnType == void.class) {
			return BodyHandlers.discarding();
		}
		if (returnType == InputStream.class) {
			return BodyHandlers.ofInputStream();
		}
		if (returnType == Response.class || returnType == HttpResponse.class) {
			return Request.super.bodyHandler();
		}

		return BodyHandlers.ofString();
	}

	public Object setResponseSupplier(final Supplier<Response> resSupplier) throws Throwable {
		final var returnType = invoked.getReturnType();

		// Request still should go out but discarding the response.
		if (returnType == Void.class || returnType == void.class) {
			resSupplier.get();
			return null;
		}

		if (returnType == Response.class) {
			return resSupplier.get();
		}

		if (returnType == HttpResponse.class) {
			return resSupplier.get().received();
		}

		if (returnType == InputStream.class) {
			return resSupplier.get().received().body();
		}

		if (returnType == CompletableFuture.class) {
			return CompletableFuture.supplyAsync(resSupplier::get);
		}

		return fromText.apply(resSupplier.get().received().body().toString(), new TextToObject.Receiver<>() {

			@Override
			public Class<?> type() {
				return returnType;
			}

		});
	}
}
