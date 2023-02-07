package com.increff.pos.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientWrapper {
    @Autowired
    public PDFClient pdfClient;
}
