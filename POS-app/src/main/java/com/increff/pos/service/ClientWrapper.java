package com.increff.pos.service;

import com.increff.pos.clients.PDFClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ClientWrapper {
    @Autowired
    private PDFClient pdfClient;
}
