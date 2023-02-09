package com.increff.pos.service;

public class ApiException extends Exception {

	private static final Long serialVersionUID = 1L;
	
	public ApiException(String string) {
		super(string);
	}

}
