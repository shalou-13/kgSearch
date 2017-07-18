package com.kgSearch.pojo;


public class GraphNodeLabel {
	
	private String labelName;
	private int weight=0;
	
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
	
	public void printInf(){
		System.out.println("labelname:"+this.labelName+" weight:"+this.weight);
	}
}
