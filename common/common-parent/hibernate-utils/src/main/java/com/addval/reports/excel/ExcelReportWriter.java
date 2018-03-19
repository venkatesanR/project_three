//Source file: D:\\Projects\\cargores\\source\\com\\addval\\cargores\\reports\\excel\\ExcelReportWriter.java

package com.addval.reports.excel;

import com.addval.environment.Environment;
import jxl.write.WritableWorkbook;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableCell;
import java.util.Iterator;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;

import java.io.File;
import com.addval.parser.InvalidInputException;


/**
 * Generalised Excel Writer. Can create a new Excel file and may populate data or
 * copy a predefined Excel file and populate the copy with data. Original format
 * remains.
 */
public class ExcelReportWriter
{
    private String _projectName = null;
    private String _styleSheet = null;
    private static final String _module = "ExcelReportWriter";
    private File _template = null;
    private File _outFile = null;
    private ExcelStyleSheet _excelStyleSheet = null;

    /**
     * constructor to be used when specific style sheet is to be used for the style
     * info. If passed as null, the default style sheet will be used for style info.
     * @param projectName
     * @param excelReport
     * @param excelReportTemplate
     * @param outFile
     * @param styleSheet
     * @throws java.lang.Exception
     * @roseuid 3CDF641703CC
     */
    public ExcelReportWriter(String projectName, ExcelReport excelReport, String excelReportTemplate, String outFile, String styleSheet) throws Exception
    {
        _projectName = projectName;
        if (outFile == null)
            throw new InvalidInputException("Output file should not be null!" );
        _outFile = new File( outFile );
        if (excelReportTemplate != null) {
            String tempFile = Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( excelReportTemplate.trim() + ".excelFile", "");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            tempFile =   loader.getResource(tempFile).getPath();
            _template = new File( tempFile );
            if (!_template.exists())
                throw new InvalidInputException("Specified Template file is not available!\t" + _template );
        }
        _styleSheet = styleSheet == null ? "excelstylesheet" : styleSheet;
        _excelStyleSheet = new ExcelStyleSheet( _styleSheet );
        createReport( excelReport.getExcelCells() );
    }

    /**
     * constructor to be used for preparing the Excel file with default Stylesheet
     * info. If excelReportTemplate is passed as null, a new file will be created to
     * populate data. In case excelReportTemplate is specified then the out file will
     * be created based on the contents of the template and then populated with thye
     * data.
     * @param projectName
     * @param excelReport
     * @param excelReportTemplate
     * @param outFile
     * @throws java.lang.Exception
     * @roseuid 3CDF6415013E
     */
    public ExcelReportWriter(String projectName, ExcelReport excelReport, String excelReportTemplate, String outFile) throws Exception
    {
        this( projectName, excelReport, excelReportTemplate, outFile, null);
    }

    /**
     * @return java.io.File
     * @roseuid 3CDF6418030F
     */
    public File getExcelFile()
    {
        return _outFile;
    }

    /**
     * @param excelCells
     * @roseuid 3CDF6419003F
     */
    private void createReport(java.util.Vector excelCells) throws InvalidInputException {
        if (_template == null) {
            createNewExcel( excelCells );
        }
        else {
            createExcelFromTemplate( excelCells );
        }
    }

    /**
     * @param excelCells
     * @roseuid 3CDF64190180
     */
    private void createNewExcel(java.util.Vector excelCells) throws InvalidInputException
    {
        try {
            WritableWorkbook output = Workbook.createWorkbook( _outFile );
            WritableSheet sheet     = output.createSheet( "cargores", 0 );
            String style = null;
            String type = null;
            WritableCell cell = null;
            for (Iterator iterator  = excelCells.iterator(); iterator.hasNext();) {
                ExcelCell excelCell = (ExcelCell)iterator.next();
                type = excelCell.getType();
                if (type.equals( "Label"))
                    cell = new Label( excelCell.getCol(), excelCell.getRow(), excelCell.getValue());
                else if (type.equals( "Number"))
                    cell = new Number( excelCell.getCol(), excelCell.getRow(), Double.parseDouble( excelCell.getValue()));
                style = excelCell.getStyle();
                if (style != null)
                    cell.setCellFormat(_excelStyleSheet.getStyle( excelCell.getStyle() ));
                sheet.addCell( cell );
            }
            output.write();
            output.close();
        }
        catch( Exception exception ) {
            throw new InvalidInputException("Problem while creating Excel file " + exception.toString() );
        }
    }

    /**
     * @param excelCells
     * @roseuid 3CDF64190306
     */
    private void createExcelFromTemplate(java.util.Vector excelCells) throws InvalidInputException
    {
        try {
            Workbook input          = Workbook.getWorkbook( _template );
            WritableWorkbook output = Workbook.createWorkbook( _outFile, input );
            //output.copySheet(0, "contract", 0);
            WritableSheet sheet = output.getSheet( 0 );
            String style = null;
            WritableCell cell = null;
            for (Iterator iterator = excelCells.iterator(); iterator.hasNext();) {
                ExcelCell excelCell = (ExcelCell)iterator.next();
                cell = sheet.getWritableCell( excelCell.getCol(), excelCell.getRow() );
                String cellType = cell.getType().toString();
                String excelCellType = excelCell.getType();
                if (excelCellType.equals( cellType )) {
                    if (excelCellType.equals( "Label" ) ) {
                        ((Label) cell).setString( excelCell.getValue() );
                    }
                    else if (excelCellType.equals( "Number" ))
                        ((Number) cell).setValue( Double.parseDouble( excelCell.getValue() ) );
                }
                else {
                    // in case the template is not correctly formatted
                    // prepare a cell and add to the sheet
                    CellFormat cf = new WritableCellFormat();
                    WritableCell wc = sheet.getWritableCell(excelCell.getCol(), excelCell.getRow());
                    WritableCell label = new Label(excelCell.getCol(), excelCell.getRow(),excelCell.getValue());
                    if(wc.getCellFormat()!=null) {
                        cf = new WritableCellFormat(wc.getCellFormat());
                        label.setCellFormat(cf);
                    }
                    sheet.addCell(label);
                }
                style = excelCell.getStyle();
                if (style != null)
                    cell.setCellFormat(_excelStyleSheet.getStyle( excelCell.getStyle() ));

            }
            output.write();
            output.close();
        }
        catch(Exception exception) {
            exception.printStackTrace();
            throw new InvalidInputException( "Problem while creating Excel file from Template " + exception.toString() );
        }
    }
}
