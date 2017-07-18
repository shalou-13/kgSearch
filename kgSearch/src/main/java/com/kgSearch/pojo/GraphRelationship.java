package com.kgSearch.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Null;

import org.neo4j.driver.v1.types.Relationship;

public class GraphRelationship {
	private long id;
	private GraphRelationshipType Type=new GraphRelationshipType();
	private GraphNode fromNode=new GraphNode();
	private GraphNode toNode=new GraphNode();
	private Map<String,String> Properties=new HashMap<>();
	
	public GraphRelationship(){
		
	}
	
	public GraphRelationship(Relationship relationship){
		this.id=relationship.id();
		this.Type.setTypeName(relationship.type());
		this.Type.setWeight(1);
		this.fromNode.setId(relationship.startNodeId());
		this.toNode.setId(relationship.endNodeId());
		for(String iter1:relationship.keys()){
			Properties.put(iter1,relationship.get(iter1).asString());
		}
	}
	
	public void setId(Long id){
		this.id=id;
	}
	
	public void setType(GraphRelationshipType Type){
		this.Type=Type;
	}
	
	public void setType(Relationship relationship){
		GraphRelationshipType graphRelationshipType=new GraphRelationshipType();
		graphRelationshipType.setTypeName(relationship.type());
		graphRelationshipType.setWeight(1);
		this.Type=graphRelationshipType;
	}
	
	public void setFromNode(GraphNode node){
		this.fromNode=node;
	}
	
	public void setToNode(GraphNode node){
		this.toNode=node;
	}
	
	public void setProperties(Map<String,String> properties){
		this.Properties=properties;
	}
	
	public void setProperties(Relationship relationship){
		for(String iter1:relationship.keys()){
			String temp=relationship.get(iter1).asString();
			this.Properties.put(iter1,temp);
		}
	}
	
	public void increaseWeight(int increment){
		this.Type.setWeight(this.Type.getWeight()+increment);
	}
	
	public long getId(){
		return this.id;
	}
	
	public GraphRelationshipType getType(){
		return this.Type;
	}
	
	public GraphNode getFromNode(){
		return this.fromNode;
	}
	
	public GraphNode getToNode(){
		return this.toNode;
	}
	
	public Map<String,String> getProperties(){
		return this.Properties;
	}
	
	public void printInf(){
		System.out.print("id:"+this.id+" type:"+this.Type.getTypeName()+" weight:"+this.Type.getWeight());
		if(this.fromNode!=null)		System.out.print(" fromNodeId:"+this.fromNode.getId());
		if(this.toNode!=null)		System.out.print(" toNodeId:"+this.toNode.getId());
		if(this.Properties.containsKey("name"))		System.out.print(" name:"+this.Properties.get("name"));
		System.out.print("\n");
	}
}
