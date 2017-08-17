package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;

import com.kgSearch.method.Neo4jGraphHandler;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.MatchTags;

public class ReNeo4jHandler extends Neo4jGraphHandler{
	

	@Override
	public ArrayList<GraphNode> searchGraphNodeByNoun() {
		ArrayList<GraphNode> NEL = new ArrayList<>();
		ArrayList<GraphNode> NEL1 = searchMovieGraphNodeByNone();
		ArrayList<GraphNode> NEL2 = searchPersonGraphNodeByNone();
		if(NEL1!=null)
			NEL.addAll(NEL1);
		if(NEL2!=null)
			NEL.addAll(NEL2);
		if(NEL.size()!=0)
			return NEL;
		return NEL;
	}
	
	
	public ArrayList<GraphNode> searchMovieGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.title contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.title contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult = this.session.run("match (x:Movie) where "+where+" return x");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				Node node=record.get(0).asNode();
				HashMap<Integer, String> labels=new HashMap<Integer, String>();
				Map<String,Object> properties=node.asMap();
				MatchTags matchTags=new MatchTags();
				for(String Alabel:node.labels()){
					labels.put(this.labelIdMap.get(Alabel), Alabel);
				}
				matchTags.setNounMatch(true);
				for(String Anoun:this.getNounList()){
					if(node.get("title").asString().indexOf(Anoun)>=0)
						matchTags.addNounMatchTag(Anoun);
				}
				GraphNode graphNode=new GraphNode();
				graphNode.setId(node.id());
				graphNode.setWeight(matchTags.getNounMatchList().size());
				graphNode.setLabels(labels);
				graphNode.setMatchTag(matchTags);
				graphNode.setProperties(properties);
				NEL.add(graphNode);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(NEL.size()!=0)
			return NEL;
		return null;
	}
	
	public ArrayList<GraphNode> searchPersonGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.name contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.name contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Person) where "+where+" return x");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				Node node=record.get(0).asNode();
				HashMap<Integer, String> labels=new HashMap<Integer, String>();
				Map<String,Object> properties=node.asMap();
				MatchTags matchTags=new MatchTags();
				for(String Alabel:node.labels()){
					labels.put(this.labelIdMap.get(Alabel), Alabel);
				}
				matchTags.setNounMatch(true);
				for(String Anoun:this.getNounList()){
					if(node.get("name").asString().indexOf(Anoun)>=0)
						matchTags.addNounMatchTag(Anoun);
				}
				GraphNode graphNode=new GraphNode();
				graphNode.setId(node.id());
				graphNode.setWeight(matchTags.getNounMatchList().size());
				graphNode.setLabels(labels);
				graphNode.setMatchTag(matchTags);
				graphNode.setProperties(properties);
				NEL.add(graphNode);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(NEL.size()!=0)
			return NEL;
		return null;
	}

	

	
}
