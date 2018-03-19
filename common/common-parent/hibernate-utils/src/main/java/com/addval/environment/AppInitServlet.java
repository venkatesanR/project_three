//Source file: D:/users/prasad/Projects/Common/src/client/source/com/addval/environment/AppInitServlet.java

/* Copyright AddVal Technology Inc. */

package com.addval.environment;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.addval.esapiutils.validator.AppSecurityValidatorException;
import com.addval.esapiutils.validator.HTMLSecurityValidator;
import com.addval.esapiutils.validator.HTMLSecurityValidatorESAPIImpl;
import com.addval.utils.XRuntime;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import com.addval.utils.AVConstants;

/**
   A pre-initialized servlet that is  executed only once during the
   startup process. The Environment instance is indirectly created through this object.
   Think of this similar to the "main()" method in a stand-alone
   executable.
   @author Sankar Dhanushkodi
   @revision $Revision$
 */
public class AppInitServlet extends HttpServlet {
	private HTMLSecurityValidator htmlSequrityValidator;

	/**
	   This function creates an instance of the Environment class
	   with the configFile got as the parameter to this servlet.
	   @param full path to configuration file.
	   @return:
	   @exception ServletException
	   @roseuid 387D0CA400DD
	 */
	public void setHTMLSecurityValidator(HTMLSecurityValidator htmlSequrityValidator){
		this.htmlSequrityValidator=htmlSequrityValidator;
	}
	public HTMLSecurityValidator getHTMLSecurityValidator(){
		if(htmlSequrityValidator==null){
			htmlSequrityValidator= new HTMLSecurityValidatorESAPIImpl();
		}
		return htmlSequrityValidator;
	}
	
	
	public void init(ServletConfig config) throws ServletException {

      super.init(config);
      //String configFile = getInitParameter("configFile");
      //initApp( configFile );
   }

	/**
	   Calls the destroy of the super class
	   @param:
	   @return:
	   @exception:
	   @roseuid 387D0CA400DF
	 */
	public void destroy() {
      super.destroy();
   }

	/**
	   Redirects call to doPost
	   @param:
	   @return:
	   @exception ServletException
	   @exception IOException
	   @see AppInitServlet#doPost
	   @roseuid 387D0CA400E0
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
      doPost( req, res );
   }

	/**
	   Sets the Response objects content type from the
	   Request object.
	   @param req HttpServletRequest
	   @param res HttpServletResponse
	   @result
	   @exception ServletException
	   @exception IOException
	   @roseuid 387D0CA400E9
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {

      res.setContentType( req.getContentType() );
      PrintWriter out = res.getWriter();
      
	try {
		String	vaildHtml = getHTMLSecurityValidator().getValidSafeHTML("AppInitServlet", "<strong>calling initApp( " + req.getParameter( "configFile" ) + " )...</strong><br>");
		out.println( vaildHtml );
	} catch (AppSecurityValidatorException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      

      try {
         initApp( req.getParameter( "configFile" ) );
        String vaildHtml = getHTMLSecurityValidator().getValidSafeHTML("AppInitServlet", "<strong>Swing Solutions Environment successfully initialized!</strong><br>");
         
         out.println( vaildHtml );
      }
      catch ( AppSecurityValidatorException e ) {
         out.println( "Exception cought while trying to initialize Swing Solutions Environment:" );
         out.println( e.toString() );
      }
   }

	/**
	   Makes a call to create the Environment object
	   @param configFile - Complete path to the configuration
	   file
	   @return:
	   @exception:
	   @roseuid 387D0CA400EC
	 */
	public void initApp(String configFile) {

      // init. envrironment object
      Environment.make( AVConstants._DEFAULT, configFile );
    //  Environment.getInstance().getLogFileMgr().logInfo( "AppInitServlet", "Successfully Initialized" );
      System.gc();
   }
}
