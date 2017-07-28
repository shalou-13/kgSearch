package com.kgSearch.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;


public class GraphPath {
	
	private ArrayList<GraphNode> nodes;
	private ArrayList<GraphRelationship> edges;
	
	public ArrayList<GraphNode> getNodes(){
		return this.nodes;
	}
	
	public ArrayList<GraphRelationship> getEdges(){
		return this.edges;
	}
	
	public void setPath(Record record){
		//针对通路(pairpath)设计，不能用于其他地方(会报错)
		//录入起点
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		Node node=record.get(0).asNode();
		GraphNode graphNode1=new GraphNode();
		graphNode1.setId(node.id());
		nodes.add(graphNode1);
		
		//录入边集，顺便录入点id集
		for(int i=0;i<record.get(1).size();i++){
			Relationship relationship=record.get(1).get(i).asRelationship();
			GraphRelationship graphRelationship=new GraphRelationship();
			graphRelationship.setId(relationship.id());
			edges.add(graphRelationship);
			if(i!=record.get(1).size()-1){
				GraphNode graphNode2=new GraphNode();
				graphNode2.setId(relationship.endNodeId());
				nodes.add(graphNode2);
			}
		}
		
		//录入终点
		node=record.get(2).asNode();
		GraphNode graphNode2=new GraphNode();
		graphNode2.setId(node.id());
		nodes.add(graphNode2);
	}
	
	public void setRevPath(Record record){
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		
		Node node=record.get(2).asNode();
		GraphNode graphNode1=new GraphNode();
		graphNode1.setId(node.id());
		nodes.add(graphNode1);
		
		//录入边集，顺便录入点id集
		for(int i=record.get(1).size()-1;i>=0;i--){
			Relationship relationship=record.get(1).get(i).asRelationship();
			GraphRelationship graphRelationship=new GraphRelationship();
			graphRelationship.setId(relationship.id());
			edges.add(graphRelationship);
			if(i!=0){
				GraphNode graphNode2=new GraphNode();
				graphNode2.setId(relationship.endNodeId());
				nodes.add(graphNode2);
			}
		}
		
		//录入终点
		node=record.get(0).asNode();
		GraphNode graphNode2=new GraphNode();
		graphNode2.setId(node.id());
		nodes.add(graphNode2);
		
	}
	
	public void setOnePath(Record record){
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		//适用于ERPS和EEPS的选举
		Node node1=record.get(0).asNode();
		Node node2=record.get(2).asNode();
		Relationship relationship=record.get(1).asRelationship();
		
		GraphNode graphNode1=new GraphNode();
		GraphNode graphNode2=new GraphNode();
		GraphRelationship graphRelationship=new GraphRelationship();
		graphNode1.setId(node1.id());
		graphNode2.setId(node2.id());
		
		if(relationship.startNodeId()==node1.id()){
			nodes.add(graphNode1);
			nodes.add(graphNode2);
		}
		else{
			nodes.add(graphNode2);
			nodes.add(graphNode1);
		}
		graphRelationship.setId(relationship.id());
		
		
		edges.add(graphRelationship);
	}
	public void setTypeAndLabels(Map<Long,ArrayList<String>> idLabelMap,Map<String,Integer> labelIntMap,Map<Long,String> idTypeMap,Map<String,Integer> typeIntMap){
		for(GraphNode g1:nodes){
			g1.setLabelsById(idLabelMap, labelIntMap);
		}
		for(GraphRelationship r1:edges){
			r1.setTypeById(idTypeMap, typeIntMap);
		}
	}
	
	/*public boolean setPath(Record record){
		
		 * 假设输入的路径就是y 如果出现环则丢弃 返回false
		 * 判断环是否存在 endnodeid不能出现在idset中
		 
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
		
		if(size==1){
			this.setOnePath(record);
			return true;
		}
		
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
		
		 * 假设输入的路径就是y 如果出现环则丢弃 返回false
		 * 判断环是否存在 endnodeid不能出现在idset中
		 *
		 
		
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
	}*/
	
		
	public boolean compare(GraphPath graphPath){
		long[] nodeIDs1 = new long[this.nodes.size()];
		long[] edgeIDs1 = new long[this.edges.size()];
		long[] nodeIDs2 = new long[graphPath.nodes.size()];
		long[] edgeIDs2 = new long[graphPath.edges.size()];
		for(int i = 0;i<this.nodes.size();i++){
			nodeIDs1[i] = this.nodes.get(i).getId();
		}
		for(int j = 0;j<this.edges.size();j++){
			edgeIDs1[j] = this.edges.get(j).getId();
		}
		for(int m = 0;m<graphPath.nodes.size();m++){
			nodeIDs2[m] = graphPath.nodes.get(m).getId();
		}
		for(int n = 0;n<graphPath.edges.size();n++){
			edgeIDs2[n] = graphPath.edges.get(n).getId();
		}
		if(Arrays.equals(nodeIDs1, nodeIDs2) && Arrays.equals(edgeIDs1, edgeIDs2))
			return true;
		return false;
	}

	public boolean contains(GraphPath graphPath){
		if((this.nodes.size()>graphPath.nodes.size())){
			long[] nodeIDs1 = new long[this.nodes.size()];
			long[] edgeIDs1 = new long[this.edges.size()];
			for(int i = 0;i<this.nodes.size();i++){
				nodeIDs1[i] = this.nodes.get(i).getId();
			}
			for(int j = 0;j<this.edges.size();j++){
				edgeIDs1[j] = this.edges.get(j).getId();
			}
			boolean hasNode = true;
			boolean hasRelation = true;
			for(int m = 0;m<graphPath.nodes.size();m++){
				if(!Arrays.asList(nodeIDs1).contains(graphPath.nodes.get(m).getId())){
					hasNode = false;
					break;
				}
			}
			if(hasNode){
				for(int n = 0;n<graphPath.edges.size();n++){
					if(!Arrays.asList(edgeIDs1).contains(graphPath.edges.get(n).getId())){
						hasRelation = false;
						break;
					}
				}
			}
			if(hasNode && hasRelation)
				return true;
			return false;
		}
		return false;
	}
	
	public void printInf(){
		System.out.print("node:");
		for(int i=0;i<nodes.size();i++){
			System.out.print(nodes.get(i).getId()+" ");
		}
		System.out.print("\n");
	}
}
