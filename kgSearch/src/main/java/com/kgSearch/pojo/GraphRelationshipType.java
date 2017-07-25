package com.kgSearch.pojo;

import java.util.Map;

public class GraphRelationshipType {
	
	private int id;
	private String typeName;
	private Map<String,Object> Properties;
	private int weight;
	private MatchTags matchTags;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public MatchTags getMatchTags() {
		return matchTags;
	}

	public void setMatchTags(MatchTags matchTags) {
		this.matchTags = matchTags;
	}

	public Map<String, Object> getProperties() {
		return Properties;
	}

	public void setProperties(Map<String, Object> properties) {
		Properties = properties;
	}
	
}
