package com.kgSearch.pojo;

public class GraphRelationshipType {
	
	private String typeName;
	private int weight=0;
	
	public void setTypeName(String typename){
		this.typeName=typename;
	}
	
	public void setWeight(int weight){
		this.weight=weight;
	}
	
	public String getTypeName(){
		return this.typeName;
	}
	
	public int getWeight(){
		return this.weight;
	}
}
