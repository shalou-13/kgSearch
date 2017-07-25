package com.kgSearch.pojo;

import java.util.Map;

public class GraphNodeLabel {
	
	private int id;
	private String labelName;
	private Map<String,Object> Properties;
	private int weight;
	private MatchTags matchTags;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLabelName(String labelname){
		this.labelName=labelname;
	}
	
	public void setWeight(int weight){
		this.weight=weight;
	}
	
	public String getLabelName(){
		return this.labelName;
	}
	
	public int getWeight(){
		return this.weight;
	}
	
	public Map<String, Object> getProperties() {
		return Properties;
	}

	public void setProperties(Map<String, Object> properties) {
		Properties = properties;
	}

	
	public MatchTags getMatchTags() {
		return matchTags;
	}

	public void setMatchTags(MatchTags matchTags) {
		this.matchTags = matchTags;
	}

	public void printInf(){
		System.out.println("labelname:"+this.labelName+" weight:"+this.weight);
	}
}
