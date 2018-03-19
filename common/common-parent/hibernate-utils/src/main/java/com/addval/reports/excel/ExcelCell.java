//Source file: D:\\Projects\\cargores\\source\\com\\addval\\cargores\\reports\\excel\\ExcelCell.java

package com.addval.reports.excel;

import com.addval.parser.InvalidInputException;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class ExcelCell
{
    private static final String _module = "ExcelCell";
    private static String _alphas = " abcdefghijklmnopqrstuvwxyz";
    private static int _BASE26 = 26;
    private String _name = null;
    private String _type = null;
    private String _value = null;
    private String _style = null;
    private int _row = - 1;
    private int _col = 0;

    /**
     * Object representing a single cell of an Excel sheet.
     * The style info is derived usually from a style sheet.
     * @param name
     * @param value
     * @param type
     * @param style
     * @roseuid 3CDF640C005F
     */
    public ExcelCell(String name, String value, String type, String style) throws InvalidInputException {
        if (name == null)
            throw new InvalidInputException("A Cell address should not be null!");
        _name   = name.toLowerCase();
        _value  = value;
        _type   = type;
        _style  = style;
        setRowCol();
    }

    /**
     * user friendly function to convert the cell address from human format to numeral
     * format.
     * eg. a3 input will be converted to column 0 and row 2
     * @roseuid 3CDF640C00B9
     */
    private void setRowCol() throws InvalidInputException {
        String firstChar = "";
        String secondChar = "";
        String rowCount = "";
        int firstNum = 0;
        int secondNum = 0;
        int cellNumber = 0;
        RE regexp = null;
        String expression ="^([a-z]?)([a-z])(\\d{1,5})";
        try {
            regexp = new RE( expression, RE.MATCH_CASEINDEPENDENT );
        } catch (RESyntaxException e) {
            throw new InvalidInputException("Regular Expression syntax error");
        }
        boolean result = regexp.match( _name );
        firstChar = regexp.getParen( 1 );
        secondChar = regexp.getParen( 2 );
        rowCount = regexp.getParen( 3 );
        if ( firstChar != null ) {
            firstNum = _alphas.indexOf(firstChar);
            firstNum *= _BASE26;
        }
        if ( secondChar != null )
            secondNum = _alphas.indexOf(secondChar);

        cellNumber = firstNum + secondNum;

        _col = cellNumber-1;
        _row = Integer.parseInt(rowCount)-1;
    }

    /**
     * @param value
     * @roseuid 3CDF640C011D
     */
    private void setValue(String value)
    {
        _value = value;
    }

    /**
     * @param c
     * @return boolean
     * @roseuid 3CDF640C0128
     */
    private boolean isNumber(char c)
    {
        if (c > 47 && c < 58)
            return true;

        return false;
    }

    /**
     * @return String
     * @roseuid 3CDF640C016D
     */
    public String getAddress()
    {
        return _name;
    }

    /**
     * @return String
     * @roseuid 3CDF640C01B3
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * @return String
     * @roseuid 3CDF640C01D1
     */
    public String getStyle()
    {
        return _style;
    }

    /**
     * @return String
     * @roseuid 3CDF640C01EF
     */
    public String getType()
    {
        return _type;
    }

    /**
     * @return int
     * @roseuid 3CDF640C01F9
     */
    public int getRow()
    {
        return _row;
    }

    /**
     * @return int
     * @roseuid 3CDF640C0203
     */
    public int getCol()
    {
        return _col;
    }

    /**
     * user friendly function to list the contents of the cell object.
     * useful in debugging
     * @return String
     * @roseuid 3CDF640C0217
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Name\t-\t"   ).append( _name  ).append( "\n" );
        buffer.append("Value\t-\t"  ).append( _value ).append( "\n" );
        buffer.append("Type\t-\t"   ).append( _type  ).append( "\n" );
        buffer.append("Row\t-\t"    ).append( _row   ).append( "\n" );
        buffer.append("Col\t-\t"    ).append( _col   ).append( "\n" );
        buffer.append("Style\t-\t"  ).append( _style );
        return buffer.toString();
    }
}
