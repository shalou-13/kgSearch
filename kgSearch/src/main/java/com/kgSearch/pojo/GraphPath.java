package com.kgSearch.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	
	public void setPath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		//针对通路(pairpath)设计，不能用于其他地方(会报错)
		//录入起点
		/* record.get(0).size()路径的条数
		 * record.get(0).get(i).asPath().nodes() 第i条路径的路数(type类型的个数)
		 * record.get(0).get(i).asPath().relationships() 第i条路径的第j个三元组
		 */
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		
		for(int i=0;i<record.get(0).size();i++){
			for(Node node:record.get(0).get(i).asPath().nodes()){
				ArrayList<Integer> labels=new ArrayList<>();
				Map<String,Object> properties=new HashMap<>();
				for(String key:node.labels()){
					labels.add(labelIdMap.get(key));
				}
				for(String key:node.keys()){
					properties.put(key,node.get(key));
				}
				GraphNode graphNode=new GraphNode();
				graphNode.setId(node.id());
				graphNode.setLabels(labels);
				graphNode.setProperties(properties);
			}
			for(Relationship relationship:record.get(0).get(i).asPath().relationships()){
				Map<String,Object> properties=new HashMap<>();
				for(String key:relationship.keys()){
					properties.put(key,relationship.get(key));
				}
				GraphRelationship graphRelationship=new GraphRelationship();
				graphRelationship.setId(relationship.id());
				graphRelationship.setType(typeIdMap.get(relationship.type()));
				graphRelationship.setFromNode(relationship.startNodeId());
				graphRelationship.setToNode(relationship.endNodeId());
				graphRelationship.setProperties(properties);
			}
		}
		
	}
	
	public void setRevPath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		
		for(int i=0;i<record.get(0).size();i++){
			ArrayList<Node> revnodes=new ArrayList<>();
			ArrayList<Relationship> revrelationship=new ArrayList<>();
			for(Node node:record.get(0).get(i).asPath().nodes()){
				revnodes.add(0,node);
			}
			for(Relationship relationship:record.get(0).get(i).asPath().relationships()){
				revrelationship.add(0,relationship);
			}
			for(Node node:revnodes){
				ArrayList<Integer> labels=new ArrayList<>();
				Map<String,Object> properties=new HashMap<>();
				for(String key:node.labels()){
					labels.add(labelIdMap.get(key));
				}
				for(String key:node.keys()){
					properties.put(key,node.get(key));
				}
				GraphNode graphNode=new GraphNode();
				graphNode.setId(node.id());
				graphNode.setLabels(labels);
				graphNode.setProperties(properties);
			}
			for(Relationship relationship:revrelationship){
				Map<String,Object> properties=new HashMap<>();
				for(String key:relationship.keys()){
					properties.put(key,relationship.get(key));
				}
				GraphRelationship graphRelationship=new GraphRelationship();
				graphRelationship.setId(relationship.id());
				graphRelationship.setType(typeIdMap.get(relationship.type()));
				graphRelationship.setFromNode(relationship.startNodeId());
				graphRelationship.setToNode(relationship.endNodeId());
				graphRelationship.setProperties(properties);
			}
		}
		
	}
	
	public void setOnePath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		nodes=new ArrayList<>();
		edges=new ArrayList<>();
		//适用于ERPS和EEPS的选举
		Node node1=record.get(0).asNode();
		Node node2=record.get(2).asNode();
		Relationship relationship=record.get(1).asRelationship();
		
		GraphNode graphNode1=new GraphNode();
		ArrayList<Integer> labels=new ArrayList<>();
		Map<String,Object> properties=new HashMap<>();
		for(String Alabel:node1.labels()){
			labels.add(labelIdMap.get(Alabel));
		}
		for(String Aproperty:node1.keys()){
			properties.put(Aproperty,node1.get(Aproperty));
		}
		graphNode1.setLabels(labels);
		graphNode1.setProperties(properties);
		graphNode1.setId(node1.id());
		labels.clear();
		properties.clear();
		
		GraphNode graphNode2=new GraphNode();
		GraphRelationship graphRelationship=new GraphRelationship();
		for(String Alabel:node2.labels()){
			labels.add(labelIdMap.get(Alabel));
		}
		for(String Aproperty:node2.keys()){
			properties.put(Aproperty,node2.get(Aproperty));
		}
		graphNode2.setLabels(labels);
		graphNode2.setProperties(properties);
		graphNode2.setId(node2.id());
		properties.clear();
		
		if(relationship.startNodeId()==node1.id()){
			nodes.add(graphNode1);
			nodes.add(graphNode2);
		}
		else{
			nodes.add(graphNode2);
			nodes.add(graphNode1);
		}
		for(String Aproperty:relationship.keys()){
			properties.put(Aproperty,relationship.get(Aproperty));
		}
		graphRelationship.setId(relationship.id());
		graphRelationship.setType(typeIdMap.get(relationship.type()));
		graphRelationship.setFromNode(relationship.startNodeId());
		graphRelationship.setToNode(relationship.endNodeId());
		graphRelationship.setProperties(properties);
		
		edges.add(graphRelationship);
	}
	public void setTypeAndLabels(Map<Long,ArrayList<String>> idLabelMap,Map<String,Integer> labelIntMap,Map<Long,String> idTypeMap,Map<String,Integer> typeIntMap){
		for(GraphNode g1:nodes){
			g1.setLabelsById(idLabelMap,labelIntMap);
		}
		for(GraphRelationship r1:edges){
			r1.setTypeById(idTypeMap,typeIntMap);
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
