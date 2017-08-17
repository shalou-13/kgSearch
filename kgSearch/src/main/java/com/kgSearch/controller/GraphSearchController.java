package com.kgSearch.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.kgSearch.dao.ChildrenMissionsMapper;
import com.kgSearch.dao.MissionsMapper;
import com.kgSearch.method.impl.CustomerNeo4jHandler;
import com.kgSearch.method.impl.ReNeo4jHandler;
import com.kgSearch.pojo.ChildrenMissions;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.MissionsWithBLOBs;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.impl.LabelService;
import com.kgSearch.service.impl.RelationTypeService;
import com.kgSearch.util.JsonHandler;

@Controller
public class GraphSearchController {
	
	/*@Autowired
	private ReNeo4jHandler reNeo4jHandler;*/
	
	@Autowired
	private LabelService labelService;
	@Autowired
	private RelationTypeService relationTypeService;
	@Resource
	private ChildrenMissionsMapper childrenMissionsMapper;
	@Resource
	private MissionsMapper missionsMapper;
	
	
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
		
		return JsonHandler.writeJsontoResponse(4000, list);
	}
	

	@RequestMapping("/movieSearch")
	@ResponseBody
	public Map<String, Object> movieSearch(HttpServletRequest request, HttpServletResponse response){
		String jsonStr = JsonHandler.readJsonFromRequest(request);
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String graphID = obj.getString("graphID");
		String typeID = obj.getString("typeID");
		String engine = obj.getString("engine");
		int subMissionID = obj.getIntValue("subMissionID");
		ChildrenMissions sub_mission = childrenMissionsMapper.selectByPrimaryKey(subMissionID);
		if(sub_mission==null)
			return JsonHandler.writeJsontoResponse(4002, "");
		MissionsWithBLOBs mission = missionsMapper.selectByPrimaryKey(sub_mission.getPId());
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		if(mission.getAkeyword()!=null && !mission.getAkeyword().equals("")){
			JSONObject a_obj = JSONObject.parseObject(mission.getAkeyword());  
			adList = new ArrayList<String>(a_obj.keySet());
		}
		if(mission.getNkeyword()!=null && !mission.getNkeyword().equals("")){
			JSONObject n_obj = JSONObject.parseObject(mission.getNkeyword());  
			nounList = new ArrayList<String>(n_obj.keySet());
		}
		if(mission.getVkeyword()!=null && !mission.getVkeyword().equals("")){
			JSONObject v_obj = JSONObject.parseObject(mission.getVkeyword());  
			verbList = new ArrayList<String>(v_obj.keySet());
		}
		if(verbList.size()!=0||adList.size()!=0||nounList.size()!=0){
			ReNeo4jHandler reNeo4jHandler = new ReNeo4jHandler();
			reNeo4jHandler.setVerbList(verbList);
			reNeo4jHandler.setAdList(adList);
			reNeo4jHandler.setNounList(nounList);
			Class<?> c;
			try {
				c = Class.forName(engine);
				Object graphHandler = c.newInstance();
				if(graphHandler instanceof ReNeo4jHandler){
					Driver driver=GraphDatabase.driver("bolt://localhost",AuthTokens.basic("neo4j","123456"));
					reNeo4jHandler.initSession(driver);
					ArrayList<LabelProperties> AllLabel=labelService.getAllLabel();
					ArrayList<TypeProperties> AllType=relationTypeService.GetAllRelationType();
					reNeo4jHandler.initIdMap(AllLabel, AllType);
					reNeo4jHandler.searchAction(labelService, relationTypeService);
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put("TResult", reNeo4jHandler.getTResult());
					result.put("SResult", reNeo4jHandler.getSResult());
					result.put("sub_RTL", reNeo4jHandler.getSub_RTL());
					result.put("sub_ELL", reNeo4jHandler.getSub_ELL());
					result.put("sub_EL", reNeo4jHandler.getSub_EL());
					result.put("RTL", reNeo4jHandler.getRTL());
					result.put("ELL", reNeo4jHandler.getELL());
					result.put("EL", reNeo4jHandler.getEL());
					result.put("graphID", graphID);
					result.put("graphTypeID", typeID);
					return JsonHandler.writeJsontoResponse(4000, result);
				}
				return JsonHandler.writeJsontoResponse(4004, "");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4003, "");
			} catch (InstantiationException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4004, "");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4004, "");
			}
		}
		return JsonHandler.writeJsontoResponse(4005, "");
	}
	
	@RequestMapping("/customerSearch")
	@ResponseBody
	public Map<String, Object> customerSearch(HttpServletRequest request, HttpServletResponse response){
		String jsonStr = JsonHandler.readJsonFromRequest(request);
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String graphID = obj.getString("graphID");
		String typeID = obj.getString("typeID");
		String engine = obj.getString("engine");
		int subMissionID = obj.getIntValue("subMissionID");
		ChildrenMissions sub_mission = childrenMissionsMapper.selectByPrimaryKey(subMissionID);
		if(sub_mission==null)
			return JsonHandler.writeJsontoResponse(4002, "");
		MissionsWithBLOBs mission = missionsMapper.selectByPrimaryKey(sub_mission.getPId());
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		if(mission.getAkeyword()!=null && !mission.getAkeyword().equals("")){
			JSONObject a_obj = JSONObject.parseObject(mission.getAkeyword());  
			adList = new ArrayList<String>(a_obj.keySet());
		}
		if(mission.getNkeyword()!=null && !mission.getNkeyword().equals("")){
			JSONObject n_obj = JSONObject.parseObject(mission.getNkeyword());  
			nounList = new ArrayList<String>(n_obj.keySet());
		}
		if(mission.getVkeyword()!=null && !mission.getVkeyword().equals("")){
			JSONObject v_obj = JSONObject.parseObject(mission.getVkeyword());  
			verbList = new ArrayList<String>(v_obj.keySet());
		}
		if(verbList.size()!=0||adList.size()!=0||nounList.size()!=0){
			CustomerNeo4jHandler reNeo4jHandler = new CustomerNeo4jHandler();
			reNeo4jHandler.setVerbList(verbList);
			reNeo4jHandler.setAdList(adList);
			reNeo4jHandler.setNounList(nounList);
			Class<?> c;
			try {
				c = Class.forName(engine);
				Object graphHandler = c.newInstance();
				if(graphHandler instanceof CustomerNeo4jHandler){
					Driver driver=GraphDatabase.driver("bolt://localhost",AuthTokens.basic("neo4j","123456"));
					reNeo4jHandler.initSession(driver);
					ArrayList<LabelProperties> AllLabel=labelService.getAllLabel();
					ArrayList<TypeProperties> AllType=relationTypeService.GetAllRelationType();
					reNeo4jHandler.initIdMap(AllLabel, AllType);
					reNeo4jHandler.searchAction(labelService, relationTypeService);
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put("TResult", reNeo4jHandler.getTResult());
					result.put("SResult", reNeo4jHandler.getSResult());
					result.put("sub_RTL", reNeo4jHandler.getSub_RTL());
					result.put("sub_ELL", reNeo4jHandler.getSub_ELL());
					result.put("sub_EL", reNeo4jHandler.getSub_EL());
					result.put("RTL", reNeo4jHandler.getRTL());
					result.put("ELL", reNeo4jHandler.getELL());
					result.put("EL", reNeo4jHandler.getEL());
					result.put("graphID", graphID);
					result.put("graphTypeID", typeID);
					return JsonHandler.writeJsontoResponse(4000, result);
				}
				return JsonHandler.writeJsontoResponse(4004, ""); 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4003, "");
			} catch (InstantiationException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4004, "");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return JsonHandler.writeJsontoResponse(4004, "");
			}
		}
		return JsonHandler.writeJsontoResponse(4005, "");
	}
}
