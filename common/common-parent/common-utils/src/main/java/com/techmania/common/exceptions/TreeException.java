//Source file: C:\\Projects\\Common\\source\\com\\addval\\utils\\trees\\TreeException.java

package com.techmania.common.exceptions;

import com.techmania.common.exceptions.XRuntime;

/**
 * Exception class used for reporting "usage errors" by classes in this package.
 */
public class TreeException extends XRuntime//### or should we make it XGeneral?
{
    /**
     * @param source - String describing the location within source code from which the exception is being thrown
     * @param desc   - Description of the exceptional condition that was detected
     */
    public TreeException(String source, String desc) {
        super(source, desc);
    }

    /**
     * @param desc - Description of the exceptional condition that was detected
     */
    public TreeException(String desc) {
        super("", desc);
    }

}
