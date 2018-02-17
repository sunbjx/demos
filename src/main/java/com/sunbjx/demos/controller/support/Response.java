package com.sunbjx.demos.controller.support;

/**
 * 
 * @Description
 * @author sunbx
 * @time 2017年11月22日 上午8:58:35
 */
public class Response {

	private int code;

	private String message;

	private Object results;

	public Response() {
		super();
	}

	public Response(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public Response setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Response setMessage(String message) {
		this.message = message;
		return this;
	}

	public Object getResults() {
		return results;
	}

	public Response setResults(Object results) {
		this.results = results;
		return this;
	}
}
