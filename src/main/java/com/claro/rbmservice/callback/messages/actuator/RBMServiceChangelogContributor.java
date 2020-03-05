package com.claro.rbmservice.callback.messages.actuator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.info.Info.Builder;

@Component
public class RBMServiceChangelogContributor implements InfoContributor {

	@Override
	public void contribute(Builder builder) {
		Map<String,List<String>> changeMap = new LinkedHashMap <String,List<String>>();	
		
		List<String> v1_0_list= new LinkedList<String>();
		v1_0_list.add("Version inicial del web service de RBM.");

		//Mas nuevos arriba
		changeMap.put("1.0 - 29/11/2019", v1_0_list);
		
		Map<String, Object> toShow = new HashMap<String, Object>();		
		toShow.put("Cambios", changeMap);
		builder.withDetails(toShow).build();		
		
	}
	

}