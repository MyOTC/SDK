package com.bdd.domain;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

@SuppressWarnings("serial")
public class JsonResult<T> implements Serializable {

	private int code;
	private String msg;
	private T body;
	
	public JsonResult() {
	}
	
	public JsonResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public JsonResult(int code, String msg, T body) {
		this.code = code;
		this.msg = msg;
		this.body = body;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
