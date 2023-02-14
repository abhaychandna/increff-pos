package com.increff.pdf.model.form;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFForm<T> {
    private String xsltFilename;
    private HashMap<String, String> headers;
    private List<T> reportData;
    public PDFForm(String xsltFilename, HashMap<String, String> headers, List<T> reportData) {
        this.xsltFilename = xsltFilename;
        this.headers = headers;
        this.reportData = reportData;
    }
    public PDFForm() {
    }

}