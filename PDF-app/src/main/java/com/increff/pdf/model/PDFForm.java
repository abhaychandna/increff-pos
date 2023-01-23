package com.increff.pdf.model;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFForm<T> {
    private String xsltFilename;
    private String outputFilename;
    private HashMap<String, String> headers;
    private List<T> reportData;
    public PDFForm(List<T> reportData, String xsltFilename, String outputFilename, HashMap<String, String> headers) {
        this.reportData = reportData;
        this.xsltFilename = xsltFilename;
        this.outputFilename = outputFilename;
        this.headers = headers;
    }
    public PDFForm() {
    }
}
