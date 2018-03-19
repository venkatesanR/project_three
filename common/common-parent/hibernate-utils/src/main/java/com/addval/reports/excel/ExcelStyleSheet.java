//Source file: D:\\Projects\\cargores\\source\\com\\addval\\cargores\\reports\\excel\\ExcelStyleSheet.java

package com.addval.reports.excel;

import jxl.write.WritableFont;
import jxl.format.Colour;
import jxl.format.BorderLineStyle;
import jxl.format.Alignment;

import jxl.write.NumberFormats;
import com.addval.environment.Environment;
import com.addval.utils.CnfgFileMgr;
import com.addval.parser.InvalidInputException;
import jxl.biff.DisplayFormat;
import java.util.Enumeration;
import jxl.format.Border;
import java.util.Hashtable;
import jxl.write.WritableCellFormat;

/**
 * class for preparing the stylesheet for Excel creation. Has its own property
 * file for style sheet definition. Styles could be created similar to css pattern
 * using the sample property file. the Styles are prepared once during
 * initialisation and used whenver necessary.
 */
public class ExcelStyleSheet
{
//    private static final String _module = "ExcelStyleSheet";
    private String _styleSheet = null;
    private Hashtable _colour = null;
    private Hashtable _font = null;
    private Hashtable _bold = null;
    private Hashtable _underline = null;
    private Hashtable _borderlinestyle = null;
    private Hashtable _align = null;
    private Hashtable _format = null;
    private Hashtable _styles = null;

    /**
     * @param styleSheet
     * @roseuid 3CDF56D60228
     */
    public ExcelStyleSheet(String styleSheet) throws InvalidInputException {
        _styleSheet = styleSheet;
        buildColors();
        buildFonts();
        buildBorderlineStyle();
        buildAlign();
        buildFormat();
        buildStyles();
    }

    /**
     * @roseuid 3CDF56D603CD
     */
    private void buildColors()
    {
        _colour = new Hashtable();
        _colour.put( "BLACK", Colour.BLACK );
        _colour.put( "WHITE", Colour.WHITE );
        _colour.put( "GRAY_80", Colour.GRAY_80 );
        _colour.put( "GRAY_50", Colour.GRAY_50 );
        _colour.put( "GRAY_25", Colour.GRAY_25 );
        _colour.put( "RED", Colour.RED );
        _colour.put( "DARK_RED", Colour.DARK_RED );
        _colour.put( "ORANGE", Colour.ORANGE );
        _colour.put( "LIGHT_ORANGE", Colour.LIGHT_ORANGE );
        _colour.put( "BLUE", Colour.BLUE );
        _colour.put( "LIGHT_BLUE", Colour.LIGHT_BLUE );
        _colour.put( "PALE_BLUE", Colour.PALE_BLUE );
        _colour.put( "DARK_BLUE", Colour.DARK_BLUE );
        _colour.put( "SKY_BLUE", Colour.SKY_BLUE );
        _colour.put( "YELLOW", Colour.YELLOW );
        _colour.put( "GOLD", Colour.GOLD );
        _colour.put( "GREEN", Colour.GREEN );
        _colour.put( "BRIGHT_GREEN", Colour.BRIGHT_GREEN );
        _colour.put( "LIGHT_GREEN", Colour.LIGHT_GREEN );
        _colour.put( "LIME", Colour.LIME );
        _colour.put( "BROWN", Colour.BROWN );
        _colour.put( "VIOLET", Colour.VIOLET );
        _colour.put( "PINK", Colour.PINK );
        _colour.put( "ROSE", Colour.ROSE );
        _colour.put( "DEFAULT_BACKGROUND", Colour.DEFAULT_BACKGROUND );
    }

    /**
     * @roseuid 3CDF56D70099
     */
    private void buildFonts()
    {
        _font = new Hashtable();
        _font.put( "ARIAL", new WritableFont( WritableFont.ARIAL ) );
        _font.put( "TIMES", WritableFont.TIMES );

        _bold = new Hashtable();
        _bold.put( "NO_BOLD", "0x190" );
        _bold.put( "BOLD", "0x2bc" );

        _underline = new Hashtable();
        _underline.put( "NO_UNDERLINE", "0"        );
        _underline.put( "SINGLE", "1"              );
        _underline.put( "DOUBLE", "2"              );
        _underline.put( "SINGLE_ACCOUNTING", "0x21" );
        _underline.put( "DOUBLE_ACCOUNTING", "0x22" );
    }

    /**
     * @roseuid 3CDF56D700DF
     */
    private void buildBorderlineStyle()
    {
        _borderlinestyle = new Hashtable();
        _borderlinestyle.put( "NONE", BorderLineStyle.NONE                               );
        _borderlinestyle.put( "THIN", BorderLineStyle.THIN                               );
        _borderlinestyle.put( "MEDIUM", BorderLineStyle.MEDIUM                           );
        _borderlinestyle.put( "DASHED", BorderLineStyle.DASHED                           );
        _borderlinestyle.put( "DOTTED", BorderLineStyle.DOTTED                           );
        _borderlinestyle.put( "THICK", BorderLineStyle.THICK                             );
        _borderlinestyle.put( "DOUBLE", BorderLineStyle.DOUBLE                           );
        _borderlinestyle.put( "HAIR", BorderLineStyle.HAIR                               );
        _borderlinestyle.put( "MEDIUM_DASHED", BorderLineStyle.MEDIUM_DASHED             );
        _borderlinestyle.put( "DASH_DOT", BorderLineStyle.DASH_DOT                       );
        _borderlinestyle.put( "MEDIUM_DASH_DOT", BorderLineStyle.MEDIUM_DASH_DOT         );
        _borderlinestyle.put( "DASH_DOT_DOT", BorderLineStyle.DASH_DOT_DOT               );
        _borderlinestyle.put( "MEDIUM_DASH_DOT_DOT", BorderLineStyle.MEDIUM_DASH_DOT_DOT );
        _borderlinestyle.put( "SLANTED_DASH_DOT", BorderLineStyle.SLANTED_DASH_DOT       );
    }

    /**
     * @roseuid 3CDF56D700FD
     */
    private void buildAlign()
    {
        _align = new Hashtable();
        _align.put( "GENERAL", Alignment.GENERAL  );
        _align.put( "LEFT", Alignment.LEFT        );
        _align.put( "CENTRE", Alignment.CENTRE    );
        _align.put( "RIGHT", Alignment.RIGHT      );
        _align.put( "FILL", Alignment.FILL        );
        _align.put( "JUSTIFY", Alignment.JUSTIFY  );
    }

    /**
     * @roseuid 3CDF56D70189
     */
    private void buildFormat()
    {
        _format = new Hashtable();
        _format.put( "DEFAULT",NumberFormats.DEFAULT );
        _format.put( "INTEGER",NumberFormats.INTEGER );
        _format.put( "FLOAT",NumberFormats.FLOAT );
        _format.put( "THOUSANDS_INTEGER",NumberFormats.THOUSANDS_INTEGER );
        _format.put( "THOUSANDS_FLOAT",NumberFormats.THOUSANDS_FLOAT );
        _format.put( "ACCOUNTING_INTEGER",NumberFormats.ACCOUNTING_INTEGER );
        _format.put( "ACCOUNTING_RED_INTEGER",NumberFormats.ACCOUNTING_RED_INTEGER );
        _format.put( "ACCOUNTING_FLOAT",NumberFormats.ACCOUNTING_FLOAT );
        _format.put( "ACCOUNTING_RED_FLOAT",NumberFormats.ACCOUNTING_RED_FLOAT );
        _format.put( "PERCENT_INTEGER",NumberFormats.PERCENT_INTEGER );
        _format.put( "PERCENT_FLOAT",NumberFormats.PERCENT_FLOAT );
        _format.put( "EXPONENTIAL",NumberFormats.EXPONENTIAL );
        _format.put( "FORMAT1",NumberFormats.FORMAT1 );
        _format.put( "FORMAT2",NumberFormats.FORMAT2 );
        _format.put( "FORMAT3",NumberFormats.FORMAT3 );
        _format.put( "FORMAT4",NumberFormats.FORMAT4 );
        _format.put( "FORMAT5",NumberFormats.FORMAT5 );
        _format.put( "FORMAT6",NumberFormats.FORMAT6 );
        _format.put( "FORMAT7",NumberFormats.FORMAT7 );
        _format.put( "FORMAT8",NumberFormats.FORMAT8 );
        _format.put( "FORMAT9",NumberFormats.FORMAT10 );
    }

    /**
     * @roseuid 3CDF56D701BB
     */
    private void buildStyles() throws InvalidInputException
    {
        _styles = new Hashtable();
        try {
            CnfgFileMgr cnfgFileMgr = Environment.getInstance( _styleSheet ).getCnfgFileMgr();
            for (Enumeration enumeration = cnfgFileMgr.getPropertyNames(); enumeration.hasMoreElements();) {
                String style = (String)enumeration.nextElement();
                // styles are specified as <stylename>.<property>.
                // so check for \".\" in the property list
                if (style.indexOf(".") < 1)
                    continue;
                style = style.substring( 0, style.indexOf( "." ) );
                // if the particular style is prepared already continue to build the next style
                if (_styles.containsKey( style ))
                    continue;
                _styles.put( style, buildStyle( style, cnfgFileMgr ) );
            }
        }
        catch(Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    /**
     * @param style
     * @param cnfgFileMgr
     * @return jxl.write.WritableCellFormat
     * @throws java.lang.Exception
     * @roseuid 3CDF56D7020C
     */
    private WritableCellFormat buildStyle(String style, CnfgFileMgr cnfgFileMgr) throws Exception
    {
        String fontName         =   cnfgFileMgr.getPropertyValue( ( style + ".font" ), "" );
        String size             =   cnfgFileMgr.getPropertyValue( ( style + ".size" ), "10" );
        String color            =   cnfgFileMgr.getPropertyValue( ( style + ".color" ), "BLACK" );
        String bgcolor          =   cnfgFileMgr.getPropertyValue( ( style + ".bgcolor" ), "WHITE" );
        String underline        =   cnfgFileMgr.getPropertyValue( ( style + ".underline" ), "" );
        String border           =   cnfgFileMgr.getPropertyValue( ( style + ".border" ), "" );
        String borderLineStyle  =   cnfgFileMgr.getPropertyValue( ( style + ".borderLineStyle" ), "" );
        String align            =   cnfgFileMgr.getPropertyValue( ( style + ".align" ), "GENERAL" );
        String format           =   cnfgFileMgr.getPropertyValue( ( style + ".format" ), "DEFAULT" );
        boolean bold            =   cnfgFileMgr.getBoolValue( ( style + ".bold" ), false );
        boolean wrap            =   cnfgFileMgr.getBoolValue( ( style + ".wrap" ), false );

        WritableFont font;
        if (bold)
            font = new WritableFont( WritableFont.ARIAL, Integer.parseInt( size ) , WritableFont.BOLD , false , jxl.format.UnderlineStyle.NO_UNDERLINE, (Colour) _colour.get( color ) );
        else
            font = new WritableFont( WritableFont.ARIAL, Integer.parseInt( size ) , WritableFont.NO_BOLD , false , jxl.format.UnderlineStyle.NO_UNDERLINE, (Colour) _colour.get( color ) );

        WritableCellFormat cellFormat = new WritableCellFormat( font , (DisplayFormat) _format.get( format ) );
        cellFormat.setAlignment( (Alignment) _align.get( align ) );
        cellFormat.setBackground( (Colour) _colour.get( bgcolor ) );
        cellFormat.setBorder( Border.NONE, (BorderLineStyle) _borderlinestyle.get( borderLineStyle ) );
        cellFormat.setWrap( wrap );
        return cellFormat;
    }

    /**
     * @param style
     * @return jxl.write.WritableCellFormat
     * @throws java.lang.Exception
     * @roseuid 3CDF56DE0125
     */
    public WritableCellFormat getStyle(String style) throws Exception
    {
        if (_styles.containsKey( style ))
            return (WritableCellFormat)_styles.get( style );

        return null;
    }
}
