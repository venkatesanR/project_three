package com.addval.utils;

public class ValidatorSpecification {
	private final String validatorType;

	private final String constraintValue;

	public ValidatorSpecification(String validatorType) {
		this(validatorType, null);
	}

	public ValidatorSpecification(String validatorType, String constraintValue) {
		this.validatorType = validatorType;
		this.constraintValue = constraintValue;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public String getValidatorType() {
		return validatorType;
	}

	@Override
	public String toString() {
		return String.format("ValidatorSpecification[%s %s]", validatorType, constraintValue);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		ValidatorSpecification ov = (ValidatorSpecification) other;
		if (!validatorType.equals(ov.validatorType)) {
			return false;
		}
		return StrUtl.equalsIgnoreCase(constraintValue, ov.constraintValue);
	}
}
