package com.kgSearch.test;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kgSearch.method.impl.Neo4jHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-mybatis.xml" })
public class TestNeo4jHandler {

	@Resource
	private Neo4jHandler neo4jHandler;
	
	
	@Test
	public void t2(){
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		verbList.add("R1");
		verbList.add("R3");
		
		adList.add("r2");
		adList.add("l1");
		adList.add("l51");
		
		nounList.add("L2");
		nounList.add("L4");
		nounList.add("l34a");
		nounList.add("l34b");
		
		neo4jHandler.setSession(GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j","123456")).session());
		neo4jHandler.setVerbList(verbList);
		neo4jHandler.setAdList(adList);
		neo4jHandler.setNounList(nounList);
		neo4jHandler.initPropertiesMaps();
		neo4jHandler.initPropertiesMaps();
		neo4jHandler.initIdMaps();
		neo4jHandler.searchAction();
	}
	
}
