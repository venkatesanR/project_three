package com.addval.udf.api;

import java.io.Serializable;

/**
 * This is the basic interface that all other Udf "Constraint" classes must implement.
 */
public abstract class UdfFieldValueConstraint implements Serializable {

	private String _constraintOrigin;

	public String getConstraintOrigin() {
		return _constraintOrigin;
	}

	public void setConstraintOrigin(String constraintOrigin) {
		_constraintOrigin = constraintOrigin;
	}

	/**
	 * Checks the specified valueObject against the Constraint specification.
	 * Returns an error message if the valueObject violates the Constraint,
	 * otherwise returns null.
	 */
	abstract public String validate(Object valueObject);

}