package com.dineout.search.query;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.utils.Constants;

@Component("idSearchQueryCreator")
public class IdSearchQueryCreator {
	Logger logger = Logger.getLogger(IdSearchQueryCreator.class);

	public QueryParam getSearchQuery(DORestSearchRequest req,
			Map<String, ArrayList<String>> nerMap) throws SearchException {
		QueryParam queryParam = new QueryParam();
		String[] ids = req.getRestIds();
		if(ids!=null && ids.length>0){
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for(String id:ids){
				sb.append(id +" OR ");
			}
			String idFilter = sb.substring(0, sb.lastIndexOf(" OR ")) + ")";
			queryParam.addParam("fq", "r_id:"+idFilter);
			queryParam.addParam("defType","edismax");
			queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		}
		return queryParam;
	}
}
