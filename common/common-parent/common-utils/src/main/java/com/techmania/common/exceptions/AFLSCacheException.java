package com.techmania.common.exceptions;

import com.techmania.common.exceptions.XRuntime;

public class AFLSCacheException extends XRuntime {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param source
     * @param desc
     */
    public AFLSCacheException(String source, String desc) {
        super(source, desc);
    }

    /**
     * @param desc
     */
    public AFLSCacheException(String desc) {
        super(desc);
    }
}
