package com.dineout.search.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.SolrParams;

public class QueryParam extends SolrParams{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7030923975626977395L;
	Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
	
	@Override
	public String[] getParams(String param) {
		if(paramMap.get(param)!=null){
			return (String[])paramMap.get(param).toArray(new String[paramMap.get(param).size()]);
		}else{
			return null;
		}
	}
	
	@Override
	public Iterator<String> getParameterNamesIterator() {
		return paramMap.keySet().iterator();
	}
	
	@Override
	public String get(String param) {
		String val = null;
		if(paramMap.get(param)!=null && paramMap.get(param).size()==1){
			val = paramMap.get(param).get(0);
		}
		return val;
	}
	
	public void addParam(String param, String val){
		if(paramMap.get(param)!=null){
			paramMap.get(param).add(val);
		}else{
			List<String> valList = new ArrayList<String>();
			valList.add(val);
			paramMap.put(param, valList);
		}
	}
	
	public void addParams(String param, List<String> val){
		if(paramMap.get(param)!=null){
			paramMap.get(param).addAll(val);
		}else{
			List<String> valList = new ArrayList<String>();
			valList.addAll(val);
			paramMap.put(param, valList);
		}
	}
	public void updateParam(String param,String val){
		List<String> valList = new ArrayList<String>();
		valList.add(val);
		paramMap.remove(param);
		paramMap.put(param, valList);
	}
}
