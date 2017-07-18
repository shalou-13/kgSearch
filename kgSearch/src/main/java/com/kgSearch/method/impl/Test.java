package com.kgSearch.method.impl;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;


public class Test {
	private Driver driver=GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","huhantao9"));
	private Session session=driver.session();
	
	@org.junit.Test
	public void t1(){
		StatementResult statementResult=session.run("match (x:L6)-[y*]->(z:L7) return distinct x,y,z");
		while(statementResult.hasNext()){
			Record record=statementResult.next();
			System.out.println(record.get(1).get(record.get(1).size()-1).asRelationship().startNodeId());
		}
	}
}
