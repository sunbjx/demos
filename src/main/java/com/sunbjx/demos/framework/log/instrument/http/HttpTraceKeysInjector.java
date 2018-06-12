package com.sunbjx.demos.framework.log.instrument.http;



import com.sunbjx.demos.framework.log.Span;
import com.sunbjx.demos.framework.log.TraceKeys;
import com.sunbjx.demos.framework.log.Tracer;

import java.net.URI;

/**
 * Injects HTTP related keys to the current span.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.1
 */
public class HttpTraceKeysInjector {

	private final Tracer tracer;
	private final TraceKeys traceKeys;

	public HttpTraceKeysInjector(Tracer tracer, TraceKeys traceKeys) {
		this.tracer = tracer;
		this.traceKeys = traceKeys;
	}

	/**
	 * Adds tags from the HTTP request to the current Span
	 */
	public void addRequestTags(String url, String host, String path, String method) {
		this.tracer.addTag(this.traceKeys.getHttp().getRequestURL(), url);
		this.tracer.addTag(this.traceKeys.getHttp().getRemoteHost(), host);
		this.tracer.addTag(this.traceKeys.getHttp().getRequestURI(), path);
		this.tracer.addTag(this.traceKeys.getHttp().getMethod(), method);
	}

	/**
	 * Adds tags from the HTTP request to the given Span
	 */
	public void addRequestTags(Span span, String url, String host, String path, String method) {
		tagSpan(span, this.traceKeys.getHttp().getRequestURL(), url);
		tagSpan(span, this.traceKeys.getHttp().getRemoteHost(), host);
		tagSpan(span, this.traceKeys.getHttp().getRequestURI(), path);
		tagSpan(span, this.traceKeys.getHttp().getMethod(), method);
	}

	/**
	 * Adds tags from the HTTP request to the given Span
	 */
	public void addRequestTags(Span span, URI uri, String method) {
		addRequestTags(span, uri.toString(), uri.getHost(), uri.getPath(), method);
	}


	/**
	 * Add a tag to the given, exportable Span
	 */
	public void tagSpan(Span span, String key, String value) {
		if (span != null && span.isExportable()) {
			span.tag(key, value);
		}
	}


}
