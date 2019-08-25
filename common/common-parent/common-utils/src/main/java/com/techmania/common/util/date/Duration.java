/*
 * Duration.java
 *
 * Created on October 9, 2003, 2:39 PM
 */

package com.techmania.common.util.date;

import com.techmania.common.util.misc.GenUtl;

import java.util.Date;

/**
 * @author ravi
 */
public class Duration implements java.io.Serializable {
    protected Date _startDt;
    protected Date _endDt;

    /**
     * Creates a new instance of Duration
     */
    public Duration() {
    }

    public Duration(Date startDt, Date endDt) {
        _startDt = startDt;
        _endDt = endDt;
    }

    public Duration(Date startDt) {
        _startDt = startDt;
        _endDt = _startDt;
    }

    public void startNow() {
        _startDt = new Date();
    }

    public void endNow() {
        _endDt = new Date();
    }

    /**
     * Getter for property startDt.
     *
     * @return Value of property startDt.
     */
    public Date getStartDt() {
        return _startDt;
    }

    /**
     * Setter for property startDt.
     *
     * @param startDt New value of property startDt.
     */
    public void setStartDt(Date startDt) {
        this._startDt = startDt;
    }

    /**
     * Getter for property endDt.
     *
     * @return Value of property endDt.
     */
    public Date getEndDt() {
        return _endDt;
    }

    /**
     * Setter for property endDt.
     *
     * @param endDt New value of property endDt.
     */
    public void setEndDt(Date endDt) {
        this._endDt = endDt;
    }

    public long getMilliSeconds() {
        if (_startDt == null || _endDt == null) {
            return 0;
        }

        return _endDt.getTime() - _startDt.getTime();
    }

    public double getSeconds() {
        return this.getMilliSeconds() / 1000.0d;
    }

    public double getMinutes() {
        return this.getMilliSeconds() / 60000.0d;
    }

    public double getHours() {
        return this.getMilliSeconds() / 3600000.0d;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof com.techmania.common.util.date.Duration)) {
            return false;
        }

        Duration other = (Duration) obj;
        if (GenUtl.equals(this._startDt, other._startDt) &&
                GenUtl.equals(this._endDt, other._endDt)) {
            return true;
        }

        return false;
    }

    public String toString() {
        return "Duration[" + this._startDt + "-" + this._endDt + "]";
    }

    public int hashCode() {
        int hashCode = 0;
        if (_startDt != null) {
            hashCode = 31 * _startDt.hashCode();
        }
        if (_endDt != null) {
            hashCode += _endDt.hashCode();
        }

        return hashCode;
    }
}
