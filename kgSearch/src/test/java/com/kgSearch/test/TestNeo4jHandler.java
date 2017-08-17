package com.kgSearch.test;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kgSearch.dao.LabelPropertiesMapper;
import com.kgSearch.method.impl.ReNeo4jHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-mybatis.xml" })
public class TestNeo4jHandler {

	Driver driver=GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","huhantao9"));
	Session session=driver.session();
	
	@Resource
	private ReNeo4jHandler reNeo4jHandler;
	
	@Resource
	private LabelPropertiesMapper labelPropertiesMapper;
	
	/*@Test
	public void t1(){
		ArrayList<LabelProperties> lp1=(ArrayList<LabelProperties>)labelPropertiesMapper.fuzzySelectLabelByString("L");
		System.out.println(lp1.size());
	}*/
	
	/*@Test
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
	}*/
	
	/*@Test
	public void t3(){
		StatementResult statementResult=session.run("match (x:L1),(y:L2) return ((x)<-[*]-(y))");
		while(statementResult.hasNext()){
			Record record=statementResult.next();
			System.out.println(record.size());
			System.out.println(record.get(0).size());
			System.out.println(record.get(0).get(record.get(0).size()-1).asPath().nodes());
			System.out.println(record.get(0).get(record.get(0).size()-1).asPath().relationships());
			
			System.out.println(record.get(0).get(0).get(1).asRelationship().id());
			System.out.println(record.get(0).get(0).get(2).asRelationship().id());
			System.out.println(record.get(0).get(0).get(3).asNode().id());
		}
	}*/
	
	@Test
	public void t4(){
		Driver driver=GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","huhantao9"));
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		
		verbList.add("ACTED_IN");
		verbList.add("WROTE");
		verbList.add("123");
		
		adList.add("sum");
		adList.add("tag");
		adList.add("lea");
		adList.add("123");
		
		nounList.add("Tom");
		nounList.add("Person");
		nounList.add("123");
		
		reNeo4jHandler.setVerbList(verbList);
		reNeo4jHandler.setAdList(adList);
		reNeo4jHandler.setNounList(nounList);
		reNeo4jHandler.initSession(driver);
		reNeo4jHandler.initIdMap();
		reNeo4jHandler.searchAction();
	}
}
