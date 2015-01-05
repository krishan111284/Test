package com.dineout.search.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.dineout.search.request.RestSearchRequest;

@Component(value="tcEstRequestValidator")
public class TCEstRequestValidator extends AbstractTCRequestValidator{

	@Override
	public boolean supports(Class<?> clazz) {
		return RestSearchRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateCityLatLng(target,errors);		
	}
}
