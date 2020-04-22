package com.techmania.spboot.excelconverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class XlshToXlsConverter {
    private String inputFile;
    private String outputFile;

    void loadFromWorkBook() {
        // new Workbook()
    }

    void createXLSHWorkBook() {
    }

    void copySheet() {
    }

    void convert() {
        loadFromWorkBook();
        createXLSHWorkBook();

    }
}
