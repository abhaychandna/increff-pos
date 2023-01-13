package com.increff.pos.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertUtil {

	public static <T, R> R convert(T fromClass, Class<R> toClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		R newObject = mapper.convertValue(fromClass, toClass);
		return newObject;
	}



}
