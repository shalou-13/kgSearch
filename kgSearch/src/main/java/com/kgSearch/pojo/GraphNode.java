package com.kgSearch.pojo;

import java.util.ArrayList;
import java.util.Map;

public class GraphNode {
	/* 为Neo4j图数据库而设计的类型，用于表示其中的节点类型
	 * 节点可以拥有：多个标签和多个属性键值对
	 */
	
	private ArrayList<Integer> Labels;
	private long id;
	private int weight;
	private Map<String,Object> Properties;
	private MatchTags matchTag;
	
	public ArrayList<Integer> getLabels() {
		return Labels;
	}
	public void setLabels(ArrayList<Integer> labels) {
		Labels = labels;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public MatchTags getMatchTag() {
		return matchTag;
	}
	public void setMatchTag(MatchTags matchTag) {
		this.matchTag = matchTag;
	}
	public Map<String, Object> getProperties() {
		return Properties;
	}
	public void setProperties(Map<String, Object> properties) {
		Properties = properties;
	}
	public void setLabelsById(Map<Long,ArrayList<String>> idLabelMap,Map<String,Integer> labelIntMap){
		Labels=new ArrayList<>();
		for(String Alabel:idLabelMap.get(this.id)){
			Labels.add(labelIntMap.get(Alabel));
		}
	}
	
	/*public GraphNode(Node node){
		this.id=node.id();
		this.weight=1;
		for(String iter1:node.labels()){
			GraphNodeLabel temp=new GraphNodeLabel();
			temp.setLabelName(iter1);
			temp.setWeight(1);
			Labels.add(temp);
		}
		for(String iter1:node.keys()){
			Properties.put(iter1,node.get(iter1).asString());
		}
	}*/
	
	
}
