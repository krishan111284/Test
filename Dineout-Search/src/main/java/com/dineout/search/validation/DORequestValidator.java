package com.dineout.search.validation;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.dineout.search.request.DOSearchHeader;

@Component(value="doRequestValidator")
public class DORequestValidator extends AbstractDORequestValidator {
	Logger logger = Logger.getLogger(DORequestValidator.class);
	@Override
	public boolean supports(Class<?> clazz) {
		return DOSearchHeader.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		checkIfEmpty(errors);		
		}
}
