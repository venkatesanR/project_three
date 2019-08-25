/*
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.techmania.common.exceptions;

/**
 * Extends the RuntimeException class. Stores information about the SQL error and desc of the Runtime Exception. This helps to handle implicit rollback in EJB layer whenever SQLException happens
 */
public class SQLRuntimeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6932548749369748703L;

	/** The _source. */
	private String _source;

	/** The _err number. */
	private int _errNumber;

	public SQLRuntimeException(String source, String desc) {
		super(desc);
		_source = source;
	}

	public SQLRuntimeException(String source, String desc, Throwable e) {
		super(desc);
		_source = source;
	}

	public SQLRuntimeException(String desc) {
		super(desc);
	}

	public SQLRuntimeException(String desc, Throwable e) {
		super(desc);
	}

	public SQLRuntimeException(int errNumber, String desc, Throwable e) {
		super(desc, e);
		_errNumber = errNumber;
	}

	public String getSource() {
		return _source;
	}

	public String getMessage() {
		return super.getMessage();
	}

	public int getErrNumber() {

		return _errNumber;
	}

	public String toString() {
		return ("SQLRuntimeException: Source : " + getSource() + "; Error: " + getMessage());
	}
}
