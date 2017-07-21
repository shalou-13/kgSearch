package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.New;

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
	private ArrayList<GraphNodeLabel> advancedEntityLabelList=new ArrayList<>();
	private ArrayList<GraphNode> extraEntityList=new ArrayList<>();
	
	private Set<String> entityKeySet=new HashSet<>();
	
	private Set<GraphPath> entityEntityPathSet=new HashSet<>();
	private Set<GraphPath> entityRelationshipPathSet=new HashSet<>();
	private Set<GraphPath> entityPairPathSet=new HashSet<>();
	private Set<GraphPath> TResult=new HashSet<>();
	private Set<GraphPath> SResult=new HashSet<>();
	
	private Driver driver=GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","huhantao9"));
	private Session session=driver.session();
	
	private ArrayList<String> verbList=new ArrayList<>();
	private ArrayList<String> adList=new ArrayList<>();
	private ArrayList<String> nounList=new ArrayList<>();

	public boolean judge1(){
		//EL RTL ELL不同时为空
		return !(entityList.size()==0&&entityLabelList.size()==0&&relationshipTypeList.size()==0);
	}
	
	public boolean judge2(){
		//EEl不为空 ELL'' EL RTL不同时为空
		System.out.println((extraEntityList.size()!=0&&(entityList.size()!=0||advancedEntityLabelList.size()!=0||relationshipTypeList.size()!=0)));;
		return (extraEntityList.size()!=0&&(entityList.size()!=0||advancedEntityLabelList.size()!=0||relationshipTypeList.size()!=0));
	}
	
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
		if(mergeList.size()==0){
			return;
		}
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
		for(int i=0;i<entityList.size();i++){
			String where="";
			for(int j=i+1;j<entityList.size();j++){
				GraphNode graphNode1=entityList.get(i);
				GraphNode graphNode2=entityList.get(j);
				where="";
				where+=" id(x)="+graphNode1.getId()+" and id(z)="+graphNode2.getId();
				System.out.println("查询语句为： "+"match (x)-[y*]->(z) where"+where+"return x,y,z"+"\n"
				+"和："+"match (x)<-[y*]-(z) where"+where+"return x,y,z");
				try{
					StatementResult statementResult=session.run("match (x)-[y*]->(z) where"+where+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						if(graphPath.setPath(record)==true){
							entityPairPathSet.add(graphPath);
						}
					}
					statementResult=session.run("match (x)<-[y*]-(z) where"+where+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						if(graphPath.setPath(record)==true){
							entityPairPathSet.add(graphPath);
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
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
		//语法为 match (x)-[y]-(z:ELL中的一个label) where id(x)=... return x,y,z
		Set<Long> idOfEL=new HashSet<>();
		Set<String> labelOfELL=new HashSet<>();
		for(GraphNode iter1:entityList){
			idOfEL.add(iter1.getId());
		}
		for(GraphNodeLabel iter1:entityLabelList){
			labelOfELL.add(iter1.getLabelName());
		}
		if(idOfEL.size()==0||labelOfELL.size()==0){
			return;
		}
		
		//生成的查询语句数量为 size(idOfEL)*size(labelOfELL)
		try{	
			for(Long iter1:idOfEL){
				for(String iter2:labelOfELL){
					System.out.println("查询语句为："+"match (x)-[y]-(z:"+iter2+") where id(x)="+iter1+" return x,y,z");
					StatementResult statementResult=session.run("match (x)-[y]-(z:"+iter2+") where id(x)="+iter1+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						if(graphPath.setOnePath(record)==true){
							entityEntityPathSet.add(graphPath);
						}
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("initEntityEntityPathSet:");
		for(GraphPath graphPath:entityEntityPathSet){
			graphPath.printInf();
		}
	}

	@Override
	public void initEntityRelationshipSet(){	
		
		Set<Long> idOfEL=new HashSet<>();
		Set<Long> idOfRTL=new HashSet<>();
		for(GraphNode iter1:entityList){
			idOfEL.add(iter1.getId());
		}
		for(GraphRelationship iter1:relationshipTypeList){
			idOfRTL.add(iter1.getId());
		}
		if(idOfEL.size()==0||idOfRTL.size()==0){
			return;
		}
		
		boolean flag=false;
		String where="";
		where+="(";
		for(long iter1:idOfEL){
			if(flag==true){
				where+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where+="id(x)="+iter1;
			}
		}
		where+=") and (";
		flag=false;
		for(long iter1:idOfRTL){
			if(flag==true){
				where+=" or id(y)="+iter1;
			}
			else{
				flag=true;
				where+="id(y)="+iter1;
			}
		}
		where+=")";
		System.out.println("查询语句为："+"match (x)-[y]->() where "+where+" return x,y"+"\n"
				+ "match (x)<-[y]-() where "+where+" return x,y");

		try{
			StatementResult statementResult=session.run("match (x)-[y]->() where "+where+" return x,y");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityRelationshipPathSet.add(graphPath);
				}
			}
			statementResult=session.run("match (x)<-[y]-() where "+where+" return x,y");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityRelationshipPathSet.add(graphPath);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
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
		//graphHandlerService.initRelationshipTypeList();
		
		ArrayList<String> vList=new ArrayList<>();
		ArrayList<String> aList=new ArrayList<>();
		ArrayList<String> nList=new ArrayList<>();
		
		vList.add("a1");
		vList.add("a2");
		vList.add("b2");
		vList.add("b3");
		vList.add("c3");
		vList.add("c1");
		nList.add("A");
		nList.add("C");
		nList.add("E");
		nList.add("G");
		nList.add("I");
		
		/*
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
		
		
		graphHandlerService.initExtraEntityList();
		graphHandlerService.initNewEntityLabelList();
		graphHandlerService.adjustRelationshipTypeList();
		graphHandlerService.adjustEntityList();
		*/
		graphHandlerService.chartExecution(vList,nList,aList);
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
				ArrayList<Long> bunch=new ArrayList<>();
				for(GraphRelationship iter2:iter1.getEdges()){
					if(iter2.getTag()==true){
						bunch.add(iter2.getId());
					}
				}
				if(bunch.contains(graphRelationship.getId())==true){
					System.out.println("delete from RTL:"+relationshipTypeList.get(i).getType());
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
				ArrayList<Long> bunch=new ArrayList<>();
				for(GraphNode iter2:iter1.getNodes()){
					if(iter2.getTag()==true){
						bunch.add(iter2.getId());
					}
				}
				if(bunch.contains(graphNode.getId())==true){
					System.out.println("delete from EL:"+entityList.get(i).getProperties().get("name"));
					entityList.remove(i--);
					break;
				}
			}
		}
		System.out.println("adjustEntityList:");
		for(GraphNode graphNode:entityList){
			graphNode.printInf();
		}
	}

	@Override
	public void initNewEntityLabelList() {
		/*
		 * 筛选在ELL中不在TResult中entitylabel
		 * 为权值留出空间
		 * 设置set记录已在TResult中出现过的nodeid
		 * 统计完毕后用一条查询语句完成与数据库的交互过程
		 */
		Set<Long> idOfTR=new HashSet<>();
		Set<String> labelNames=new HashSet<>();
		for(GraphPath iter1:TResult){
			ArrayList<GraphNode> tempNodes=iter1.getNodes();
			for(GraphNode iter2:tempNodes){
				if(iter2.getTag()==true){
					idOfTR.add(iter2.getId());
				}
			}
		}
		if(idOfTR.size()==0){
			advancedEntityLabelList=entityLabelList;
		}
		boolean flag=false;
		String where="";
		for(Long iter1:idOfTR){
			if(flag==true){
				where+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where+=" id(x)="+iter1;
			}
		}
		System.out.println("查询语句为："+"match (x) where "+where+" return x");
		StatementResult statementResult=session.run("match (x) where "+where+" return x");
		while(statementResult.hasNext()){
			Record record=statementResult.next();
			for(String iter1:record.get(0).asNode().labels()){
				labelNames.add(iter1);
			}
		}
		for(GraphNodeLabel iter1:entityLabelList){
			if(labelNames.contains(iter1.getLabelName())){
				continue;
			}
			else{
				advancedEntityLabelList.add(iter1);
			}
		}
		System.out.println("initNewEntityLabelList:");
		for(GraphNodeLabel iter1:advancedEntityLabelList){
			iter1.printInf();
		}
	}
//
	
	@Override
	public void initExtraEntityList() {
		/*
		 * 满足三个条件：
		 * 1.EEL中没有任何一个节点在EL中(用nodeId识别)
		 * 2.EEL中所有节点标签没有一个在ELL中
		 * 3.EEL所有节点在TResult中
		 */
		Set<Long> idOfTR=new HashSet<>();
		Set<Long> idOfEntityList=new HashSet<>();
		for(GraphPath iter1:TResult){
			for(GraphNode iter2:iter1.getNodes()){
				idOfTR.add(iter2.getId());
			}
		}
		//排除掉EL中的节点
		for(GraphNode iter1:entityList){
			idOfEntityList.add(iter1.getId());
		}
		for(Long iter1:idOfEntityList){
			idOfTR.remove(iter1);
		}
		if(idOfTR.size()==0){
			return;
		}
		boolean flag=false;
		String where="";
		for(Long iter1:idOfTR){
			if(flag==true){
				where+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where+=" id(x)="+iter1;
			}
		}
		System.out.println("查询语句为："+"match (x) where "+where+" return x");
		StatementResult statementResult=session.run("match (x) where "+where+" return x");
		
		//statementResult此时应包括TResult中所有不包含EL的节点,去labelNamesOfELL包含ELL中所有标签
		Set<String> labelNamesOfELL=new HashSet<>();
		for(GraphNodeLabel iter1:entityLabelList){
			labelNamesOfELL.add(iter1.getLabelName());
		}
		while(statementResult.hasNext()){
			Record record=statementResult.next();
			flag=false;
			for(String label:record.get(0).asNode().labels()){
				if(labelNamesOfELL.contains(label)){
					flag=true;
					break;
				}
			}
			if(flag==false){
				Set<Long> idOfEEL=new HashSet<>();
				for(GraphNode iter1:extraEntityList){
					idOfEEL.add(iter1.getId());
				}
				if(idOfEEL.contains(new GraphNode(record.get(0).asNode()).getId())==false){
					extraEntityList.add(new GraphNode(record.get(0).asNode()));
				}
			}
		}
		System.out.println("initExtraEntityList:");
		for(GraphNode iter1:extraEntityList){
			iter1.printInf();
		}
	}
	
	@Override
	public void adjustEntityRelationshipPathSet() {
		//符合EEL-RTL模式
		//match (x)-[y]->() where ...
		//match (x)<-[y]-() where ...
		
		Set<Long> idOfEEL=new HashSet<>();
		Set<Long> idOfRTL=new HashSet<>();
		for(GraphNode iter1:extraEntityList){
			idOfEEL.add(iter1.getId());
		}
		for(GraphRelationship iter1:relationshipTypeList){
			idOfRTL.add(iter1.getId());
		}
		if(idOfEEL.size()==0||idOfRTL.size()==0){
			return;
		}
		boolean flag=false;
		String where="";
		where+="(";
		for(long iter1:idOfEEL){
			if(flag==true){
				where+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where+="id(x)="+iter1;
			}
		}
		where+=") and (";
		flag=false;
		for(long iter1:idOfRTL){
			if(flag==true){
				where+=" or id(y)="+iter1;
			}
			else{
				flag=true;
				where+="id(y)="+iter1;
			}
		}
		where+=")";
		System.out.println("查询语句为："+"match (x)-[y]->() where "+where+" return x,y"+"\n"
				+ "match (x)<-[y]-() where "+where+" return x,y");

		try{
			StatementResult statementResult=session.run("match (x)-[y]->() where "+where+" return x,y");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityRelationshipPathSet.add(graphPath);
				}
			}
			statementResult=session.run("match (x)<-[y]-() where "+where+" return x,y");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityRelationshipPathSet.add(graphPath);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("adjustEntityRelationshipPathSet:");
		for(GraphPath graphPath:entityRelationshipPathSet){
			graphPath.printInf();
		}
	}

	@Override
	public void adjustEntityEntityPathSet() {
		//符合EL--ELL模式
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
		System.out.println("adjustEntityEntityPathSet:");
		for(GraphPath graphPath:entityEntityPathSet){
			graphPath.printInf();
		}
	}
	
	@Override
	public void adjustEntityPairsPathSet() {
		//符合EEL EL模式
		//match (x)-[y*]->(z) where
		//match (z)-[y*]->(z) where
		//更改graphpath 中setpath方法
		
		Set<Long> idOfEL=new HashSet<>();
		Set<Long> idOfEEL=new HashSet<>();
		for(GraphNode iter1:entityList){
			idOfEL.add(iter1.getId());
		}
		for(GraphNode iter1:extraEntityList){
			idOfEEL.add(iter1.getId());
		}
		if(idOfEEL.size()==0||idOfEL.size()==0){
			return;
		}
		boolean flag=false;
		String where1="";
		String where2="";
		for(long iter1:idOfEL){
			if(flag==true){
				where1+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where1+=" id(x)="+iter1;
			}
		}
		for(long iter1:idOfEEL){
			where2+=" id(z)="+iter1;
		}
		System.out.println("查询语句为："+"match (x)-[y*]->(z) "+where1+where2+" return distinct x,y,z"+"\n"+"match (z)-[y*]->(x) "+where1+where2+" return distinct x,y,z");
		try{
			StatementResult statementResult=session.run("match (x)-[y*]->(z) "+where1+where2+" return distinct x,y,z");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityPairPathSet.add(graphPath);
				}
			}
			statementResult=session.run("match (z)-[y*]->(x) "+where1+where2+" return distinct x,y,z");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				GraphPath graphPath=new GraphPath();
				if(graphPath.setPath(record)==true){
					entityPairPathSet.add(graphPath);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("adjustEntityPairPathSet:");
		for(GraphPath iter1:entityPairPathSet){
			iter1.printInf();
		}
	}
	
	@Override
	public void initSResult() {
		// 符合 newELL-RTL模式
		/*
		 * 找到与RTL联结的节点(根据关系id查找)
		 * 根据newELL一一排除节点
		 * 存储入SResult
		 */
		
		HashSet<Long> idOfEdges=new HashSet<>();
		for(GraphRelationship iter1:relationshipTypeList){
			idOfEdges.add(iter1.getId());
		}
		if(idOfEdges.size()==0){
			return;
		}
		boolean flag=false;
		String where="";
		for(Long iter1:idOfEdges){
			if(flag==true){
				where+=" or id(x)="+iter1;
			}
			else{
				flag=true;
				where+=" id(x)="+iter1;
			}
		}
		System.out.println("查询语句为："+"match (x)-[y]-() where "+where+" return x");
		try{
			StatementResult statementResult=session.run("match (x)-[y]->(z) where "+where+" return x,y,z");
			//labelNames承载了newELL中所有标签
			Set<String> labelNames=new HashSet<>();
			for(GraphNodeLabel iter1:advancedEntityLabelList){
				labelNames.add(iter1.getLabelName());
			}
			
			//只要x或z有一个节点中包含了至少一个labelNames中的标签 则条件成立
			while(statementResult.hasNext()){
				flag=false;
				Record record=statementResult.next();
				for(String iter1:record.get(0).asNode().labels()){
					if(labelNames.contains(iter1)){
						flag=true;
						break;
					}
				}
				if(flag==false){
					for(String iter1:record.get(2).asNode().labels()){
						if(labelNames.contains(iter1)){
							flag=true;
							break;
						}
					}
				}
				if(flag==true){
					GraphPath graphPath=new GraphPath();
					graphPath.setOnePath(record);
					SResult.add(graphPath);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("initSResult:");
		for(GraphPath iter1:SResult){
			iter1.printInf();
		}
	}
	
	@Override
	public void finalEntityLabelList() {
		// 在newELL中不在SResult中
		// 既在newELL也在SResult中的要删除
		HashSet<String> labelsOfSR=new HashSet<>();
		for(GraphPath iter1:SResult){
			for(GraphNode iter2:iter1.getNodes()){
				for(GraphNodeLabel iter3:iter2.getLabels()){
					labelsOfSR.add(iter3.getLabelName());
				}
			}
		}
		for(int i=0;i<advancedEntityLabelList.size();i++){
			GraphNodeLabel iter1=advancedEntityLabelList.get(i);
			if(labelsOfSR.contains(iter1.getLabelName())){
				advancedEntityLabelList.remove(i--);
			}
		}
		System.out.println("finalEntityLabelList:");
		for(GraphNodeLabel iter1:advancedEntityLabelList){
			iter1.printInf();
		}
	}

	@Override
	public void finalRelationshipTypeList() {
		// 在RTL中不在SResult中
		// 在RTL中也在SResult中的要删除
		HashSet<Long> idOfSR=new HashSet<>();
		for(GraphPath iter1:SResult){
			idOfSR.add(iter1.getEdges().get(0).getId());
		}
		for(int i=0;i<relationshipTypeList.size();i++){
			GraphRelationship iter1=relationshipTypeList.get(i);
			if(idOfSR.contains(iter1.getId())==true){
				relationshipTypeList.remove(i--);
			}
		}
		System.out.println("finalRelationshipTypeList:");
		for(GraphRelationship iter1:relationshipTypeList){
			iter1.printInf();
		}
	}

	public void chartExecution(ArrayList<String> vList,ArrayList<String> nList,ArrayList<String> aList){
		
		GraphHandlerService graphHandlerService=new GraphHandlerService();
		graphHandlerService.doInput(vList,aList,nList);
		graphHandlerService.initEntityKeySet();
		graphHandlerService.initRelationshipTypeList();
		graphHandlerService.initEntityLabelList();
		graphHandlerService.initEntityList();
		graphHandlerService.adjustEntityLabelList();
		
		if(graphHandlerService.judge1()==false){
			return;
		}
		
		graphHandlerService.initEntityPairsPathSet();
		graphHandlerService.initEntityRelationshipSet();
		graphHandlerService.initEntityEntityPathSet();
		graphHandlerService.getTResult();
		
		graphHandlerService.initExtraEntityList();
		graphHandlerService.initNewEntityLabelList();
		graphHandlerService.adjustRelationshipTypeList();
		graphHandlerService.adjustEntityList();
		
		
		
		while(graphHandlerService.judge2()==true){
			graphHandlerService.adjustEntityRelationshipPathSet();
			graphHandlerService.adjustEntityEntityPathSet();
			graphHandlerService.adjustEntityPairsPathSet();
			graphHandlerService.getTResult();
			graphHandlerService.initExtraEntityList();
			graphHandlerService.initNewEntityLabelList();
			graphHandlerService.adjustRelationshipTypeList();
			graphHandlerService.adjustEntityList();
		}
		
		graphHandlerService.initSResult();
		graphHandlerService.finalEntityLabelList();
		graphHandlerService.finalRelationshipTypeList();
	}
}
