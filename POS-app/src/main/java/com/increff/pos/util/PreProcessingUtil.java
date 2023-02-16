package com.increff.pos.util;

import com.increff.pos.model.data.ErrorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static com.increff.pos.util.ErrorUtil.throwErrors;

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
	
	public static <T> void normalizeAndValidate(List<T> forms) throws ApiException {
		List<ErrorData<T>> errorList = new ArrayList<ErrorData<T>>();
		for(T form : forms) {
			try{
				normalizeAndValidate(form);
			}
			catch (ApiException e) {
				errorList.add(new ErrorData<T>(form, e.getMessage()));
			}
		}
		if(errorList.size()>0) {
			throwErrors(errorList);
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
		for(java.lang.reflect.Field field : form.getClass().getDeclaredFields()) {
			try {					
				field.setAccessible(true);
				if(field.getType().equals(String.class) && Objects.nonNull(field.get(form))) {
					field.set(form, StringUtil.toLowerCase(field.get(form).toString()).trim());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ApiException(e.getMessage());
			}
		}
	}
}
