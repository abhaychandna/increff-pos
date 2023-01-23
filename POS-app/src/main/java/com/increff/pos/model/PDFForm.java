package com.increff.pos.model;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFForm<T> {
    //List<T> reportForm, String xsltFilename, String outputFilename, HashMap<String, String> headers    private List<T> reportForm;
    private String xsltFilename;
    private String outputFilename;
    private HashMap<String, String> headers;
    private List<T> reportData;

}
