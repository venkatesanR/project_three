//Source file: D:\\Projects\\cargores\\source\\com\\addval\\cargores\\reports\\excel\\ExcelUtils.java

package com.addval.reports.excel;

import com.addval.parser.InvalidInputException;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * friendly class to create an Excel cell based on the available info.
 * the type of the cell is always derived from the value passed. The style info
 * also is accepted. The style should specified in the style sheet. So the
 * ExcelStyleSheet, excelstylesheet.properties and ExcelReportWrtiter for how
 * styles are used.
 */
public class ExcelUtils
{
    private static String _alphas = " abcdefghijklmnopqrstuvwxyz";
    private static final String _module = "ExcelUtils";

    /**
     * @param value
     * @param cellAddress
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641D01D6
     */
    public static ExcelCell buildCell(float value, String cellAddress) throws InvalidInputException {
        return buildCell( value, cellAddress, null );
    }

    /**
     * @param value
     * @param cellAddress
     * @param style
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641D0302
     */
    public static ExcelCell buildCell(float value, String cellAddress, String style) throws InvalidInputException {
        return buildCell( String.valueOf( value ), cellAddress, style, "Number" );
    }

    /**
     * @param value
     * @param cellAddress
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641E005A
     */
    public static ExcelCell buildCell(int value, String cellAddress) throws InvalidInputException {
        return buildCell( value, cellAddress, null);
    }

    /**
     * @param value
     * @param cellAddress
     * @param style
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641E0169
     */
    public static ExcelCell buildCell(int value, String cellAddress, String style) throws InvalidInputException {
        return buildCell( String.valueOf( value ), cellAddress, style, "Number");
    }

    /**
     * @param value
     * @param cellAddress
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641E0281
     */
    public static ExcelCell buildCell(double value, String cellAddress) throws InvalidInputException {
        return buildCell( value, cellAddress, null );
    }

    /**
     * @param value
     * @param cellAddress
     * @param style
     * @param type
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641F03B9
     */
    public static ExcelCell buildCell(String value, String cellAddress, String style, String type) throws InvalidInputException {
        return new ExcelCell( cellAddress, value, type,  style);
    }

    /**
     * @param value
     * @param cellAddress
     * @param style
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641E03CC
     */
    public static ExcelCell buildCell(double value, String cellAddress, String style) throws InvalidInputException {
        return buildCell( String.valueOf( value ), cellAddress, style, "Number" );
    }

    /**
     * @param value
     * @param cellAddress
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641F011A
     */
    public static ExcelCell buildCell(String value, String cellAddress) throws InvalidInputException {
        if (value == null)
            value = "";
        return buildCell( value, cellAddress, null );
    }

    /**
     * @param value
     * @param cellAddress
     * @param style
     * @return com.openwave.sip.report.ExcelCell
     * @roseuid 3CDF641F0248
     */
    public static ExcelCell buildCell(String value, String cellAddress, String style) throws InvalidInputException {
        if (value == null)
            value = "";

        return buildCell(value, cellAddress, style, "Label");
    }


    /**
     * @param cellAddress
     * @return String
     * @roseuid 3CDF642000F4
     */
    public static String getNextCellLeft(String cellAddress) throws InvalidInputException {
        return getNextCell(cellAddress, -1, 0);
    }

    /**
     * @param cellAddress
     * @return String
     * @roseuid 3CDF6420011C
     */
    public static String getNextCellRight(String cellAddress) throws InvalidInputException {
        return getNextCell(cellAddress, 1, 0);
    }

    /**
     * @param cellAddress
     * @return String
     * @roseuid 3CDF64200162
     */
    public static String getNextCellTop(String cellAddress) throws InvalidInputException {
        return getNextCell(cellAddress, 0, -1);
    }

    /**
     * @param cellAddress
     * @return String
     * @roseuid 3CDF6420016D
     */
    public static String getNextCellBottom(String cellAddress) throws InvalidInputException {
        return getNextCell(cellAddress, 0, 1);
    }

    /**
     * @param cellAddress
     * @param x
     * @param y
     * @return String
     * @roseuid 3CDF6420018A
     */
   private static String getNextCell(String cellAddress, int x, int y) throws InvalidInputException {
        int maxCols = 256;
        int maxRows = 65536;
        int firstNum = 0;
        int secondNum = 0;
        int columnCount = 0;
        String firstChar = "";
        String secondChar = "";
        String rowCount = "";
        String newFirstChar = "";
        String newSecondChar = "";
        RE regexp = null;
        String expression ="^([a-z]?)([a-z])(\\d{1,5})";
        try {
            regexp = new RE( expression, RE.MATCH_CASEINDEPENDENT );
        } catch (RESyntaxException e) {
            throw new InvalidInputException("Regular Expression syntax error");
        }
        boolean result = regexp.match( cellAddress );
        firstChar = regexp.getParen( 1 );
        secondChar = regexp.getParen( 2 );
        rowCount = regexp.getParen( 3 );
        if ( firstChar != null ) {
            firstNum = _alphas.indexOf(firstChar);
            firstNum *= 26;
        }
        if ( secondChar != null )
            secondNum = _alphas.indexOf(secondChar);

        columnCount = firstNum + secondNum;

        columnCount += x;
        if ( columnCount > maxCols )
            throw new InvalidInputException( "Number of columns exceeded 256");

        int newFirstNum = 0;
        int newSecondNum = 0;
        if ( columnCount % 26 == 0 ) {
            newFirstNum = (columnCount-1)/26;
            newSecondNum = 26;
        }
        else
        {
            newFirstNum = columnCount/26;
            newSecondNum = columnCount%26;
        }
        if ( newFirstNum != 0 )
            newFirstChar = _alphas.substring(newFirstNum,newFirstNum+1);
        newSecondChar = _alphas.substring(newSecondNum,newSecondNum+1);
        int newRowCount = Integer.parseInt( rowCount ) + y;
        if ( newRowCount > maxRows )
            throw new InvalidInputException( "Number of rows exceeded 65536");
        return newFirstChar + newSecondChar + String.valueOf( newRowCount );
    }
}
