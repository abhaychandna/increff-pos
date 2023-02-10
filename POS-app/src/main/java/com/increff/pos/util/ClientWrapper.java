package com.increff.pos.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ClientWrapper {
    @Autowired
    private PDFClient pdfClient;
}
