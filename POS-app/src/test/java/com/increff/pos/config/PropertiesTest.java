package com.increff.pos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class PropertiesTest {
    @Value("${supervisorEmail}")
    private String supervisorEmail;
}
