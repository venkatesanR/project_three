
/* AddVal Technology Inc. */

package com.addval.taglib;

import com.addval.metadata.EditorMetaData;
import java.io.IOException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public class HeadingTag extends GenericBodyTag
{
    private int _type = 1;
	private String _viewrole = "";
	private String _editrole = "";
	private boolean _hasViewPermission = true;
	private boolean _hasEditPermission = true;

    /**
     * @param name
     * @return void
     * @exception
     * @roseuid 3B42E45D02F6
     */
    public void setName(String name)
    {
        if ( name.equalsIgnoreCase("SUB") )
            _type = 2;
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42E48102B1
     */
    public int doEndTag() throws JspTagException
    {
        try
        {

 			if ((_editrole != null) && (_editrole.length() > 0))
 			{
 				javax.servlet.http.HttpServletRequest httpreq = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();

 				if (httpreq.isUserInRole(_editrole))
 					_hasEditPermission = true;
 				else
 					_hasEditPermission = false;
 			}


 			int colspan = 0;
            Object obj = pageContext.getAttribute ( getId(), getScope() );
            if (obj != null && obj instanceof EditorMetaData)
            {
                EditorMetaData editorMetaData = ( EditorMetaData ) obj;
                int _searchColumnsPerRow = 4;
                int searchSize = ( editorMetaData.getSearchableColumns()   == null ? 0 : (editorMetaData.getSearchableColumns().size() < _searchColumnsPerRow ) ? _searchColumnsPerRow * 2 : editorMetaData.getSearchableColumns().size() * 2  + 3 );
                int aggSize    = ( editorMetaData.getAggregatableColumns() == null ? 0 : editorMetaData.getAggregatableColumns().size() + 1 );
                int dispSize   = ( editorMetaData.getDisplayableColumns()  == null ? 0 : editorMetaData.getDisplayableColumns().size() + 3 );

				if (_hasEditPermission == false)
				{
					// this should ideally be calculated from the editorMetaData.
					// but that is going to involve more looping and performance
					// so for now we will assume that the number of linkColumns is 3 and we will
					// subtract that from the displayable columns

					final int linkColumns = 3;


					dispSize = dispSize - linkColumns;
				}

                colspan = aggSize;
                if ( searchSize > colspan )
                    colspan = searchSize;
                if ( dispSize > colspan )
                    colspan = dispSize;
            }
            else
            {
                throw new IllegalArgumentException( "Invalid Object ID." );
            }
            BodyContent tagbody = getBodyContent();
            String body_text = "";
            if (tagbody != null)
            {
                body_text = "<tr>";

                if (_type == 2)
                    body_text += "<th colspan = \""+colspan+"\"  class=\"subheading\">";
                else
                    body_text += "<th colspan = \""+colspan+"\"  class=\"heading\">";

                body_text += tagbody.getString();
                body_text += "</th></tr>";
            }
            pageContext.getOut().write ( body_text.trim() );
        }
        catch ( IOException e )
        {
            throw new JspTagException( "HeadingTag Error:" + e.toString() );
        }
        return EVAL_PAGE;
    }


	public String getViewrole()
	{
        return _viewrole;
	}

	public void setViewrole(String aViewrole)
	{
        _viewrole = aViewrole;
	}

	public String getEditrole()
	{
        return _editrole;
	}

	public void setEditrole(String aEditrole)
	{
        _editrole = aEditrole;
	}


}
