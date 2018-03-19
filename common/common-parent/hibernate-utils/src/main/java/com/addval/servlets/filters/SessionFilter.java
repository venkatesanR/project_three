package com.addval.servlets.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A Session Filter that takes everything the client
 * would normally output and Validates its session's USER_PROFILE
 * Attribute.
 */

public class SessionFilter implements Filter
{

     /** A list of Pattern objects that match paths to exclude */
    private LinkedList excludePatterns;

    public SessionFilter(){}

	/**
	 * Initializes SessionFilter.
	 * <P>
	 * FilterConfig will read the SessionFilter's Values which
	 * is stored in web.xml and values will be Compiled into a Pattern.
	 * filter-name refers to locate the SessionFilter servlet
	 * filter-mapping refers where the filter is to be applied
	 * init-param refers to the excludePattern which will list the directories
	 * 		where the filter need not be applied. eg., js, css, images
	 *		and the first time login pages doesnot need to be validated for the session.
	 * New excludePattern should start with new <init-param>
	 * <P>
	 * 	<filter>
	 *		<filter-name>sessionFilter</filter-name>
	 *		<filter-class>com.addval.servlets.filters.SessionFilter</filter-class>
	 *		<init-param>
	 *		  <param-name>excludePattern.1</param-name>
	 *		  <param-value>login</param-value>
	 *		</init-param>
	 * 	</filter>
	 *
	 *	<filter-mapping>
	 *		<filter-name>sessionFilter</filter-name>
	 *		<url-pattern>/*</url-pattern>
	 *	</filter-mapping>
	 */

    public void init(FilterConfig config)
        throws ServletException
    {
        /* parse all of the initialization parameters, collecting the
        exclude from web.xml excludePattern */

        Enumeration enumeration = config.getInitParameterNames();
        excludePatterns = new LinkedList();
		if (enumeration != null) {
			while( enumeration.hasMoreElements() )
			{
				String paramName = ( String )enumeration.nextElement();
				String paramValue = config.getInitParameter( paramName );

				if( paramName.startsWith( "excludePattern" ) )
				{
					// compile the pattern
					Pattern excludePattern = Pattern.compile( paramValue );
					excludePatterns.add( excludePattern );
				}
			}
		}
    }


    /**
	 * When servlets or JSP is accessed , doFilter() method will be called,
	 * It will check for the Valid Session and looks for the USER_PROFILE.
	 * If the USER_PROFILE is not available in the session, we just redirect user
	 * to logout page.
	 */
	 public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpSession session = ((HttpServletRequest)request).getSession();
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String urlStr = "../logout.jsp";
//        String name = "";
//        if (request instanceof HttpServletRequest){
//            name = ((HttpServletRequest)request).getRequestURI();
//        }
        if( !isFilteredRequest( req ) )
        {
            chain.doFilter( request, response );
            return;
        }

        if(session.getAttribute("USER_PROFILE") == null)
        {
            String requestUrl = req.getRequestURL().toString();
            int endIndex = requestUrl.lastIndexOf( "/" );
			requestUrl = requestUrl.substring( 0, endIndex + 1 ) + urlStr;
            res.sendRedirect( requestUrl );
            session.invalidate();
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    public void destroy(){}


  private boolean isFilteredRequest(HttpServletRequest request)
  {
    String path = request.getRequestURI();
    Iterator patternIter = excludePatterns.iterator();
    while( patternIter.hasNext() )
    {
      Pattern p = (Pattern)patternIter.next();
      Matcher m = p.matcher(path);
      if( m.find(0))
        return false;
    }
    return true;
  }

}