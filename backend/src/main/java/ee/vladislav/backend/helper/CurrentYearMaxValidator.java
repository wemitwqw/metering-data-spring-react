package ee.vladislav.backend.helper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CurrentYearMaxValidator implements ConstraintValidator<MaxCurrentYear, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}

		int currentYear = Year.now().getValue();
		return value <= currentYear;
	}
}
