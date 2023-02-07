package com.increff.pdf.model;

import java.util.HashMap;
import java.util.List;

import com.increff.pdf.model.data.XSLTFilname;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFForm<T> {
    private XSLTFilname xsltFilename;
    private HashMap<String, String> headers;
    private List<T> reportData;
    public PDFForm(XSLTFilname xsltFilename, HashMap<String, String> headers, List<T> reportData) {
        this.xsltFilename = xsltFilename;
        this.headers = headers;
        this.reportData = reportData;
    }
    public PDFForm() {
    }

}