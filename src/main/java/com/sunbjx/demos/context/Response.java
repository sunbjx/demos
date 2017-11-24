package com.sunbjx.demos.context;

import java.io.Serializable;

/**
 * 
 * @Description
 * @author sunbx
 * @time 2017年11月22日 上午8:58:35
 * @param <T>
 */
public class Response<T> implements Serializable {

	private static final long serialVersionUID = 6476276344203847131L;
	private int code;

	private String message;

	private T results;

	public Response() {
		super();
	}

	public Response(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResults() {
		return results;
	}

	public void setResults(T results) {
		this.results = results;
	}
}
