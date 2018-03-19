package com.addval.aswtaglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

public class SetPropertyTag extends BodyTagSupport implements Cloneable {
	private String _name;
	private String _value;
	
	/**
	 * @param name
	 * @roseuid 3E70640F02A7
	 */
	public void setName(String name) {
        _name = name;		
	}
	
	/**
	 * @param value
	 * @roseuid 3E7064100168
	 */
	public void setValue(String value) {
        _value = value;		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 3E70641003C1
	 */
	public String getName() {
        return _name;		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 3E706411005B
	 */
	public String getValue() {
        return _value;		
	}
	
	/**
	 * --------------------------------------------------------- Tag API methods
	 * Passes attribute information up to the parent TableTag.<p>
	 * When we hit the end of the tag, we simply let our parent (which better
	 * be a TableTag) know what the user wants to change a property value, and
	 * we pass the name/value pair that the user gave us, up to the parent
	 * @return int
	 * @throws javax.servlet.jsp.JspException if this tag is being used outside of a
	 * <addval:table...> tag.
	 * @roseuid 3E70641101B0
	 */
	public int doEndTag() throws JspException {
        Object parent = this.getParent();

        if (parent == null) {
            throw new JspException( "Can not use column tag outside of a " +
                 "TableTag. Invalid parent = null" );
        }

        if (!( parent instanceof TableTag )) {
            throw new JspException( "Can not use column tag outside of a " +
                 "TableTag. Invalid parent = " +
                    parent.getClass().getName() );
        }

        ((TableTag)parent).setProperty( _name, _value );

        return super.doEndTag();		
	}
}
