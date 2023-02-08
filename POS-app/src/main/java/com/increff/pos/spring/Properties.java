package com.increff.pos.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class Properties {

    @Value("${app.version}")
    private String appVersion;
    @Value("${app.name}")
    private String appName;
    @Value("${app.baseUrl}")
    private String appBaseUrl;
    @Value("${supervisorEmail}")
    private String supervisorEmail;
    @Value("${resourcePath}")
    private String resourcePath;
    @Value("${pdfApp.baseUrl}")
    private String pdfAppBaseUrl;
    @Value("${pdfApp.generateReportUrl}")
    private String pdfAppGenerateReportUrl;
    @Value("${daySalesScheduler.delay.seconds}")
    private String daySalesSchedulerDelaySeconds;

}
