package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kgSearch.pojo.AuthToken;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.INeo4jDBService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-mybatis.xml" })
public class Test {
	
	@Resource
	private INeo4jDBService service;
	
	
	@org.junit.Test
	public void t2(){
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		
		Map<String,ArrayList<String>> labelPropertiesMap=new HashMap<>();
		Map<String,ArrayList<String>> typePropertiesMap=new HashMap<>();
		Map<String,Integer> labelIntMap=new HashMap<>();
		Map<String,Integer> typeIntMap=new HashMap<>();
		
		ArrayList<TypeProperties> typeProperties=(ArrayList<TypeProperties>)service.selectAllTypes();
		ArrayList<LabelProperties> labelProperties=(ArrayList<LabelProperties>)service.selectAllLabels();
		for(TypeProperties element:typeProperties){
			typeIntMap.put(element.getType(),element.getId());
			ArrayList<String> propertiesOfAType=new ArrayList<>();
			String[] temp=element.getProperties().split(",");
			for(String iter:temp){
				propertiesOfAType.add(iter);
			}
			typePropertiesMap.put(element.getType(),propertiesOfAType);
		}
		for(LabelProperties element:labelProperties){
			labelIntMap.put(element.getLabel(),element.getId());
			ArrayList<String> propertiesOfALabel=new ArrayList<>();
			String[] temp=element.getProperties().split(",");
			for(String iter:temp){
				propertiesOfALabel.add(iter);
			}
			labelPropertiesMap.put(element.getLabel(),propertiesOfALabel);
		}
	
		System.out.println("typePropertiesMap:"+typePropertiesMap);
		System.out.println("labelPropertiesMap:"+labelPropertiesMap);
		System.out.println("typeIntMap:"+typeIntMap);
		System.out.println("labelIntMap:"+labelIntMap);
		
		verbList.add("R1");
		verbList.add("R3");
		
		adList.add("l2");
		adList.add("l1");
		adList.add("l3");
		
		nounList.add("l2a");
		
		AuthToken authToken=new AuthToken();
		authToken.setUserName("neo4j");
		authToken.setPassword("huhantao9");
		Neo4jHandler handler=new Neo4jHandler(verbList,adList,nounList,"bolt://localhost:7687", authToken);
		handler.setPropertiesMaps(labelIntMap, typeIntMap, labelPropertiesMap, typePropertiesMap);
		handler.initIdMaps();
		
		handler.searchAction();
	}
	
	/*@org.junit.Test
	public void t1(){
		iNeo4jDBService.selectAllLabels();
	}*/
}
