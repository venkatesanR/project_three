package com.addval.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.addval.jaxbutils.InvalidDate;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = { ValidDate.DateValidator.class })
public @interface ValidDate {

	String message() default "{ValidDate-error}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public class DateValidator implements ConstraintValidator<ValidDate, Date> {
		@Override
		public void initialize(final ValidDate validDate) {
		}

		@Override
		public boolean isValid(final Date value, final ConstraintValidatorContext constraintValidatorContext) {
			if (value != null && value instanceof InvalidDate) {
				return false;
			}
			return true;
		}
	}

}
