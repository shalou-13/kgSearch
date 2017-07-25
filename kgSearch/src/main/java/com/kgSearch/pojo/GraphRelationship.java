package com.kgSearch.pojo;

import java.util.Map;

public class GraphRelationship {
	private long id;
	private int Type;
	private long fromNode;
	private long toNode;
	private Map<String,Object> Properties;
	private MatchTags matchTags;
	private int weight;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	
	public long getFromNode() {
		return fromNode;
	}
	public void setFromNode(long fromNode) {
		this.fromNode = fromNode;
	}
	public long getToNode() {
		return toNode;
	}
	public void setToNode(long toNode) {
		this.toNode = toNode;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
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
	
	/*public GraphRelationship(Relationship relationship){
		this.id=relationship.id();
		this.Type.setTypeName(relationship.type());
		this.Type.setWeight(1);
		this.fromNode.setId(relationship.startNodeId());
		this.toNode.setId(relationship.endNodeId());
		for(String iter1:relationship.keys()){
			Properties.put(iter1,relationship.get(iter1).asString());
		}
	}*/
	
	
}
