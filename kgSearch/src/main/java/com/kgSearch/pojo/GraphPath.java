package com.kgSearch.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Relationship;


public class GraphPath {
	
	private ArrayList<GraphNode> nodes=new ArrayList<>();
	private ArrayList<GraphRelationship> edges=new ArrayList<>();
	private ArrayList<Long> ids=new ArrayList<>();
	
	public ArrayList<GraphNode> getNodes(){
		return this.nodes;
	}
	
	public ArrayList<GraphRelationship> getEdges(){
		return this.edges;
	}
	
	public boolean setPath(Record record){
		/*
		 * 假设输入的路径就是y 如果出现环则丢弃 返回false
		 * 判断环是否存在 endnodeid不能出现在idset中
		 */
		int size=record.get(1).size();
		Set<Long> nodeId=new HashSet<>();
		
		if(record.size()==2){
			this.setOneSidePath(record);
			return true;
		}
		
		//环的检测
		for(int i=0;i<size;i++){
			long startNode=record.get(1).get(i).asRelationship().startNodeId();
			long endNode=record.get(1).get(i).asRelationship().endNodeId();
			if(startNode==endNode){
				//System.out.println("loopback exists!");
				return false;
			}
			if(nodeId.contains(endNode)){
				//System.out.println("loop exists!");
				return false;
			}
			nodeId.add(startNode);
			nodeId.add(endNode);
		}
		
		//若没有检测到环，则录入数据
		
		/*if(size==1){
			this.setOnePath(record);
			return true;
		}*/
		
		//录入起点
		GraphNode graphNode=new GraphNode(record.get(0).asNode());
		graphNode.setTagTrue();
		nodes.add(graphNode);
		
		//录入边集，顺便录入点id集
		for(int i=0;i<size;i++){
			Relationship relationship=record.get(1).get(i).asRelationship();
			GraphRelationship graphRelationship=new GraphRelationship(relationship);
			edges.add(graphRelationship);
			if(i!=size-1){
				GraphNode graphNode2=new GraphNode();
				graphNode2.setId(relationship.endNodeId());
				nodes.add(graphNode2);
			}
		}
		
		//录入终点
		GraphNode graphNode2=new GraphNode(record.get(2).asNode());
		graphNode2.setTagTrue();
		nodes.add(graphNode2);
		return true;
	}
	
	public boolean setOnePath(Record record){
		/*
		 * 假设输入的路径就是y 如果出现环则丢弃 返回false
		 * 判断环是否存在 endnodeid不能出现在idset中
		 *
		 */
		/*
		int size=record.get(1).size();
		Set<Long> nodeId=new HashSet<>();
		
		//环的检测
		for(int i=0;i<size;i++){
			long startNode=record.get(1).get(i).asRelationship().startNodeId();
			long endNode=record.get(1).get(i).asRelationship().endNodeId();
			if(startNode==endNode){
				//System.out.println("loopback exists!");
				return false;
			}
			if(nodeId.contains(endNode)){
				//System.out.println("loop exists!");
				return false;
			}
			nodeId.add(startNode);
			nodeId.add(endNode);
		}
		
		//若没有检测到环，则录入数据
		
		*/
		
		
		//录入起点
		GraphNode graphNode=new GraphNode(record.get(0).asNode());
		GraphNode graphNode2=new GraphNode(record.get(2).asNode());
		graphNode.setTagTrue();
		graphNode2.setTagTrue();
		
		//录入边集，顺便录入点id集
		Relationship relationship=record.get(1).asRelationship();
		GraphRelationship graphRelationship=new GraphRelationship(relationship);
		edges.add(graphRelationship);
		
		if(graphNode.getId()==relationship.endNodeId()){
			nodes.add(graphNode2);
			nodes.add(graphNode);
		}
		//录入终点
		else {
			nodes.add(graphNode);
			nodes.add(graphNode2);
		}
		
		
		return true;
	}
	
	public boolean setOneSidePath(Record record){
		//由一条边和其所包含的其中之一结点构成
		if(record.size()!=2){
			System.out.println("invalid input format");
			return false;
		}
		
		GraphNode graphNode=new GraphNode(record.get(0).asNode());
		GraphRelationship graphRelationship=new GraphRelationship(record.get(1).asRelationship());
		graphNode.setTagTrue();
		graphRelationship.setTagTrue();
		
		nodes.add(graphNode);
		edges.add(graphRelationship);
		
		
		return true;
	}
	
	public ArrayList<Long> getIds(){
		return this.ids;
	}
	
	public void setIds(){
		int i;
		//System.out.println(edges.size()+" "+nodes.size());
		for(i=0;i<edges.size();i++){
			ids.add(edges.get(i).getId());
		}
		for(i=0;i<edges.size();i++){
			ids.add(nodes.get(i).getId());
		}
	}
		
	public static boolean compare(GraphPath graphPath1,GraphPath graphPath2){
		ArrayList<Long> ids1=graphPath1.getIds();
		ArrayList<Long> ids2=graphPath2.getIds();
		//System.out.println(ids1+"  "+ids2);
		int flag,i;
		if(ids1.size()!=ids2.size()){
			return true;
		}
		for(i=flag=0;i<ids1.size();i++){
			if(ids1.get(i)!=ids2.get(i)){
				flag=1;
				break;
			}
		}
		if(flag==0){
			return false;
		}
		for(i=flag=0;i<ids1.size();i++){
			if(ids1.get(i)!=ids2.get(ids1.size()-i-1)){
				flag=1;
				break;
			}
		}
		if(flag==0){
			return false;
		}
		return true;
	}

	public static boolean contains(GraphPath graphPath1,GraphPath graphPath2){
		//不包含返回true,包含返回false
		if(graphPath1.getIds().size()==graphPath2.getIds().size()){
			return compare(graphPath1, graphPath2);
		}
		ArrayList<Long> longer=(graphPath1.getIds().size()>graphPath2.getIds().size())?graphPath1.getIds():graphPath2.getIds();
		ArrayList<Long> shorter=(graphPath1.getIds().size()>graphPath2.getIds().size())?graphPath2.getIds():graphPath1.getIds();
		ArrayList<Long> arrshorter=shorter;
		Collections.reverse(arrshorter);
		if(longer.toString().indexOf(shorter.toString())!=-1||longer.toString().indexOf(arrshorter.toString())!=-1){
			return false;
		}
		return true;
	}
	
	public void printInf(){
		System.out.print(this.nodes.get(0).getProperties().get("name")+" ");
		for(GraphRelationship graphRelationship:this.edges){
			System.out.print(graphRelationship.getType().getTypeName()+" ");
		}
		if(this.nodes.size()>1){
			System.out.print(this.nodes.get(this.nodes.size()-1).getProperties().get("name"));
		}
		System.out.print("\n");
	}
}
