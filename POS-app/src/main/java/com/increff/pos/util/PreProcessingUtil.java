package com.increff.pos.util;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.increff.pos.service.ApiException;

public class PreProcessingUtil {

	private static String errorMessageSeparator = ".\n";
	private static String errorStartMessage = "";
	
	public static String getErrorMessageSeparator() {
		return errorMessageSeparator;
	}
	
	public static String getErrorStartMessage() {
		return errorStartMessage;
	}

	public static <T> void normalizeAndValidate(T form) throws ApiException {
		normalize(form);
		validate(form);
	}
	
	public static <T> void normalizeAndValidate(List<T> form) throws ApiException {
		for(T f : form) {
			normalizeAndValidate(f);
		}
	}


	public static <T> void validate(T form) throws ApiException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
		if(violations.size()>0){
			String error = errorStartMessage;
			for(ConstraintViolation<T> v : violations) {
				error += v.getMessage() + errorMessageSeparator;
			}
			throw new ApiException(error);
		}
	}
    
	public static <T> void normalize(T form) throws ApiException{
		for(java.lang.reflect.Field f : form.getClass().getDeclaredFields()) {
			try {					
				f.setAccessible(true);
				if(f.getType().equals(String.class) && Objects.nonNull(f.get(form))) {
					f.set(form, StringUtil.toLowerCase(f.get(form).toString()).trim());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ApiException(e.getMessage());
			}
		}
	}
}
