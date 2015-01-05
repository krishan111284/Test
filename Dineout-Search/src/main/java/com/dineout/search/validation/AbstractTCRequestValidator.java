package com.dineout.search.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.request.GenericTCSearchRequest;
import com.dineout.search.utils.RequestUtils;

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
		GenericTCSearchRequest req = (GenericTCSearchRequest) target; 
		if(!(!StringUtils.isEmpty(req.getBycity()) || RequestUtils.isSpatial(req))){
			errors.reject(ErrorCode.MISSING_CITY_LAT_LNG);
		}
	}
}
