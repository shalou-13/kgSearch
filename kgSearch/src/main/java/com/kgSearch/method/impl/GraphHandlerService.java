package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.From;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.kgSearch.method.GraphHandler;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphRelationship;
import com.kgSearch.pojo.GraphRelationshipType;

public class GraphHandlerService implements GraphHandler{

	private ArrayList<GraphRelationship> relationshipTypeList=new ArrayList<>();
	private ArrayList<GraphNodeLabel> entityLabelList=new ArrayList<>();
	private ArrayList<GraphNode> entityList=new ArrayList<>();
	
	private Set<String> entityKeySet=new HashSet<>();
	
	private Set<GraphPath> entityEntityPathSet=new HashSet<>();
	private Set<GraphPath> entityRelationshipPathSet=new HashSet<>();
	private Set<GraphPath> entityPairPathSet=new HashSet<>();
	private Set<GraphPath> TResult=new HashSet<>();
	
	private Driver driver=GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","huhantao9"));
	private Session session=driver.session();
	
	private ArrayList<String> verbList=new ArrayList<>();
	private ArrayList<String> adList=new ArrayList<>();
	private ArrayList<String> nounList=new ArrayList<>();

	public void doInput(ArrayList<String> verbList,ArrayList<String> adList,ArrayList<String> nounList){
		this.verbList=verbList;
		this.adList=adList;
		this.nounList=nounList;
	}
	
	public void initEntityKeySet(){
		StatementResult statementResult=session.run("match (x) return x");
		while(statementResult.hasNext()==true){
			Record record=statementResult.next();
			Node node=record.get(0).asNode();
			for(String iter1:node.keys()){
				entityKeySet.add(iter1);
			}
		}
	}
	
	@Override
	public void initRelationshipTypeList(){
		/*	1.match type datain
		 * 	2.match keys datain
		 * 	3.merge & cal weight
		 */
		ArrayList<String> mergeList=verbList;
		mergeList.addAll(adList);
		
		for(String iter1:mergeList){
			try{
				StatementResult statementResult=session.run("match ()-[x:"+iter1+"]->() return x");
				while(statementResult.hasNext()==true){
					Record record=statementResult.next();
					Relationship relationship=record.get(0).asRelationship();
					if(searchListById(relationship.id(),0)==false){
						//原表中没有该点，插入该节点
						GraphRelationship mem=new GraphRelationship();
						GraphRelationshipType type=new GraphRelationshipType();
						
						type.setTypeName(relationship.type());
						type.setWeight(1);
						mem.setId(relationship.id());
						mem.setType(type);
						mem.setProperties(relationship);
						relationshipTypeList.add(mem);
					}
					else{
						//原表中有该节点 增加其type的权值
						for(int i=0;i<relationshipTypeList.size();i++){
							if(relationshipTypeList.get(i).getId()==relationship.id()){
								relationshipTypeList.get(i).increaseWeight(1);
								break;
							}
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			try{
				StatementResult statementResult=session.run("match ()-[x]->() return x");
				while(statementResult.hasNext()==true){
					Record record=statementResult.next();
					Relationship relationship=record.get(0).asRelationship();
					
					for(String iter2:relationship.keys()){
						//如果有关系匹配的话
						if(iter1.equals(iter2)){
							if(searchListById(relationship.id(),0)==false){
								//原表中没有该点，插入该节点
								GraphRelationship mem=new GraphRelationship();
								GraphRelationshipType type=new GraphRelationshipType();
								
								type.setTypeName(relationship.type());
								type.setWeight(1);
								mem.setId(relationship.id());
								mem.setType(type);
								mem.setProperties(relationship);
								relationshipTypeList.add(mem);
							}
							else{
								//原表中有该节点 增加其type的权值
								for(int i=0;i<relationshipTypeList.size();i++){
									if(relationshipTypeList.get(i).getId()==relationship.id()){
										relationshipTypeList.get(i).increaseWeight(1);
										break;
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		System.out.println("initRelationshipTypeList:");
		for(GraphRelationship iter1:relationshipTypeList){
			iter1.printInf();
		}
	}

	private boolean searchListById(Long id,int state){
		switch(state){
		case 0:
			for(GraphRelationship iter:relationshipTypeList){
				if(iter.getId()==id){
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public void initEntityLabelList(){
		ArrayList<String> mergeList=adList;
		mergeList.addAll(nounList);
		
		for(String iter1:mergeList){
			StatementResult statementResult=session.run("match (x:"+iter1+") return x");
			int counter=0;
			while(statementResult.hasNext()==true){
				@SuppressWarnings("unused")
				Record consume=statementResult.next();
				counter++;
			}
			if(counter!=0){
				int flag=0;
				for(int i=0;i<entityLabelList.size();i++){
					if(entityLabelList.get(i).getLabelName().equals(iter1)){
						entityLabelList.get(i).setWeight(entityLabelList.get(i).getWeight()+counter);
						flag=1;
						break;
					}
				}
				if(flag==0){
					GraphNodeLabel temp=new GraphNodeLabel();
					temp.setLabelName(iter1);
					temp.setWeight(counter);
					entityLabelList.add(temp);
				}
			}
		}
		System.out.println("\ninitEntityLabelList:");
		for(GraphNodeLabel graphNodeLabel:entityLabelList){
			graphNodeLabel.printInf();
		}
	}

	@Override
	public void initEntityList(){
		ArrayList<String> mergeList=adList;
		mergeList.addAll(nounList);
		for(String iter1:mergeList){
			String temp="";
			boolean flag=false;
			for(String iter2:entityKeySet){
				if(flag==true){
					temp+=" or x."+iter2+"='"+iter1+"'";
				}
				else{
					temp+=" x."+iter2+"='"+iter1+"'";
					flag=true;
				}
			}
			try{
				//System.out.println("match (x) where"+temp+" return x");
				StatementResult statementResult=session.run("match (x) where"+temp+" return x");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					Node node=record.get(0).asNode();
					GraphNode graphNode=new GraphNode(node);
					flag=false;
					for(GraphNode iter2:entityList){
						if(iter2.getId()==graphNode.getId()){
							iter2.setWeight(iter2.getWeight()+1);
							flag=true;
							break;
						}
					}
					if(flag==false){
						entityList.add(graphNode);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.println("\ninitEntityList:");
		for(GraphNode graphNode:entityList){
			graphNode.printInf();
		}
	}
	
	@Override
	public void adjustEntityLabelList(){
		//遍历ELL中的每一个标签 在entitylist的所有节点中查找这些标签
		//如果查找的话 就将其权值加上去
		//从ELL中删除这个标签
		for(int i=0;i<entityLabelList.size();i++){
			int weight=entityLabelList.get(i).getWeight();
			String labelName=entityLabelList.get(i).getLabelName();
			boolean flag=false;
			for(GraphNode iter1:entityList){
				for(int j=0;j<iter1.getLabels().size();j++){
					if(iter1.getLabels().get(j).getLabelName().equals(labelName)){
						iter1.getLabels().get(j).setWeight(iter1.getLabels().get(j).getWeight()+weight);
						flag=true;
						break;
					}
				}
			}
			if(flag==true){
				entityLabelList.remove(i);
				i--;
			}
		}
		System.out.println("\nadjustEntityLabelList:");
		for(GraphNodeLabel graphNodeLabel:entityLabelList){
			graphNodeLabel.printInf();
		}
	}

	@Override
	public void initEntityPairsPathSet(){
		for(int i=0;i<entityList.size()-1;i++){
			String label1="";
			String label2="";
			String where1="";
			String where2="";
			String where3="";
			String where4="";
			boolean flag=false;
			
			GraphNode graphNode1=entityList.get(i);
			
			for(GraphNodeLabel iter1:graphNode1.getLabels()){
				label1+=":"+iter1.getLabelName();
			}
			for(String iter1:graphNode1.getProperties().keySet()){
				if(flag==true){
					where1+=" and x."+iter1+"='"+graphNode1.getProperties().get(iter1)+"'";
					where3+=" and z."+iter1+"='"+graphNode1.getProperties().get(iter1)+"'";
				}
				else{
					flag=true;
					where1+=" x."+iter1+"='"+graphNode1.getProperties().get(iter1)+"'";
					where3+=" z."+iter1+"='"+graphNode1.getProperties().get(iter1)+"'";
				}
			}
			for(int j=i+1;j<entityList.size();j++){
				GraphNode graphNode2=entityList.get(j);
				
				for(GraphNodeLabel iter1:graphNode2.getLabels()){
					label2+=":"+iter1.getLabelName();
				}
				for(String iter1:graphNode2.getProperties().keySet()){
					where2+=" and z."+iter1+"='"+graphNode2.getProperties().get(iter1)+"'";
					where4+=" and x."+iter1+"='"+graphNode2.getProperties().get(iter1)+"'";
				}
				System.out.println("查询语句为："+"match (x"+label1+")-[y*]->(z"+label2+") where "+where1+where2+" return distinct x,y,z");
			
				StatementResult statementResult=session.run("match (x"+label1+")-[y*]->(z"+label2+") where "+where1+where2+"return distinct x,y,z");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					GraphPath graphPath=new GraphPath();
					if(graphPath.setPath(record)==true){
						entityPairPathSet.add(graphPath);
					}
				}
				System.out.println("查询语句为："+"match (x"+label2+")-[y*]->(z"+label1+") where "+where3+where4+"return distinct x,y,z");
				statementResult=session.run("match (x"+label2+")-[y*]->(z"+label1+") where "+where3+where4+"return distinct x,y,z");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					GraphPath graphPath=new GraphPath();
					if(graphPath.setPath(record)==true){
						entityPairPathSet.add(graphPath);
					}
				}
			}
		}
		System.out.println("initEntityPairPathSet:");
		for(GraphPath graphPath:entityPairPathSet){
			graphPath.printInf();
		}
	}

	@Override
	public void initEntityEntityPathSet(){
		//生成查询语句
		//遍历EL
		for(GraphNode iter1:entityList){
			boolean flag=false;
			String label1="";
			String where="";
			String label2="";
			for(GraphNodeLabel iter2:iter1.getLabels()){
				label1+=":"+iter2.getLabelName();
			}
			for(String iter2:iter1.getProperties().keySet()){
				if(flag==true){
					where+=" and x."+iter2+"='"+iter1.getProperties().get(iter2)+"'";
				}
				else{
					flag=true;
					where+=" x."+iter2+"='"+iter1.getProperties().get(iter2)+"'";
				}
			}
			for(GraphNodeLabel iter2:entityLabelList){
				//遍历ELL
				label2+=":"+iter2.getLabelName();
				System.out.println("查询语句为"+"match(x"+label1+")-[y]-(z"+label2+") where "+where+" return distinct x,y,z");
				try{
					StatementResult statementResult=session.run("match(x"+label1+")-[y]-(z"+label2+") where "+where+" return distinct x,y,z");
					while(statementResult.hasNext()){
						GraphPath graphPath=new GraphPath();
						Record record=statementResult.next();
						if(graphPath.setOnePath(record)==true){
							entityEntityPathSet.add(graphPath);
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		System.out.println("initEntityEntityPathSet:");
		for(GraphPath graphPath:entityEntityPathSet){
			graphPath.printInf();
		}
	}

	@Override
	public void initEntityRelationshipSet(){	
		for(GraphNode iter1:entityList){
			String label1="";
			String label2="";
			String where1="";
			String where2="";
			boolean flag=false;
			for(GraphNodeLabel iter2:iter1.getLabels()){
				label1+=":"+iter2.getLabelName();
			}
			for(String iter2:iter1.getProperties().keySet()){
				if(flag==true){
					where1+=" and x."+iter2+"='"+iter1.getProperties().get(iter2)+"'";
				}
				else{
					where1+=" x."+iter2+"='"+iter1.getProperties().get(iter2)+"'";
					flag=true;
				}
			}
			for(GraphRelationship iter2:relationshipTypeList){
				where2="";
				label2=":"+iter2.getType().getTypeName();
				for(String iter3:iter2.getProperties().keySet()){
					where2+=" and y."+iter3+"='"+iter2.getProperties().get(iter3)+"'";
				}
				System.out.println("查询语句为："+"match (x"+label1+")-[y"+label2+"]-(z) where "+where1+where2+" return distinct x,y,z");
				try{
					StatementResult statementResult=session.run("match (x"+label1+")-[y"+label2+"]-(z) where "+where1+where2+" return distinct x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						if(graphPath.setOnePath(record)==true){
							entityRelationshipPathSet.add(graphPath);
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		System.out.println("initEntityRelationshipPathSet:");
		for(GraphPath graphPath:entityRelationshipPathSet){
			graphPath.printInf();
		}
	}

	@Override
	public void getTResult() {
		//可以加一步 合并父子集的步骤以简化后续
		int flag=0;
		for(GraphPath iter1:entityEntityPathSet){
			flag=0;
			iter1.setIds();
			for(GraphPath iter2:TResult){
				if(GraphPath.compare(iter1,iter2)==false){
					flag=1;
					break;
				}
			}
			if(flag==0){
				TResult.add(iter1);
			}
		}
		
		for(GraphPath iter1:entityPairPathSet){
			flag=0;
			iter1.setIds();
			for(GraphPath iter2:TResult){
				if(GraphPath.compare(iter1,iter2)==false){
					flag=1;
					break;
				}
			}
			if(flag==0){
				TResult.add(iter1);
			}
		}
		for(GraphPath iter1:entityRelationshipPathSet){
			flag=0;
			//System.out.println(++counter);
			iter1.setIds();
			for(GraphPath iter2:TResult){
				if(GraphPath.compare(iter1,iter2)==false){
					flag=1;
					break;
				}
			}
			if(flag==0){
				TResult.add(iter1);
			}
		}
		
		System.out.println("getTResult:");
		for(GraphPath iter1:TResult){
			iter1.printInf();
		}
	}

	
	public static void main(String args[]){
		GraphHandlerService graphHandlerService=new GraphHandlerService();
		graphHandlerService.initRelationshipTypeList();
		
		ArrayList<String> vList=new ArrayList<>();
		ArrayList<String> aList=new ArrayList<>();
		ArrayList<String> nList=new ArrayList<>();
		
		vList.add("R1");
		vList.add("R2");
		aList.add("extend");
		aList.add("length");
		nList.add("L1");
		nList.add("L2");
		nList.add("C");
		nList.add("E");
		
		graphHandlerService.doInput(vList,aList,nList);
		graphHandlerService.initEntityKeySet();
		graphHandlerService.initRelationshipTypeList();
		graphHandlerService.initEntityLabelList();
		graphHandlerService.initEntityList();
		graphHandlerService.adjustEntityLabelList();
		graphHandlerService.initEntityPairsPathSet();
		graphHandlerService.initEntityRelationshipSet();
		graphHandlerService.initEntityEntityPathSet();
		graphHandlerService.getTResult();
	}

	@Override
	public void adjustRelationshipTypeList() {
		/*
		 * 筛选在RTL中但不在TResult中的关系
		 * 为权值保留留下空间
		 */
		for(int i=0;i<relationshipTypeList.size();i++){
			GraphRelationship graphRelationship=relationshipTypeList.get(i);
			for(GraphPath iter1:TResult){
				ArrayList<Long> bunch=iter1.getIds();
				if(bunch.contains(graphRelationship.getId())==true){
					System.out.println("delete from RTL:"+relationshipTypeList.get(i).getProperties().get("name"));
					relationshipTypeList.remove(i--);
					break;
				}
			}
		}
		System.out.println("adjustRelationshipTypeList:");
		for(GraphRelationship iter1:relationshipTypeList){
			iter1.printInf();
		}
	}

	
	@Override
	public void adjustEntityList() {
		for(int i=0;i<entityList.size();i++){
			GraphNode graphNode=entityList.get(i);
			for(GraphPath iter1:TResult){
				ArrayList<Long> bunch=iter1.getIds();
				if(bunch.contains(graphNode.getId())==true){
					System.out.println("delete from EL:"+entityList.get(i).getProperties().get("name"));
					entityList.remove(i--);
					break;
				}
			}
		}
		System.out.println("\nadjustEntityList:");
		for(GraphNode graphNode:entityList){
			graphNode.printInf();
		}
	}

	
	@Override
	public void initNewEntityLabelList() {
		/*
		 * 筛选在ELL中不在TResult中entitylabel
		 */
		for(int i=0;i<entityLabelList.size();i++){
			
		}
	}

	

}
