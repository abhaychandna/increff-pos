package com.increff.pdf.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.increff.pdf.service.ApiException;

public class PreProcessingUtil {
	
	private static String errorMessageSeparator = ".\n";
	private static String errorStartMessage = "";

	public static <T> void validate(T form) throws ApiException {
		System.out.println("Validating form: ");
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
		System.out.println("Violations: " + violations.size());
		if(violations.size()>0){
			String error = errorStartMessage;
			for(ConstraintViolation<T> v : violations) {
				error += v.getMessage() + errorMessageSeparator;
			}
			System.out.println("Error: " + error);
			throw new ApiException(error);
		}
	}
}
