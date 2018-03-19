package com.addval.jasperutils;

import java.awt.Color;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.addval.environment.Environment;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.GenUtl;
import com.addval.utils.StrUtl;

public class EditorMetaDataReportDesigner {

    private static final String SUB_SYSTEM = "jasper_report";
    private JasperDesign jasperDesign = null;
    private EditorMetaData metadata = null;
    private boolean forLandscape = false;
    private int formatType = -1;
    private HashMap columnPositions = null;
    private String _designFileName = null;

    public static final int PDF_FORMAT = 1;
    public static final int CSV_FORMAT = 2;
    public static final int XLS_FORMAT = 3;
    public static final int HTML_FORMAT = 4;
    private static HashMap jasperDesigns = new HashMap();


    private static final String EDIT 	= "_EDIT";
    private static final String MODIFY = "_MODIFY";
    private static final String ADD 	= "_ADD";
    private static final String CLONE 	= "_CLONE";
    private static final String COPY 	= "_COPY";
    private static final String DELETE = "_DELETE";
    private static final String LOG    = "_LOG";
    private static final String VIEW   = "_VIEW";
    
    public EditorMetaDataReportDesigner(EditorMetaData metadata, boolean forLandscape,int formatType, String designFileName) throws JRException {
        try{
		    this.setDesignFileName(designFileName);
            this.setMetadata( metadata );
            this.setForLandscape( forLandscape );
            this.setFormatType( formatType );
            jasperDesign = createJasperDesign();
            computeColumnPositions();
            addJRDesignFields();
            setReportWidthHeight();
        }
        catch(Exception ex){
            throw new JRException(ex);
        }
	}


    public EditorMetaDataReportDesigner(EditorMetaData metadata, boolean forLandscape,int formatType) throws JRException {
        try{
            this.setMetadata( metadata );
            this.setForLandscape( forLandscape );
            this.setFormatType( formatType );
            jasperDesign = createJasperDesign();
            computeColumnPositions();
            addJRDesignFields();
            setReportWidthHeight();
        }
        catch(Exception ex){
            throw new JRException(ex);
        }
    }

    private JasperDesign createJasperDesign() throws Exception {
        String jrxmlDefaultFileName = null;

        if (StrUtl.isEmpty(getDesignFileName())) {
			jrxmlDefaultFileName = Environment.getInstance( SUB_SYSTEM ).getCnfgFileMgr().getPropertyValue("jasper.reports.editormetadata","");
		} else {
			jrxmlDefaultFileName = getDesignFileName();
		}

        String jrxmlFileName =null;
        switch ( getFormatType() ){
            case PDF_FORMAT :{
                jrxmlFileName = "pdf_" + jrxmlDefaultFileName;
                break;
            }
            case CSV_FORMAT :{
                jrxmlFileName = "csv_" + jrxmlDefaultFileName;
                break;
            }
            case XLS_FORMAT :{
                jrxmlFileName = "xls_" + jrxmlDefaultFileName;
                break;
            }
            case HTML_FORMAT :{
                jrxmlFileName = "html_" + jrxmlDefaultFileName;
                break;
            }
            default :{
                break;
            }
        }

        if( jasperDesigns.containsKey( jrxmlFileName ) ){
            return  (JasperDesign) GenUtl.cloneObject( (JasperDesign )jasperDesigns.get( jrxmlFileName ) );
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //To load the Jasper template from classpath or from jar file.

        JasperDesign originalJasperDesign = null;
        InputStream inputStream = loader.getResourceAsStream( jrxmlFileName );
        if ( inputStream != null ){
            originalJasperDesign = JRXmlLoader.load( inputStream );
        }
        else {
            inputStream = loader.getResourceAsStream( jrxmlDefaultFileName );
            if ( inputStream != null ){
            	originalJasperDesign = JRXmlLoader.load( inputStream );
            }
            else {
                throw new RuntimeException( "Unable to load or locate Jasper template File - " + jrxmlDefaultFileName + " in classpath" );
            }
        }
        jasperDesigns.put( jrxmlFileName , originalJasperDesign);

        return  (JasperDesign) GenUtl.cloneObject( originalJasperDesign );

    }

	public String getDesignFileName() {
		return _designFileName;
	}

	public void setDesignFileName(String designFileName) {
		_designFileName = designFileName;
	}

    public EditorMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(EditorMetaData metadata) {
        this.metadata = metadata;
    }

    public boolean isForLandscape() {
        return forLandscape;
    }

    public void setForLandscape(boolean forLandscape) {
        this.forLandscape = forLandscape;
    }
    public int getFormatType() {
        return formatType;
    }

    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }
    public HashMap getColumnPositions() {
        return columnPositions;
    }

    public JasperReport getJasperReport() throws JRException {
        if(getFormatType() == CSV_FORMAT ){
            jasperDesign.setTitle( getPageHeader() ) ;
        }
        else {
            jasperDesign.setPageHeader( getPageHeader() );
        }
        System.out.println("EditorMetaDataReportDesigner :getJasperReport()" + ((JRDesignSection) jasperDesign.getDetailSection()).getBands().length);
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(getDetail());
        JasperReport report = JasperCompileManager.compileReport( jasperDesign );
        return report;
    }

    private void addJRDesignFields() throws JRException {
        Vector columns = getMetadata().getDisplayableColumns();
        if(columns == null) return;
        JRDesignField field = null;
        ColumnMetaData columnMetaData = null;
        for (int i=0; i<columns.size(); ++i) {
            columnMetaData = (ColumnMetaData) columns.get( i );
            if( !isColumnExportable(columnMetaData) ){
            	continue;
            }
            field = new JRDesignField();
            field.setName( columnMetaData.getName() );
            field.setValueClass(java.lang.String.class);
            jasperDesign.addField(field);
        }
    }

    private JRBand getPageHeader(){
        JRDesignBand band = new JRDesignBand();
        band.setHeight(25);
        Vector columns = getMetadata().getDisplayableColumns();
        ColumnMetaData columnMetaData = null;
        JRDesignStaticText staticText = null;
        ColumnPosition columnPosition = null;
        for (int i=0; i<columns.size(); ++i) {
            columnMetaData = (ColumnMetaData) columns.get( i );
            if( !isColumnExportable(columnMetaData) ){
            	continue;
            }
            columnPosition = (ColumnPosition)getColumnPositions().get( columnMetaData.getName() );

            staticText = new JRDesignStaticText();
            staticText.setX( columnPosition.getX() );
            staticText.setY( columnPosition.getY() );
            staticText.setWidth( columnPosition.getWidth() );
            staticText.setHeight( columnPosition.getHeight() );

            staticText.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
            staticText.setForecolor(Color.white);
            staticText.setBackcolor(new Color(0x33, 0x33, 0x33));
        	staticText.setMode(ModeEnum.OPAQUE);
        	staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
        	staticText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
            //staticText.setFont(((JRReportFont)jasperDesign.getFontsMap().get("Arial_Bold")));
        	staticText.setFontSize(7);
            staticText.setText(columnMetaData.getCaption().replaceAll("<br>",""));
            band.addElement(staticText);
        }
        return band;
    }

    private JRBand getDetail(){
        JRDesignBand band = new JRDesignBand();
        band.setHeight(20);
        Vector columns = getMetadata().getDisplayableColumns();
        ColumnMetaData columnMetaData = null;
        JRDesignTextField textField = null;
        ColumnPosition columnPosition = null;
        for (int i=0; i<columns.size(); ++i) {
            columnMetaData = (ColumnMetaData) columns.get( i );
            if( !isColumnExportable(columnMetaData) ){
            	continue;
            }
            columnPosition = (ColumnPosition)getColumnPositions().get( columnMetaData.getName() );
            textField = new JRDesignTextField();
            textField.setStretchWithOverflow(true);
            textField.setX(columnPosition.getX());
            textField.setY(columnPosition.getY());
            textField.setWidth(columnPosition.getWidth());
            textField.setHeight(columnPosition.getHeight());
            textField.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
            //textField.setFont(((JRReportFont)jasperDesign.getFontsMap().get("Arial_Normal")));
            textField.setFontSize(7);
            JRDesignExpression expression = new JRDesignExpression();
            expression.setValueClass(java.lang.String.class);
            expression.setText("$F{" + columnMetaData.getName() + "}");
            textField.setExpression(expression);
            band.addElement(textField);
        }
        return band;
    }

    public void computeColumnPositions(){
        Vector columns = getMetadata().getDisplayableColumns();
        // calculate the x,y,height and width for all the displayable columns of editor metadata
        int x_pos = 7;
        int font_size = 7; //((JRReportFont)jasperDesign.getFontsMap().get("Arial_Normal")).getSize();
        int total_width = 0;

        columnPositions = new HashMap();
        ColumnMetaData columnMetaData = null;
        ColumnPosition columnPosition = null;

        for (int i=0; i<columns.size(); ++i) {
            columnMetaData = (ColumnMetaData) columns.get(i);
            if( !isColumnExportable(columnMetaData) ){
            	continue;
            }
            String format = columnMetaData.getFormat();
            int len = 0;
            if(format != null){
                len = format.length();
            }
            if(columnMetaData.getCaption() != null && len < columnMetaData.getCaption().trim().length() ){
                len = columnMetaData.getCaption().trim().length();
            }
            len = len * font_size;

            columnPosition= new ColumnPosition();
            columnPosition.setName(columnMetaData.getName());
            columnPosition.setX(x_pos);
            columnPosition.setY(5);
            columnPosition.setWidth(len);
            //increased height to display the page header. Praveen : 19th May, 2006
            columnPosition.setHeight(15);

            columnPositions.put(columnMetaData.getName(),columnPosition);

            x_pos = x_pos + len + font_size;
            total_width = x_pos;
        }
    }

    public void setReportWidthHeight(){
        if( isForLandscape() ){

        	jasperDesign.setOrientation(OrientationEnum.LANDSCAPE);
            jasperDesign.setPageWidth(842);
            jasperDesign.setPageHeight(595);
            jasperDesign.setColumnWidth(842-60);
        }
        else {

        	jasperDesign.setOrientation(OrientationEnum.PORTRAIT);
            jasperDesign.setPageWidth(595);
            jasperDesign.setPageHeight(842);
            jasperDesign.setColumnWidth(595-60);
        }

        Vector columns = getMetadata().getDisplayableColumns();
        ColumnMetaData columnMetaData = null;
        ColumnPosition columnPosition = null;
        for (int i=columns.size()-1; i >= 0; i--) {
            columnMetaData = (ColumnMetaData) columns.get( i );
            if( !isColumnExportable(columnMetaData) ){
            	continue;
            }
            columnPosition = (ColumnPosition)getColumnPositions().get( columnMetaData.getName() );
            break;
        }
        if(columnPosition != null){
            int reportX = columnPosition.getX();
            int reportWidth = columnPosition.getWidth();
            if((reportX + reportWidth ) > jasperDesign.getPageWidth()){
                jasperDesign.setPageWidth( reportX + reportWidth + 20);
                jasperDesign.setColumnWidth(reportX + reportWidth  - 60);
            }
       }
    }
    private boolean isColumnExportable(ColumnMetaData columnMetaData){
    	String editorName = getMetadata().getName();
    	String columnName = columnMetaData.getName();
    	return ((!columnName.equalsIgnoreCase( editorName+EDIT )  &&
    			  !columnName.equalsIgnoreCase( editorName+MODIFY ) &&
    			  !columnName.equalsIgnoreCase( editorName+ADD ) &&
    			  !columnName.equalsIgnoreCase( editorName+CLONE ) &&
    			  !columnName.equalsIgnoreCase( editorName+COPY ) &&
    			  !columnName.equalsIgnoreCase( editorName+DELETE ) &&
    			  !columnName.equalsIgnoreCase( editorName+LOG ) &&
    			  !columnName.equalsIgnoreCase( editorName+VIEW ) &&
    			  columnMetaData.getType() != ColumnDataType._CDT_LINK)
				);
	}
    
    private static class ColumnPosition implements Serializable{
        private String _name;
        private int _x;
        private int _y;
        private int _width;
        private int _height;

        public ColumnPosition(){
        }

        public void setName(String aName){
            _name = aName;
        }

        public String getName(){
            return _name;
        }

        public void setX(int x){
            _x = x;
        }

        public int getX(){
            return _x;
        }

        public void setY(int y){
            _y = y;
        }

        public int getY(){
            return _y;
        }

        public void setWidth(int width){
            _width = width;
        }

        public int getWidth(){
            return _width;
        }

        public void setHeight(int height){
            _height = height;
        }

        public int getHeight(){
            return _height;
        }
    }
    
 
}
