package com.kgSearch.pojo;

public class GraphPathPattern {
	
	private GraphNodeLabel label;
	private GraphRelationshipType relationType;
	// direction 0:from label to relationType; 1: from relationType to label
	private int direction;
	
	
	public GraphNodeLabel getLabel() {
		return label;
	}
	public void setLabel(GraphNodeLabel label) {
		this.label = label;
	}
	public GraphRelationshipType getRelationType() {
		return relationType;
	}
	public void setRelationType(GraphRelationshipType relationType) {
		this.relationType = relationType;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	

}
