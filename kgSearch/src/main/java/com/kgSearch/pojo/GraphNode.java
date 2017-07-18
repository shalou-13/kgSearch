package com.kgSearch.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.types.Node;

public class GraphNode {
	/* 为Neo4j图数据库而设计的类型，用于表示其中的节点类型
	 * 节点可以拥有：多个标签和多个属性键值对
	 */
	
	private ArrayList<GraphNodeLabel> Labels=new ArrayList<>();
	private Map<String,String> Properties=new HashMap<>();
	private long id;
	private int weight;
	
	public GraphNode(){
		
	}
	
	public GraphNode(Node node){
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
	}
	
	public void setId(Long id){
		this.id=id;
	}
	
	public void setWeight(int weight){
		this.weight=weight;
	}
	
	public void setLabels(ArrayList<GraphNodeLabel> labels){
		this.Labels=labels;
	}
	
	public void setProperties(Map<String,String> properties){
		this.Properties=properties;
	}
	
	
	public long getId(){
		return this.id;
	}

	public int getWeight(){
		return this.weight;
	}
	
	public ArrayList<GraphNodeLabel> getLabels(){
		return this.Labels;
	}
	
	public Map<String,String> getProperties(){
		return this.Properties;
	}
	
	public void printInf(){
		System.out.print("id:"+this.id+" weight:"+this.weight+" properties:"+this.Properties);
		System.out.print(" labels:[");
		for(GraphNodeLabel graphNodeLabel:this.Labels){
			System.out.print(graphNodeLabel.getLabelName()+" ");
		}
		System.out.print("]");
		System.out.print("\n");
	}
}
