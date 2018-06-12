/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunbjx.demos.framework.log.instrument.http;

import com.sunbjx.demos.framework.log.Span;
import com.sunbjx.demos.framework.log.SpanExtractor;
import com.sunbjx.demos.framework.log.TraceKeys;
import com.sunbjx.demos.framework.log.Tracer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.sort;

/**
 * Filter that takes the value of the {@link Span#SPAN_ID_NAME} and
 * {@link Span#TRACE_ID_NAME} header from either request or response and uses them to
 * create a new span.
 *
 * <p>
 * In order to keep the size of spans manageable, this only add tags defined in
 * {@link TraceKeys}. If you need to add additional tags, such as headers subtype this and
 * override {@link #addRequestTags} or {@link #addResponseTags}.
 *
 * @author Jakub Nabrdalik, 4financeIT
 * @author Tomasz Nurkiewicz, 4financeIT
 * @author Marcin Grzejszczak
 * @author Spencer Gibb
 * @author Dave Syer
 * @see Tracer
 * @see TraceKeys
 * @see TraceWebAutoConfiguration#traceFilter
 * @since 1.0.0
 */
@Order(HttpTraceFilter.ORDER)
public class HttpTraceFilter extends GenericFilterBean {

	private static final Log log = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private static final String REQUEST_REMOTE_ADDRESS_ATTR = "REQUEST_REMOTE_ADDRESS_ATTR";

	private static final String HTTP_COMPONENT = "http";

	protected static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 5;

	protected static final String TRACE_REQUEST_ATTR = HttpTraceFilter.class.getName() + ".TRACE";

	protected static final String TRACE_ERROR_HANDLED_REQUEST_ATTR = HttpTraceFilter.class.getName() + ".ERROR_HANDLED";

	public static final String
			DEFAULT_SKIP_PATTERN
			= "/api-docs.*|/autoconfig|/configprops|/dump|/health|/info|/metrics.*|/mappings|/trace|/swagger.*|.*\\.png|.*\\.css|.*\\.js|.*\\.html|/favicon.ico|/hystrix.stream";

	private final Tracer tracer;
	private final SpanExtractor<HttpServletRequest> spanExtractor;
	private final TraceKeys traceKeys;


	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private final HttpTraceKeysInjector httpTraceKeysInjector;


	public HttpTraceFilter(Tracer tracer) {
		this.tracer = tracer;
		this.spanExtractor = new HttpServletRequestExtractor();
		this.traceKeys = TraceKeys.getInstance();
		this.httpTraceKeysInjector = new HttpTraceKeysInjector(this.tracer, this.traceKeys);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
			throw new ServletException("Filter just supports HTTP requests");
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String uri = this.urlPathHelper.getPathWithinApplication(request);
		Span spanFromRequest = getSpanFromAttribute(request);
		if (spanFromRequest != null) {
			continueSpan(request, spanFromRequest);
		}
		if (log.isDebugEnabled()) {
			log.debug("Received a request to uri [" + uri + "] ");
		}
		putHttpInfoMDC(request);
		// in case of a response with exception status a exception controller will close the span
		if (!httpStatusSuccessful(response) && isSpanContinued(request)) {
			processErrorRequest(filterChain, request, response, spanFromRequest);
			return;
		}
		String name = HTTP_COMPONENT + ":" + uri;
		try {
			spanFromRequest = createSpan(request, spanFromRequest, name);
		} catch (IllegalArgumentException e) {
			filterChain.doFilter(request, response);
			response.sendError(HttpStatus.BAD_REQUEST.value(), "Exception tracing request [" + e.getMessage() + "]");
			return;
		}
		Throwable exception = null;
		try {
			filterChain.doFilter(request, response);
		} catch (Throwable e) {
			exception = e;
			throw e;
		} finally {
			if (isAsyncStarted(request) || request.isAsyncStarted()) {
				if (log.isDebugEnabled()) {
					log.debug("The span " + spanFromRequest + " will get detached by a HandleInterceptor");
				}
				// TODO: how to deal with response annotations and async?
				return;
			}
			spanFromRequest = createSpanIfRequestNotHandled(request, spanFromRequest, name);
			detachOrCloseSpans(request, response, spanFromRequest, exception);
		}
	}

	private void processErrorRequest(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response,
                                     Span spanFromRequest) throws IOException, ServletException {
		if (log.isDebugEnabled()) {
			log.debug("The span [" + spanFromRequest + "] was already detached once and we're processing an error");
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			request.setAttribute(TRACE_ERROR_HANDLED_REQUEST_ATTR, true);
			addResponseTags(response, null);
			this.tracer.close(spanFromRequest);
		}
	}

	private void continueSpan(HttpServletRequest request, Span spanFromRequest) {
		this.tracer.continueSpan(spanFromRequest);
		request.setAttribute(TraceRequestAttributes.SPAN_CONTINUED_REQUEST_ATTR, "true");
		if (log.isDebugEnabled()) {
			log.debug("There has already been a span in the request " + spanFromRequest);
		}
	}

	// This method is a fallback in case if handler interceptors didn't catch the request.
	// In that case we are creating an artificial span so that it can be visible in Zipkin.
	private Span createSpanIfRequestNotHandled(HttpServletRequest request, Span spanFromRequest, String name) {
		if (!requestHasAlreadyBeenHandled(request)) {
			spanFromRequest = this.tracer.createSpan(name);
			request.setAttribute(TRACE_REQUEST_ATTR, spanFromRequest);
		}
		return spanFromRequest;
	}

	private boolean requestHasAlreadyBeenHandled(HttpServletRequest request) {
		return request.getAttribute(TraceRequestAttributes.HANDLED_SPAN_REQUEST_ATTR) != null;
	}

	private void detachOrCloseSpans(HttpServletRequest request, HttpServletResponse response, Span spanFromRequest, Throwable exception) {
		Span span = spanFromRequest;
		if (span != null) {
			addResponseTags(response, exception);
			if (span.hasSavedSpan() && requestHasAlreadyBeenHandled(request)) {
				recordParentSpan(span.getSavedSpan());
			} else if (!requestHasAlreadyBeenHandled(request)) {
				span = this.tracer.close(span);
			}
			recordParentSpan(span);
			// in case of a response with exception status will close the span when exception dispatch is handled
			if (httpStatusSuccessful(response)) {
				if (log.isDebugEnabled()) {
					log.debug("Closing the span " + span + " since the response was successful");
				}
				this.tracer.close(span);
			} else if (errorAlreadyHandled(request)) {
				if (log.isDebugEnabled()) {
					log.debug("Won't detach the span " + span + " since error has already been handled");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Detaching the span " + span + " since the response was unsuccessful");
				}
				this.tracer.detach(span);
			}
		}
	}

	private void recordParentSpan(Span parent) {
		if (parent == null) {
			return;
		}
		if (parent.isRemote()) {
			if (log.isDebugEnabled()) {
				log.debug("Trying to send the parent span " + parent + " to Zipkin");
			}
			parent.stop();
			parent.logEvent(Span.SERVER_SEND);
		} else {
			parent.logEvent(Span.SERVER_SEND);
		}
	}

	private boolean httpStatusSuccessful(HttpServletResponse response) {
		if (response.getStatus() == 0) {
			return false;
		}
		HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
		return httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection();
	}

	private Span getSpanFromAttribute(HttpServletRequest request) {
		return (Span) request.getAttribute(TRACE_REQUEST_ATTR);
	}

	private boolean errorAlreadyHandled(HttpServletRequest request) {
		return Boolean.valueOf(String.valueOf(request.getAttribute(TRACE_ERROR_HANDLED_REQUEST_ATTR)));
	}

	private boolean isSpanContinued(HttpServletRequest request) {
		return getSpanFromAttribute(request) != null;
	}

	/**
	 * In order not to send unnecessary data we're not adding request tags to the server
	 * side spans. All the tags are there on the client side.
	 */
	private void addRequestTagsForParentSpan(HttpServletRequest request, Span spanFromRequest) {
		if (spanFromRequest.getName().contains("parent")) {
			addRequestTags(spanFromRequest, request);
		}
	}

	/**
	 * Creates a span and appends it as the current request's attribute
	 */
	private Span createSpan(HttpServletRequest request, Span spanFromRequest, String name) {
		if (spanFromRequest != null) {
			if (log.isDebugEnabled()) {
				log.debug("Span has already been created - continuing with the previous one");
			}
			return spanFromRequest;
		}
		Span parent = this.spanExtractor.joinTrace(request);
		if (parent != null) {
			if (log.isDebugEnabled()) {
				log.debug("Found a parent span " + parent + " in the request");
			}
			addRequestTagsForParentSpan(request, parent);
			spanFromRequest = parent;
			this.tracer.continueSpan(spanFromRequest);
			if (parent.isRemote()) {
				parent.logEvent(Span.SERVER_RECV);
			}
			request.setAttribute(TRACE_REQUEST_ATTR, spanFromRequest);
			if (log.isDebugEnabled()) {
				log.debug("Parent span is " + parent + "");
			}
		} else {

			spanFromRequest = this.tracer.createSpan(name);

			spanFromRequest.logEvent(Span.SERVER_RECV);
			request.setAttribute(TRACE_REQUEST_ATTR, spanFromRequest);
			if (log.isDebugEnabled()) {
				log.debug("No parent span present - creating a new span");
			}
		}
		return spanFromRequest;
	}

	/**
	 * Override to add annotations not defined in {@link TraceKeys}.
	 */
	protected void addRequestTags(Span span, HttpServletRequest request) {
		String uri = this.urlPathHelper.getPathWithinApplication(request);
		this.httpTraceKeysInjector.addRequestTags(span, getFullUrl(request), request.getServerName(), uri, request.getMethod());

	}


	/**
	 * Override to add annotations not defined in {@link TraceKeys}.
	 */
	protected void addResponseTags(HttpServletResponse response, Throwable e) {
		int httpStatus = response.getStatus();
		if (httpStatus == HttpServletResponse.SC_OK && e != null) {
			// Filter chain threw exception but the response status may not have been set
			// yet, so we have to guess.
			this.tracer.addTag(this.traceKeys.getHttp().getStatusCode(), String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		}
		// only tag valid http statuses
		else if (httpStatus >= 100 && (httpStatus < 200) || (httpStatus > 399)) {
			this.tracer.addTag(this.traceKeys.getHttp().getStatusCode(), String.valueOf(response.getStatus()));
		}
	}

	protected boolean isAsyncStarted(HttpServletRequest request) {
		return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
	}


	private String getFullUrl(HttpServletRequest request) {
		return getRequestURL(request.getRequestURL(), request.getQueryString());
	}


	/**
	 * 取得当前的request URL，包括query string。
	 *
	 * @param withQueryString 是否包含query string
	 * @return 当前请求的request URL
	 */
	private String getRequestURL(StringBuffer requestURL, String queryString) {
		int length = requestURL.length();

		try {
			if (queryString != null) {
				requestURL.append('?').append(queryString);
			}

			return requestURL.toString();
		} finally {
			requestURL.setLength(length);
		}
	}


	private void putHttpInfoMDC(HttpServletRequest request) {
		// GET or POST
		putMDC(traceKeys.getHttp().getMethod(), request.getMethod());

		// request URL：完整的URL
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		putMDC(traceKeys.getHttp().getRequestURL(), getRequestURL(requestURL, null));
		putMDC(traceKeys.getHttp().getRequestURLWithQueryString(), getRequestURL(requestURL, queryString));

		// request URI：不包括host信息的URL
		String requestURI = request.getRequestURI();
		String requestURIWithQueryString = queryString == null ? requestURI : requestURI + "?" + queryString;

		putMDC(traceKeys.getHttp().getRequestURI(), requestURI);
		putMDC(traceKeys.getHttp().getRequestURIWithQueryString(), requestURIWithQueryString);
		putMDC(traceKeys.getHttp().getQueryString(), queryString);

		// client info
		putMDC(traceKeys.getHttp().getRemoteHost(), request.getRemoteHost());
		putMDC(traceKeys.getHttp().getRemoteAddr(), this.getRemoteAddress(request));

		// user agent
		putMDC(traceKeys.getHttp().getUserAgent(), request.getHeader("User-Agent"));

		// referrer
		putMDC(traceKeys.getHttp().getReferrer(), request.getHeader("Referer"));

		//  这里不放入cookies 的信息 cookies
		Cookie[] cookies = request.getCookies();
		List<String> names = emptyList();

		if (cookies != null) {
			names = new ArrayList<>();

			for (Cookie cookie : cookies) {
				names.add(cookie.getName());
				putMDC(traceKeys.getHttp().getCookie_() + cookie.getName(), cookie.getValue());
			}

			sort(names);
		}

		putMDC(traceKeys.getHttp().getCookies(), names.toString());
	}

	private void putMDC(String key, String value) {
		MDC.put(key, value);
	}

	/**
	 * 获取真实的ip地址
	 */
	private String getRemoteAddress(HttpServletRequest request) {
		String remoteAddress = (String) request.getAttribute(REQUEST_REMOTE_ADDRESS_ATTR);
		if (remoteAddress == null) {
			// 初始化remoteAddress
			remoteAddress = request.getHeader("X-Forwarded-For");
			if (remoteAddress == null || remoteAddress.length() == 0) {
				remoteAddress = request.getHeader("X-Real-IP");
			}
			if (remoteAddress == null || remoteAddress.length() == 0) {
				remoteAddress = request.getRemoteAddr();
			}
			// 如果是多级代理，那么取第一个ip为客户ip
			if (remoteAddress != null && remoteAddress.indexOf(",") != -1) {
				remoteAddress = remoteAddress.substring(remoteAddress.lastIndexOf(",") + 1, remoteAddress.length()).trim();
			}
			request.setAttribute(REQUEST_REMOTE_ADDRESS_ATTR, remoteAddress);
		}
		return remoteAddress;
	}
}