package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorData<T> {

    private T form;
    private String error;
    public ErrorData(T form, String error) {
        this.form = form;
        this.error = error;
    }
}
