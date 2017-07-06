package com.kgSearch.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.kgSearch.util.JsonHandler;

@Controller
public class GraphSearchController {
	
	
	
	@RequestMapping("/testSearch")
	@ResponseBody
	public Map<String, Object> getPolicyInfoList1(HttpServletRequest request, HttpServletResponse response){
		String jsonStr = JsonHandler.readJsonFromRequest(request);
		JSONObject obj = JSONObject.parseObject(jsonStr);
		Driver driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic( "neo4j", "123456" ) );
		Session session = driver.session();
		ArrayList<Object> list = new ArrayList<Object>();
		try ( Transaction tx = session.beginTransaction() )
	    {
	        StatementResult result = tx.run( "MATCH (org:Organization)-[r]-(dp:DeadPerson) WHERE org.legalName = {name} RETURN org.orgID AS orgID, org.address AS address, org.legalName AS name,org.leiCode AS leiCode, org.areaCode AS areaCode, org.latitude AS latitude, org.longitude AS longitude, TYPE(r), dp",
	        		Values.parameters( "name", "临沂市平邑县殡仪馆" ) );
	       /* StatementResult result = tx.run( "MATCH (n) RETURN n" );*/
	        while ( result.hasNext() )
	        {
	            Record record = result.next();
	            
	            list.add(record.asMap());
	            System.out.println( record.asMap().toString() );
	        }
	    }catch(Exception e){
			e.printStackTrace();
		}
		
		return JsonHandler.writeJsontoResponse(3000, list);
	}
	

}
