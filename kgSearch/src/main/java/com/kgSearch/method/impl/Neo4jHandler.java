package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kgSearch.method.GraphHandler;
import com.kgSearch.pojo.AuthToken;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphPathPattern;
import com.kgSearch.pojo.GraphRelationshipType;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.MatchTags;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.INeo4jDBService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-mybatis.xml" })
public class Neo4jHandler extends GraphHandler{

	@Resource
	private INeo4jDBService service;
	
	private Session session;
	private Map<String,ArrayList<String>> labelPropertiesMap;
	private Map<String,ArrayList<String>> typePropertiesMap;
	private Map<String,Integer> labelIntMap;
	private Map<String,Integer> typeIntMap;
	private Map<String,ArrayList<Long>> labelIdMap;
	private Map<String,ArrayList<Long>> typeIdMap;
	private Map<Long,ArrayList<String>> idLabelMap;
	private Map<Long,String> idTypeMap;
	
	public Neo4jHandler(ArrayList<String> verbList, ArrayList<String> adList, ArrayList<String> nounList, String host, AuthToken token) {
		super(verbList, adList, nounList);
		this.session = GraphDatabase.driver(host, AuthTokens.basic(token.getUserName(),token.getPassword())).session();
	}

	@Deprecated
	public void setPropertiesMaps(Map<String,Integer> labelIntMap,Map<String,Integer> typeIntMap,Map<String,ArrayList<String>> labelPropertiesMap,Map<String,ArrayList<String>> typePropertiesMap){
		this.labelIntMap=labelIntMap;
		this.typeIntMap=typeIntMap;
		this.labelPropertiesMap=labelPropertiesMap;
		this.typePropertiesMap=typePropertiesMap;
	}
	
	public void initPropertiesMaps(){
		//通过mybatis获得labelPropertiesMap和typePropertiesMap相关的信息
		labelIntMap=new HashMap<>();
		typeIntMap=new HashMap<>();
		labelPropertiesMap=new HashMap<>();
		typePropertiesMap=new HashMap<>();
		try{
			ArrayList<TypeProperties> typeProperties=(ArrayList<TypeProperties>)service.selectAllTypes();
			ArrayList<LabelProperties> labelProperties=(ArrayList<LabelProperties>)service.selectAllLabels();
			for(TypeProperties element:typeProperties){
				typeIntMap.put(element.getType(),element.getId());
				ArrayList<String> propertiesOfAType=new ArrayList<>();
				String[] temp=element.getProperties().split(",");
				for(String iter:temp){
					propertiesOfAType.add(iter);
				}
				typePropertiesMap.put(element.getType(),propertiesOfAType);
			}
			for(LabelProperties element:labelProperties){
				labelIntMap.put(element.getLabel(),element.getId());
				ArrayList<String> propertiesOfALabel=new ArrayList<>();
				String[] temp=element.getProperties().split(",");
				for(String iter:temp){
					propertiesOfALabel.add(iter);
				}
				labelPropertiesMap.put(element.getLabel(),propertiesOfALabel);
			}
		
			System.out.println("typePropertiesMap:"+typePropertiesMap);
			System.out.println("labelPropertiesMap:"+labelPropertiesMap);
			System.out.println("typeIntMap:"+typeIntMap);
			System.out.println("labelIntMap:"+labelIntMap);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//测试用
		
		
		/*ArrayList<String> temp=new ArrayList<>();
		temp.add("r1");
		typeIntMap.put("R1",1);
		typePropertiesMap.put("R1",temp);
		ArrayList<String> temp1=new ArrayList<>();
		temp1.add("r2");
		temp1.add("r21");
		typeIntMap.put("R2",2);
		typePropertiesMap.put("R2",temp1);
		ArrayList<String> temp2=new ArrayList<>();
		temp2.add("r3");
		temp2.add("r32");
		typeIntMap.put("R3",3);
		typePropertiesMap.put("R3",temp2);
		
		ArrayList<String> temp3=new ArrayList<>();
		temp3.add("l1");
		labelIntMap.put("L1",1);
		labelIntMap.put("L2",2);
		labelIntMap.put("L3",3);
		labelIntMap.put("L4",4);
		labelIntMap.put("L5",5);
		labelPropertiesMap.put("L1",temp3);
		ArrayList<String> temp4=new ArrayList<>();
		temp4.add("l2");
		labelPropertiesMap.put("L2",temp4);
		ArrayList<String> temp5=new ArrayList<>();
		temp5.add("l3");
		temp5.add("l34");
		labelPropertiesMap.put("L3",temp5);
		ArrayList<String> temp6=new ArrayList<>();
		temp6.add("l4");
		labelPropertiesMap.put("L4",temp6);
		ArrayList<String> temp7=new ArrayList<>();
		temp7.add("l5");
		temp7.add("l51");
		labelPropertiesMap.put("L5",temp7);
		
		System.out.println("typePropertiesMap:"+typePropertiesMap);
		System.out.println("labelPropertiesMap:"+labelPropertiesMap);
		System.out.println("typeIntMap:"+typeIntMap);
		System.out.println("labelIntMap:"+labelIntMap);*/
	}
	
	public void initIdMaps(){
		labelIdMap=new HashMap<>();
		typeIdMap=new HashMap<>();
		idLabelMap=new HashMap<>();
		idTypeMap=new HashMap<>();
		
		Set<String> labelSet=new HashSet<>();				//临时存储数据库中所有标签类型
		Set<String> typeSet=new HashSet<>();				//临时存储数据库中所有关系类型
		Set<Long> nodeIdSet=new HashSet<>();
		Set<Long> edgeIdSet=new HashSet<>();
		try{
			StatementResult statementResult=session.run("match (x) return x");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				nodeIdSet.add(record.get(0).asNode().id());
				for(String labelOfNode:record.get(0).asNode().labels()){
					labelSet.add(labelOfNode);
				}
			}
			statementResult=session.run("match ()-[x]->() return x");
			while(statementResult.hasNext()){
				Record record=statementResult.next();
				edgeIdSet.add(record.get(0).asRelationship().id());
				String typeOfRelationship=record.get(0).asRelationship().type();
				typeSet.add(typeOfRelationship);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		for(String Alabel:labelSet){
			ArrayList<Long> temp=new ArrayList<>();
			try{
				StatementResult statementResult=session.run("match (x:"+Alabel+") return x");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					temp.add(record.get(0).asNode().id());
				}
				//System.out.println("temp:"+temp);
				labelIdMap.put(Alabel,temp);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(String Atype:typeSet){
			ArrayList<Long> temp=new ArrayList<>();
			try{
				StatementResult statementResult=session.run("match ()-[x:"+Atype+"]->() return x");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					temp.add(record.get(0).asRelationship().id());
				}
				//System.out.println("temp:"+temp);
				typeIdMap.put(Atype,temp);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Long id:nodeIdSet){
			ArrayList<String> temp=new ArrayList<>();
			try{
				StatementResult statementResult=session.run("match (x) where id(x)="+id+" return x");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					for(String Alabel:record.get(0).asNode().labels()){
						temp.add(Alabel);
					}
				}
				idLabelMap.put(id,temp);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Long id:edgeIdSet){
			try{
				StatementResult statementResult=session.run("match ()-[x]->() where id(x)="+id+" return x");
				while(statementResult.hasNext()){
					Record record=statementResult.next();
					idTypeMap.put(id,record.get(0).asRelationship().type());
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("typeIdMap:"+typeIdMap);
		System.out.println("labelIdMap:"+labelIdMap);
		System.out.println("idTypeMap:"+idTypeMap);
		System.out.println("idLabelMap:"+idLabelMap);;
	}
	
	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb() {
		ArrayList<GraphRelationshipType> VRTL=new ArrayList<>();
		Set<Integer> idOfVRTL=new HashSet<>();
		for(String Averb:this.getVerbList()){
			for(String Atype:typeIdMap.keySet()){
				if(Atype.equals(Averb)){
					if(idOfVRTL.contains(typeIntMap.get(Atype))==false){
						//不包含
						GraphRelationshipType graphRelationshipType=new GraphRelationshipType();
						graphRelationshipType.setId(typeIntMap.get(Atype));
						graphRelationshipType.setTypeName(Atype);
						graphRelationshipType.setWeight(1);
						MatchTags matchTags=new MatchTags();
						matchTags.init();
						matchTags.setVerbMatch(true);
						matchTags.addVerbMatchTag(Averb);
						graphRelationshipType.setMatchTags(matchTags);
						VRTL.add(graphRelationshipType);
						idOfVRTL.add(typeIntMap.get(Atype));
					}
					else{
						for(GraphRelationshipType graphRelationshipType:VRTL){
							graphRelationshipType.getMatchTags().setVerbMatch(true);
							graphRelationshipType.getMatchTags().addVerbMatchTag(Averb);
							graphRelationshipType.setWeight(graphRelationshipType.getWeight()+1);
						}
					}
				}
			}
		}
		return VRTL;
	}

	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj() {
		ArrayList<GraphRelationshipType> ARTL=new ArrayList<>();
		Set<Integer> idOfARTL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			for(String Atype:typePropertiesMap.keySet()){
				ArrayList<String> keys=typePropertiesMap.get(Atype);
				if(keys.contains(Aadj)){
					//属性名称匹配，搜索拥有拥有该属性名称的关系类型的关系id
					if(idOfARTL.contains(typeIntMap.get(Atype))==false){
						GraphRelationshipType graphRelationshipType=new GraphRelationshipType();
						graphRelationshipType.setId(typeIntMap.get(Atype));
						graphRelationshipType.setTypeName(Atype);
						graphRelationshipType.setWeight(1);
						MatchTags matchTags=new MatchTags();
						matchTags.init();
						matchTags.setAdjMatch(true);
						matchTags.addAdjMatchTag(Aadj);
						graphRelationshipType.setMatchTags(matchTags);
						ARTL.add(graphRelationshipType);
						idOfARTL.add(typeIntMap.get(Atype));
					}
					else{
						for(GraphRelationshipType graphRelationshipType:ARTL){
							graphRelationshipType.getMatchTags().setAdjMatch(true);
							graphRelationshipType.getMatchTags().addAdjMatchTag(Aadj);
							graphRelationshipType.setWeight(graphRelationshipType.getWeight()+1);
						}
					}
				}
			}
		}
		return ARTL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun() {
		// TODO Auto-generated method stub
		ArrayList<GraphNodeLabel> NELL=new ArrayList<>();
		Set<Integer> idOfNELL=new HashSet<>();
		for(String Anoun:this.getNounList()){
			for(String Alabel:labelIdMap.keySet()){
				if(Alabel.equals(Anoun)){
					if(idOfNELL.contains(labelIntMap.get(Alabel))==false){
						GraphNodeLabel graphNodeLabel=new GraphNodeLabel();
						graphNodeLabel.setId(labelIntMap.get(Alabel));
						graphNodeLabel.setLabelName(Alabel);
						graphNodeLabel.setWeight(0);
						MatchTags matchTags=new MatchTags();
						matchTags.init();
						matchTags.setNounMatch(true);
						matchTags.addNounMatchTag(Anoun);
						graphNodeLabel.setMatchTags(matchTags);
						NELL.add(graphNodeLabel);
						idOfNELL.add(labelIntMap.get(Alabel));
					}
					else{
						for(GraphNodeLabel graphNodeLabel:NELL){
							if(graphNodeLabel.getId()==labelIntMap.get(Alabel)){
								MatchTags matchTags=graphNodeLabel.getMatchTags();
								matchTags.setNounMatch(true);
								matchTags.addNounMatchTag(Anoun);
								graphNodeLabel.setMatchTags(matchTags);
								graphNodeLabel.setWeight(graphNodeLabel.getWeight()+1);
							}
						}
					}
				}
			}
		}
		
		return NELL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj() {
		// TODO Auto-generated method stub
		ArrayList<GraphNodeLabel> AELL=new ArrayList<>();
		Set<Integer> idOfAELL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			for(String Alabel:labelIdMap.keySet()){
				ArrayList<String> properties=labelPropertiesMap.get(Alabel);
				if(properties.contains(Aadj)){
					if(idOfAELL.contains(labelIntMap.get(Alabel))==false){
						GraphNodeLabel graphNodeLabel=new GraphNodeLabel();
						graphNodeLabel.setId(labelIntMap.get(Alabel));
						graphNodeLabel.setLabelName(Alabel);
						graphNodeLabel.setWeight(1);
						MatchTags matchTags=new MatchTags();
						matchTags.init();
						matchTags.setAdjMatch(true);
						matchTags.addAdjMatchTag(Aadj);
						graphNodeLabel.setMatchTags(matchTags);
						AELL.add(graphNodeLabel);
						idOfAELL.add(labelIntMap.get(Alabel));
					}
					else{
						for(GraphNodeLabel graphNodeLabel:AELL){
							if(graphNodeLabel.getId()==labelIntMap.get(Alabel)){
								MatchTags matchTags=graphNodeLabel.getMatchTags();
								matchTags.setAdjMatch(true);
								matchTags.addAdjMatchTag(Aadj);
								graphNodeLabel.setMatchTags(matchTags);
								graphNodeLabel.setWeight(graphNodeLabel.getWeight()+1);
							}
						}
					}
				}
			}
		}
		return AELL;
	}

	@Override
	public ArrayList<GraphNode> searchGraphNodeByNoun() {
		ArrayList<GraphNode> EL=new ArrayList<>();
		ArrayList<Long> idOfEL=new ArrayList<>();
		try{
			StatementResult statementResult=session.run("match (x) return x");
			while(statementResult.hasNext()){
				ArrayList<String> values=new ArrayList<>();
				Record record=statementResult.next();
				for(String Akey:record.get(0).asNode().keys()){
					System.out.println(record.get(0).asNode().get(Akey).asString());
					values.add(record.get(0).asNode().get(Akey).asString());
				}
				for(String Anoun:this.getNounList()){
					if(values.contains(Anoun)){
						Node node=record.get(0).asNode();
						if(idOfEL.contains(node.id())==false){
							//加入新元素
							ArrayList<Integer> labels=new ArrayList<>();
							for(String temp:node.labels()){
								labels.add(labelIntMap.get(temp));
							}
							MatchTags matchTags=new MatchTags();
							matchTags.init();
							matchTags.setNounMatch(true);
							matchTags.addNounMatchTag(Anoun);
							GraphNode graphNode=new GraphNode();
							graphNode.setId(node.id());
							graphNode.setLabels(labels);
							graphNode.setWeight(1);
							graphNode.setMatchTag(matchTags);
							EL.add(graphNode);
							idOfEL.add(node.id());
						}
						else{
							for(GraphNode graphNode:EL){
								graphNode.setWeight(graphNode.getWeight()+1);
								graphNode.getMatchTag().setNounMatch(true);
								graphNode.getMatchTag().addNounMatchTag(Anoun);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
		return EL;
	}

	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> EL) {
		
		ArrayList<GraphPath> EPPS=new ArrayList<>(); 
		for(int i=0;i<EL.size();i++){
			for(int j=i+1;j<EL.size();j++){
				GraphNode g1=EL.get(i);
				GraphNode g2=EL.get(j);
				try{
					StatementResult statementResult=session.run("match (x)-[y*]->(z) where id(x)="+g1.getId()+" and id(z)="+g2.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match (x)<-[y*]-(z) where id(x)="+g1.getId()+" and id(z)="+g2.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setRevPath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						EPPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("EPPS");
		for(GraphPath graphPath:EPPS){
			graphPath.printInf();
		}
		return EPPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodeLabelPath(ArrayList<GraphNode> nodes, ArrayList<GraphNodeLabel> ELL) {
		ArrayList<GraphPath> EEPS=new ArrayList<>();
		for(GraphNode node:nodes){
			for(GraphNodeLabel graphNodeLabel:ELL){
				try{
					StatementResult statementResult=session.run("match (x)-[y]-(z:"+graphNodeLabel.getLabelName()+") where id(x)="+node.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setOnePath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						EEPS.add(graphPath);
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("EEPS");
		for(GraphPath graphPath:EEPS){
			graphPath.printInf();
		}
		return EEPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodeRelationTypePath(ArrayList<GraphNode> nodes,
			ArrayList<GraphRelationshipType> RTL) {
		ArrayList<GraphPath> ERPS=new ArrayList<>();
		for(GraphNode node:nodes){
			for(GraphRelationshipType relationshipType:RTL){
				try{
					StatementResult statementResult=session.run("match (x)-[y:"+relationshipType.getTypeName()+"]->(z) where id(x)="+node.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setOnePath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						ERPS.add(graphPath);
					}
					statementResult=session.run("match (x)<-[y:"+relationshipType.getTypeName()+"]-(z) where id(x)="+node.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setOnePath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						ERPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("ERPS");
		for(GraphPath graphPath:ERPS){
			graphPath.printInf();
		}
		return ERPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> sub_EL, ArrayList<GraphNode> EEL) {
		ArrayList<GraphPath> EPPS=new ArrayList<>(); 
		for(GraphNode g1:sub_EL){
			for(GraphNode g2:EEL){
				try{
					StatementResult statementResult=session.run("match (x)-[y*]->(z) where id(x)="+g1.getId()+" and id(z)="+g2.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match (x)<-[y*]-(z) where id(x)="+g1.getId()+" and id(z)="+g2.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setRevPath(record);
						graphPath.setTypeAndLabels(idLabelMap, labelIntMap, idTypeMap, typeIntMap);
						EPPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return EPPS;
	}

	@Override
	public ArrayList<GraphPathPattern> searchPattern(ArrayList<GraphNodeLabel> sub_ELL,
			ArrayList<GraphRelationshipType> sub_RTL) {
		if(sub_ELL==null||sub_RTL==null){
			return null;
		}
		System.out.println("sub_ELL sub_RTL:");
		for(GraphNodeLabel gl:sub_ELL){
			System.out.println(gl.getLabelName());
		}
		for(GraphRelationshipType rt:sub_RTL){
			System.out.println(rt.getTypeName());
		}
		ArrayList<GraphPathPattern> SResult=new ArrayList<>();
		for(GraphNodeLabel graphNodeLabel:sub_ELL){
			for(GraphRelationshipType graphRelationshipType:sub_RTL){
				try{
					StatementResult statementResult=session.run("match (x:"+graphNodeLabel.getLabelName()+")-[y:"+graphRelationshipType.getTypeName()+"]->(z) return x,y");
					while(statementResult.hasNext()){
						@SuppressWarnings("unused")
						Record record=statementResult.next();
						GraphPathPattern graphPathPattern=new GraphPathPattern();
						graphPathPattern.setLabel(graphNodeLabel);
						graphPathPattern.setRelationType(graphRelationshipType);
						graphPathPattern.setDirection(0);
						
					}
					statementResult=session.run("match (x:"+graphNodeLabel.getLabelName()+")<-[y:"+graphRelationshipType.getTypeName()+"]-(z) return x,y");
					while(statementResult.hasNext()){
						@SuppressWarnings("unused")
						Record record=statementResult.next();
						GraphPathPattern graphPathPattern=new GraphPathPattern();
						graphPathPattern.setLabel(graphNodeLabel);
						graphPathPattern.setRelationType(graphRelationshipType);
						graphPathPattern.setDirection(1);
						
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return SResult;
	}
	
	public static void main(String args[]){
		ArrayList<String> verbList=new ArrayList<>();
		ArrayList<String> nounList=new ArrayList<>();
		ArrayList<String> adList=new ArrayList<>();
		
		verbList.add("R1");
		verbList.add("R3");
		
		adList.add("r2");
		adList.add("l1");
		adList.add("l51");
		
		nounList.add("L2");
		nounList.add("L4");
		nounList.add("l34a");
		nounList.add("l34b");
		
		AuthToken authToken=new AuthToken();
		authToken.setUserName("neo4j");
		authToken.setPassword("huhantao9");
		Neo4jHandler handler=new Neo4jHandler(verbList,adList,nounList,"bolt://localhost:7687", authToken);
		handler.initPropertiesMaps();
		handler.initIdMaps();
		
		handler.searchAction();
	}

}
