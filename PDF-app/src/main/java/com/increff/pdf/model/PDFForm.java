package com.increff.pdf.model;

import java.util.HashMap;
import java.util.List;

import com.increff.pdf.model.data.XSLTFilename;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFForm<T> {
    private XSLTFilename xsltFilename;
    private HashMap<String, String> headers;
    private List<T> reportData;
    public PDFForm(XSLTFilename xsltFilename, HashMap<String, String> headers, List<T> reportData) {
        this.xsltFilename = xsltFilename;
        this.headers = headers;
        this.reportData = reportData;
    }
    public PDFForm() {
    }

}