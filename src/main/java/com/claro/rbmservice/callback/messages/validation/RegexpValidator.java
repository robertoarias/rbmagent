package com.claro.rbmservice.callback.messages.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegexpValidator implements ConstraintValidator<MatchRegexp, Object>{
	
	private Pattern pattern;
	
	@Override
	public void initialize(MatchRegexp constrain) {
		String pat = constrain.regexp();
				
		pattern = Pattern.compile(pat);	
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		String valTomatch="";
		
		if (value instanceof String)
		{
			valTomatch = (String)value;
		}else if (value instanceof Integer){
			Integer integerVal= (Integer) value;
			valTomatch = Integer.toString(integerVal);
		}
			
		if (valTomatch==null ){
			return true;
		}
		if (pattern==null)
		{
			return false;
			
		}
		Matcher matcher = pattern.matcher(valTomatch);
        return matcher.matches();
	}
}
