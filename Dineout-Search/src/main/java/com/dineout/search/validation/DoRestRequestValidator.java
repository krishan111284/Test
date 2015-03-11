package com.dineout.search.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.dineout.search.request.DORestSearchRequest;

@Component(value="doRestRequestValidator")
public class DoRestRequestValidator extends AbstractDORequestValidator{

	@Override
	public boolean supports(Class<?> clazz) {
		return DORestSearchRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateCityLatLng(target,errors);		
	}
}
