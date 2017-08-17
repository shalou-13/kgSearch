package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.springframework.stereotype.Service;

import com.kgSearch.method.Neo4jGraphHandler;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.MatchTags;

@Service
public class CustomerNeo4jHandler extends Neo4jGraphHandler {

	@Override
	public ArrayList<GraphNode> searchGraphNodeByNoun() {
		ArrayList<GraphNode> NEL = new ArrayList<>();
		ArrayList<GraphNode> NEL1 = searchCategoryGraphNodeByNone();
		ArrayList<GraphNode> NEL2 = searchCustomerGraphNodeByNone();
		ArrayList<GraphNode> NEL3 = searchProductGraphNodeByNone();
		ArrayList<GraphNode> NEL4 = searchOrderGraphNodeByNone();
		ArrayList<GraphNode> NEL5 = searchSupplierGraphNodeByNone();
		if(NEL1!=null)
			NEL.addAll(NEL1);
		if(NEL2!=null)
			NEL.addAll(NEL2);
		if(NEL3!=null)
			NEL.addAll(NEL3);
		if(NEL4!=null)
			NEL.addAll(NEL4);
		if(NEL5!=null)
			NEL.addAll(NEL5);
		if(NEL.size()!=0)
			return NEL;
		return NEL;
	}
	
	public ArrayList<GraphNode> searchCategoryGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.categoryName contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.categoryName contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Category) where "+where+" return x");
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
					if(node.get("categoryName").asString().equals(Anoun))
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
	
	public ArrayList<GraphNode> searchCustomerGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.customerID contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.customerID contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Customer) where "+where+" return x");
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
					if(node.get("customerID").asString().equals(Anoun))
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
	
	public ArrayList<GraphNode> searchProductGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.productName contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.productName contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Product) where "+where+" return x");
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
					if(node.get("productName").asString().indexOf(Anoun)>=0)
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
	
	public ArrayList<GraphNode> searchOrderGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.customerID contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.customerID contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Order) where "+where+" return x");
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
					if(node.get("customerID").asString().equals(Anoun))
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
	
	public ArrayList<GraphNode> searchSupplierGraphNodeByNone(){
		ArrayList<GraphNode> NEL=new ArrayList<>();
		boolean flag=false;
		String where="";
		for(String Anoun:this.getNounList()){
			if(flag){
				where+=" or x.companyName contains "+"'"+Anoun+"'";
			}
			else{
				where+=" x.companyName contains "+"'"+Anoun+"'";
				flag=true;
			}
		}
		try{
			StatementResult statementResult=this.session.run("match (x:Supplier) where "+where+" return x");
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
					if(node.get("companyName").asString().indexOf(Anoun)>=0)
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
