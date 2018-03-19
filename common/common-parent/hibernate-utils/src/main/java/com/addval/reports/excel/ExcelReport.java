//Source file: D:\\Projects\\cargores\\source\\com\\addval\\cargores\\reports\\excel\\ExcelReport.java

package com.addval.reports.excel;

import java.util.Vector;

/**
 * interface to be implemented for objects which are to be input to
 * ExcelReportWriter for producing Excel file
 */
public interface ExcelReport
{

    /**
     * @return java.util.Vector
     * @roseuid 3CDF640C03D0
     */
    public Vector getExcelCells();
}
