package com.increff.pos.util;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.service.ApiException;

public class ErrorUtil {
    public static <T> void throwErrors(List<T> errors) throws ApiException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
            throw new ApiException(json);
        }
        catch (JsonProcessingException e) {
            throw new ApiException("Error in parsing error data to json");
        }
    }
}
