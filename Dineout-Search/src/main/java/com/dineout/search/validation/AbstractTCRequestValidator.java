package com.dineout.search.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.request.GenericDOSearchRequest;
import com.dineout.search.utils.DORequestUtils;

public abstract class AbstractTCRequestValidator implements Validator{
	private  String[] params;
	
	public void validatorResourceData(Object target, Errors errors, String ... params){
		this.params=params;
		validate(target, errors);
	}
	
	public void checkIfEmpty(Errors errors){
		for(String parameter : params){										
			ValidationUtils.rejectIfEmpty(errors, parameter, ErrorCode.MISSING_PARAMETER_ERROR,parameter);
		}
	}
	public void validateCityLatLng(Object target,Errors errors){
		GenericDOSearchRequest req = (GenericDOSearchRequest) target; 
		if(!(!StringUtils.isEmpty(req.getBycity()) || DORequestUtils.isSpatial(req))){
			errors.reject(ErrorCode.MISSING_CITY_LAT_LNG);
		}
	}
}
