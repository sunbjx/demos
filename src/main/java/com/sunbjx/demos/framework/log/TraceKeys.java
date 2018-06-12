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

package com.sunbjx.demos.framework.log;


/**
 * Well-known {@link org.springframework.cloud.sleuth.Span#tag(String, String) span tag}
 * keys.
 *
 * <h3>Overhead of adding Trace Data</h3>
 *
 * Overhead is directly related to the size of trace data exported out of process.
 * Accordingly, it is better to tag what's important for latency troubleshooting, i.e. a
 * whitelist vs. collecting everything and filtering downstream. The keys listed here are
 * very common in tracing tools, and are considerate to the issue of overhead.
 *
 * <p>
 * When evaluating new keys, consider how much additional data it implies, and if that
 * data is critical to classifying, filtering or displaying traces. More data often means
 * larger systems, less retention, or a lower sample rate.
 *
 * <p>
 * For example, in zipkin, a thrift-encoded span with an "sr" annotation is 82 bytes plus
 * the size of its name and associated service. The maximum size of an HTTP cookie is 4096
 * bytes, roughly 50x that. Even if compression helps, if you aren't analyzing based on
 * cookies, storing them displaces resources that could be used for more traces.
 * Meanwhile, you have another system storing private data! The takeaway isn't never store
 * cookies, as there are valid cases for this. The takeaway is to be conscious about
 * what's you are storing.
 *
 * @since 1.0.0
 */
public class TraceKeys {

	private final static TraceKeys instance = new TraceKeys();

	private Http http = new Http();


	private Async async = new Async();

	private Mvc mvc = new Mvc();

	public Http getHttp() {
		return this.http;
	}


	public Async getAsync() {
		return this.async;
	}

	public Mvc getMvc() {
		return this.mvc;
	}

	public void setHttp(Http http) {
		this.http = http;
	}

	public void setAsync(Async async) {
		this.async = async;
	}

	public void setMvc(Mvc mvc) {
		this.mvc = mvc;
	}


	public static class Http {

		private String requestURL                = "http_requestURL";
		private String requestURLWithQueryString = "http_requestURLWithQueryString";
		private String requestURI                = "http_requestURI";
		private String requestURIWithQueryString = "http_requestURIWithQueryString";
		private String queryString               = "http_queryString";
		private String remoteAddr                = "http_remoteAddr";
		private String remoteHost                = "http_remoteHost";
		private String userAgent                 = "http_userAgent";
		private String referrer                  = "http_referrer";
		public  String cookies                   = "http_cookies";
		private String cookie_                   = "http_cookie_";
		private String statusCode                = "http_status_code";
		private String method                    = "http_method";


		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getRequestURL() {
			return requestURL;
		}

		public void setRequestURL(String requestURL) {
			this.requestURL = requestURL;
		}

		public String getRequestURLWithQueryString() {
			return requestURLWithQueryString;
		}

		public void setRequestURLWithQueryString(String requestURLWithQueryString) {
			this.requestURLWithQueryString = requestURLWithQueryString;
		}

		public String getRequestURI() {
			return requestURI;
		}

		public void setRequestURI(String requestURI) {
			this.requestURI = requestURI;
		}

		public String getRequestURIWithQueryString() {
			return requestURIWithQueryString;
		}

		public void setRequestURIWithQueryString(String requestURIWithQueryString) {
			this.requestURIWithQueryString = requestURIWithQueryString;
		}

		public String getQueryString() {
			return queryString;
		}

		public void setQueryString(String queryString) {
			this.queryString = queryString;
		}

		public String getRemoteAddr() {
			return remoteAddr;
		}

		public void setRemoteAddr(String remoteAddr) {
			this.remoteAddr = remoteAddr;
		}

		public String getRemoteHost() {
			return remoteHost;
		}

		public void setRemoteHost(String remoteHost) {
			this.remoteHost = remoteHost;
		}

		public String getUserAgent() {
			return userAgent;
		}

		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		public String getReferrer() {
			return referrer;
		}

		public void setReferrer(String referrer) {
			this.referrer = referrer;
		}

		public String getCookies() {
			return cookies;
		}

		public void setCookies(String cookies) {
			this.cookies = cookies;
		}

		public String getCookie_() {
			return cookie_;
		}

		public void setCookie_(String cookie_) {
			this.cookie_ = cookie_;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}
	}


	/**
	 * Trace keys related to async processing
	 */
	public static class Async {

		/**
		 * Prefix for header names if they are added as tags.
		 */
		private String prefix = "";

		/**
		 * Name of the thread that executed the async method
		 *
		 * @see org.springframework.scheduling.annotation.Async
		 */
		private String threadNameKey = "thread";

		/**
		 * Simple name of the class with a method annotated with {@code @Async}
		 * from which the asynchronous process started
		 *
		 * @see org.springframework.scheduling.annotation.Async
		 */
		private String classNameKey = "class";

		/**
		 * Name of the method annotated with {@code @Async}
		 *
		 * @see org.springframework.scheduling.annotation.Async
		 */
		private String methodNameKey = "method";

		public String getPrefix() {
			return this.prefix;
		}

		public String getThreadNameKey() {
			return this.threadNameKey;
		}

		public String getClassNameKey() {
			return this.classNameKey;
		}

		public String getMethodNameKey() {
			return this.methodNameKey;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public void setThreadNameKey(String threadNameKey) {
			this.threadNameKey = threadNameKey;
		}

		public void setClassNameKey(String classNameKey) {
			this.classNameKey = classNameKey;
		}

		public void setMethodNameKey(String methodNameKey) {
			this.methodNameKey = methodNameKey;
		}
	}

	/**
	 * Trace keys related to MVC controller tags
	 */
	public static class Mvc {

		/**
		 * The lower case, hyphen delimited name of the class that processes the request.
		 * Ex. class named "BookController" will result in "book-controller" tag value.
		 */
		private String controllerClass = "mvc.controller.class";

		/**
		 * The lower case, hyphen delimited name of the class that processes the request.
		 * Ex. method named "listOfBooks" will result in "list-of-books" tag value.
		 */
		private String controllerMethod = "mvc.controller.method";

		public String getControllerClass() {
			return this.controllerClass;
		}

		public void setControllerClass(String controllerClass) {
			this.controllerClass = controllerClass;
		}

		public String getControllerMethod() {
			return this.controllerMethod;
		}

		public void setControllerMethod(String controllerMethod) {
			this.controllerMethod = controllerMethod;
		}
	}


	public static TraceKeys getInstance() {
		return instance;
	}
}
