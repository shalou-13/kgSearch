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
	
	public void setPath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		//针对通路(pairpath)设计，不能用于其他地方(会报错)
		//录入起点
		/* record.get(0).size()路径的条数
		 * record.get(0).get(i).asPath().nodes() 第i条路径的路数(type类型的个数)
		 * record.get(0).get(i).asPath().relationships() 第i条路径的第j个三元组
		 */
		this.nodes=new ArrayList<>();
		this.edges=new ArrayList<>();
		for(Node node:record.get("p").asPath().nodes()){
			ArrayList<Integer> labels=new ArrayList<>();
			Map<String,Object> properties=node.asMap();
			for(String key:node.labels()){
				labels.add(labelIdMap.get(key));
			}
			GraphNode graphNode=new GraphNode();
			graphNode.setId(node.id());
			graphNode.setLabels(labels);
			graphNode.setProperties(properties);
			this.nodes.add(graphNode);
		}
		for(Relationship relationship:record.get("p").asPath().relationships()){
			Map<String,Object> properties=relationship.asMap();
			GraphRelationship graphRelationship=new GraphRelationship();
			graphRelationship.setId(relationship.id());
			graphRelationship.setType(typeIdMap.get(relationship.type()));
			graphRelationship.setFromNode(relationship.startNodeId());
			graphRelationship.setToNode(relationship.endNodeId());
			graphRelationship.setProperties(properties);
			this.edges.add(graphRelationship);
		}
	}
	
	public void setRevPath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		this.nodes=new ArrayList<>();
		this.edges=new ArrayList<>();
		ArrayList<Node> revnodes=new ArrayList<>();
		ArrayList<Relationship> revrelationship=new ArrayList<>();
		for(Node node:record.get("p").asPath().nodes()){
			revnodes.add(node);
		}
		for(Relationship relationship:record.get("p").asPath().relationships()){
			revrelationship.add(relationship);
		}
		for(Node node:revnodes){
			ArrayList<Integer> labels=new ArrayList<>();
			Map<String,Object> properties=node.asMap();
			for(String key:node.labels()){
				labels.add(labelIdMap.get(key));
			}
			GraphNode graphNode=new GraphNode();
			graphNode.setId(node.id());
			graphNode.setLabels(labels);
			graphNode.setProperties(properties);
			this.nodes.add(graphNode);
		}
		for(Relationship relationship:revrelationship){
			Map<String,Object> properties=relationship.asMap();
			GraphRelationship graphRelationship=new GraphRelationship();
			graphRelationship.setId(relationship.id());
			graphRelationship.setType(typeIdMap.get(relationship.type()));
			graphRelationship.setFromNode(relationship.startNodeId());
			graphRelationship.setToNode(relationship.endNodeId());
			graphRelationship.setProperties(properties);
			this.edges.add(graphRelationship);
		}
		
	}
	
	public void setOnePath(Record record,Map<String,Integer> labelIdMap,Map<String,Integer> typeIdMap){
		this.nodes=new ArrayList<>();
		this.edges=new ArrayList<>();
		//适用于ERPS和EEPS的选举
		Node node1=record.get(0).asNode();
		Node node2=record.get(2).asNode();
		Relationship relationship=record.get(1).asRelationship();
		
		GraphNode graphNode1=new GraphNode();
		ArrayList<Integer> labels1=new ArrayList<Integer>();
		Map<String,Object> properties1=node1.asMap();
		for(String Alabel:node1.labels()){
			labels1.add(labelIdMap.get(Alabel));
		}
		
		graphNode1.setLabels(labels1);
		graphNode1.setProperties(properties1);
		graphNode1.setId(node1.id());
		
		ArrayList<Integer> labels2=new ArrayList<Integer>();
		Map<String,Object> properties2=node2.asMap();
		GraphNode graphNode2=new GraphNode();
		GraphRelationship graphRelationship=new GraphRelationship();
		for(String Alabel:node2.labels()){
			labels2.add(labelIdMap.get(Alabel));
		}
		graphNode2.setLabels(labels2);
		graphNode2.setProperties(properties2);
		graphNode2.setId(node2.id());
		
		Map<String,Object> properties3=relationship.asMap();
		if(relationship.startNodeId()==node1.id()){
			this.nodes.add(graphNode1);
			this.nodes.add(graphNode2);
		}
		else{
			this.nodes.add(graphNode2);
			this.nodes.add(graphNode1);
		}
		graphRelationship.setId(relationship.id());
		graphRelationship.setType(typeIdMap.get(relationship.type()));
		graphRelationship.setFromNode(relationship.startNodeId());
		graphRelationship.setToNode(relationship.endNodeId());
		graphRelationship.setProperties(properties3);
		
		this.edges.add(graphRelationship);
	}
	public void setTypeAndLabels(Map<Long,ArrayList<String>> idLabelMap,Map<String,Integer> labelIntMap,Map<Long,String> idTypeMap,Map<String,Integer> typeIntMap){
		for(GraphNode g1:this.nodes){
			g1.setLabelsById(idLabelMap,labelIntMap);
		}
		for(GraphRelationship r1:this.edges){
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
	
		
	public void setNodes(ArrayList<GraphNode> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(ArrayList<GraphRelationship> edges) {
		this.edges = edges;
	}

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
