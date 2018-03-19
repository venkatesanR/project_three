
/* AddVal Technology Inc. */

package com.addval.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.Tag;
import java.util.Vector;

/**
 * @author
 * @version
 */
public abstract class GenericTag extends TagSupport
{
    private int _scope;

    public GenericTag()
    {
        this._scope = pageContext.PAGE_SCOPE; //1
    }

    /**
     * @param scope
     * @return void
     * @exception
     * @roseuid 3B42D0E70338
     */
    public void setScope(String scope)
    {
        if (scope.equalsIgnoreCase( "page" ))
            this._scope = pageContext.PAGE_SCOPE;            //1
        else if (scope.equalsIgnoreCase( "request" ))
            this._scope = pageContext.REQUEST_SCOPE;         //2
        else if (scope.equalsIgnoreCase( "session" ))
            this._scope = pageContext.SESSION_SCOPE;         //3
        else if (scope.equalsIgnoreCase( "application" ))
            this._scope = pageContext.APPLICATION_SCOPE;     //4
        else
            this._scope = pageContext.PAGE_SCOPE;
    }

    /**
     * @param scope
     * @return void
     * @exception
     * @roseuid 3B42D116026D
     */
    public void setScope(int scope)
    {
        this._scope = scope;
    }

    /**
     * @return int
     * @exception
     * @roseuid 3B42D13102EE
     */
    public int getScope()
    {
        return this._scope;
    }

    /**
     * @param att
     * @return Object
     * @exception
     * @roseuid 3B42D1820287
     */
    protected Object getAttribute(String att)
    {
        if (pageContext == null || att == null)
            return null;
        if (att.startsWith( "page." )) {
            String pag = att.substring( 5 );
            if (pag == null)
                return null;
            else
                return pageContext.getAttribute( pag,  pageContext.PAGE_SCOPE );
        }
        else if (att.startsWith( "request." )) {
            String req = att.substring( 8 );
            if (req == null)
                return null;
            else
                return pageContext.getAttribute( req,  pageContext.REQUEST_SCOPE );
        }
        else if (att.startsWith( "session." )) {
            String ses = att.substring( 8 );
            if (ses == null)
                return null;
            else
                return pageContext.getAttribute( ses,  pageContext.SESSION_SCOPE );
        }
        else if (att.startsWith( "application." )) {
            String app = att.substring( 12 );
            if (app == null)
                return null;
            else
                return pageContext.getAttribute( app,  pageContext.APPLICATION_SCOPE );
        }
        else {
            return pageContext.getAttribute( att );
        }
    }

    /**
     * @param tag
     * @param className
     * @return Tag
     * @exception
     * @roseuid 3B42D1F90101
     */
    public static final Tag findAncestorWithClass(Tag tag, String className)
    {
        try {
            return TagSupport.findAncestorWithClass( tag,  Class.forName( className ) );
        }
        catch( Exception _ex ) {
            return null;
        }
    }

    /**
     * @return Tag[]
     * @exception
     * @roseuid 3B42D6400387
     */
    public final Tag[] getAncestors()
    {
        Vector vec = new Vector();
        for (Object obj = this; ( obj = ( ( Tag ) ( obj ) ).getParent() ) != null;)
            vec.addElement(obj);

        int i = vec.size();
        Tag tags[] = new Tag[i];
        if (i != 0)
            vec.copyInto(tags);
        return tags;
    }

    /**
     * @param className
     * @return Tag[]
     * @exception
     * @roseuid 3B42D6550034
     */
    public Tag[] getAncestors(Class className)
    {
        Vector vec = new Vector();
        for (Object obj = this; ( obj = TagSupport.findAncestorWithClass( ( ( Tag ) ( obj ) ),  className ) ) != null;)
            vec.addElement ( obj );

        int i = vec.size();
        Tag tags[] = new Tag[i];
        if (i != 0)
            vec.copyInto( tags );
        return tags;
    }

    /**
     * @param className
     * @return Tag[]
     * @exception
     * @roseuid 3B42D678039B
     */
    public Tag[] getAncestors(String className)
    {
        try {
            return getAncestors( Class.forName( className ) );
        }
        catch( Exception _ex ) {
            return new Tag[0];
        }
    }

    /**
     * @return void
     * @exception
     * @roseuid 3B42D6A70118
     */
    public void release()
    {
        _scope = pageContext.PAGE_SCOPE;
        super.release();
    }
}
