package com.kgSearch.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphPathPattern;
import com.kgSearch.pojo.GraphRelationshipType;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.MatchTags;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.impl.LabelService;
import com.kgSearch.service.impl.RelationTypeService;

public class Neo4jGraphHandler extends GraphHandler{
	
	protected Session session;
	protected Map<String,Integer> labelIdMap;
	protected Map<String,Integer> typeIdMap;
	
	public void initSession(Driver driver){
		session=driver.session();
	}

	public void initIdMap(ArrayList<LabelProperties> AllLabel, ArrayList<TypeProperties> AllType){
		labelIdMap=new HashMap<>();
		typeIdMap=new HashMap<>();
		for(LabelProperties Alabel:AllLabel){
			labelIdMap.put(Alabel.getLabel(),Alabel.getId());
		}
		for(TypeProperties Atype:AllType){
			typeIdMap.put(Atype.getType(),Atype.getId());
		}
	}
	
	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb(RelationTypeService relationTypeService) {
		ArrayList<GraphRelationshipType> VRTL=new ArrayList<>();
		Set<Integer> idOfVRTL=new HashSet<>();
		for(String Averb:this.getVerbList()){
			ArrayList<TypeProperties> searchResult=relationTypeService.fuzzySelectTypeByString(Averb);
			for(TypeProperties Atype:searchResult){
				int id=Atype.getId();
				if(idOfVRTL.contains(id)==false){
					GraphRelationshipType graphRelationshipType=new GraphRelationshipType();
					graphRelationshipType.setId(id);
					graphRelationshipType.setTypeName(Atype.getType());
					graphRelationshipType.setWeight(1);
					MatchTags matchTags=new MatchTags();
					matchTags.setVerbMatch(true);
					matchTags.setVerbMatchList(new ArrayList<>());
					matchTags.addVerbMatchTag(Averb);
					graphRelationshipType.setMatchTags(matchTags);
					Map<String,Object> properties=new HashMap<>();
					if(Atype.getProperties()!=null){
						for(String Aproperty:Atype.getProperties().split(",")){
							properties.put(Aproperty,"");
						}
					}
					graphRelationshipType.setProperties(properties);
					VRTL.add(graphRelationshipType);
					idOfVRTL.add(id);
				}
				else{
					for(GraphRelationshipType graphRelationshipType:VRTL){
						if(graphRelationshipType.getId()==id){
							graphRelationshipType.setWeight(graphRelationshipType.getWeight()+1);
							graphRelationshipType.getMatchTags().addVerbMatchTag(Averb);
						}
					}
				}
			}
		}
		return VRTL;
	}

	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj(RelationTypeService relationTypeService) {
		ArrayList<GraphRelationshipType> ARTL=new ArrayList<>();
		Set<Integer> idOfARTL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			ArrayList<TypeProperties> searchResult=relationTypeService.fuzzySelectPropertiesByString(Aadj);
			for(TypeProperties Atype:searchResult){
				int id=Atype.getId();
				if(idOfARTL.contains(id)==false){
					GraphRelationshipType graphRelationshipType=new GraphRelationshipType();
					graphRelationshipType.setId(id);
					graphRelationshipType.setTypeName(Atype.getType());
					graphRelationshipType.setWeight(1);
					MatchTags matchTags=new MatchTags();
					matchTags.setAdjMatch(true);
					matchTags.setAdjMatchList(new ArrayList<>());
					matchTags.addAdjMatchTag(Aadj);
					graphRelationshipType.setMatchTags(matchTags);
					Map<String,Object> properties=new HashMap<>();
					if(Atype.getProperties()!=null){
						for(String Aproperty:Atype.getProperties().split(",")){
							properties.put(Aproperty,"");
						}
					}
					graphRelationshipType.setProperties(properties);
					ARTL.add(graphRelationshipType);
					idOfARTL.add(id);
				}
				else{
					for(GraphRelationshipType graphRelationshipType:ARTL){
						if(graphRelationshipType.getId()==id){
							graphRelationshipType.setWeight(graphRelationshipType.getWeight()+1);
							graphRelationshipType.getMatchTags().addAdjMatchTag(Aadj);
						}
					}
				}
			}
		}
		return ARTL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun(LabelService labelService) {
		ArrayList<GraphNodeLabel> NELL=new ArrayList<>();	
		Set<Integer> idOfNELL=new HashSet<>();
		for(String Anoun:this.getNounList()){
			ArrayList<LabelProperties> searchResult=labelService.fuzzySelectLabelByString(Anoun);
			for(LabelProperties Alabel:searchResult){
				int id=Alabel.getId();
				if(idOfNELL.contains(id)==false){
					GraphNodeLabel graphNodeLabel=new GraphNodeLabel();
					graphNodeLabel.setId(id);
					graphNodeLabel.setLabelName(Alabel.getLabel());
					graphNodeLabel.setWeight(1);
					MatchTags matchTags=new MatchTags();
					matchTags.setNounMatch(true);
					matchTags.setNounLabelMatchList(new ArrayList<>());
					matchTags.addNounLabelMatchTag(Anoun);
					graphNodeLabel.setMatchTags(matchTags);
					Map<String,Object> properties=new HashMap<>();
					if(Alabel.getProperties()!=null){
						for(String Aproperty:Alabel.getProperties().split(",")){
							properties.put(Aproperty,"");
						}
					}
					graphNodeLabel.setProperties(properties);
					NELL.add(graphNodeLabel);
					idOfNELL.add(id);
				}
				else{
					for(GraphNodeLabel graphNodeLabel:NELL){
						if(graphNodeLabel.getId()==id){
							graphNodeLabel.setWeight(graphNodeLabel.getWeight()+1);
							graphNodeLabel.getMatchTags().addNounLabelMatchTag(Anoun);
						}
					}
				}
			}
		}
		return NELL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj(LabelService labelService) {
		ArrayList<GraphNodeLabel> AELL=new ArrayList<>();
		Set<Integer> idOfAELL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			ArrayList<LabelProperties> searchResult=labelService.fuzzySelectPropertiesByString(Aadj);
			for(LabelProperties Alabel:searchResult){
				int id=Alabel.getId();
				if(idOfAELL.contains(id)==false){
					GraphNodeLabel graphNodeLabel=new GraphNodeLabel();
					graphNodeLabel.setId(id);
					graphNodeLabel.setLabelName(Alabel.getLabel());
					graphNodeLabel.setWeight(1);
					MatchTags matchTags=new MatchTags();
					matchTags.setAdjMatch(true);
					matchTags.setAdjMatchList(new ArrayList<>());
					matchTags.addAdjMatchTag(Aadj);
					graphNodeLabel.setMatchTags(matchTags);
					Map<String,Object> properties=new HashMap<>();
					if(Alabel.getProperties()!=null){
						for(String Aproperty:Alabel.getProperties().split(",")){
							properties.put(Aproperty,"");
						}
					}
					graphNodeLabel.setProperties(properties);
					AELL.add(graphNodeLabel);
					idOfAELL.add(id);
				}
				else{
					for(GraphNodeLabel graphNodeLabel:AELL){
						if(graphNodeLabel.getId()==id){
							graphNodeLabel.setWeight(graphNodeLabel.getWeight()+1);
							graphNodeLabel.getMatchTags().addAdjMatchTag(Aadj);
						}
					}
				}
			}
		}
		return AELL;
	}
	
	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> EL) {
		ArrayList<GraphPath> EPPS=new ArrayList<GraphPath>(); 
		for(int i=0;i<EL.size();i++){
			for(int j=i+1;j<EL.size();j++){
				GraphNode g1=EL.get(i);
				GraphNode g2=EL.get(j);
				try{
					StatementResult statementResult=session.run("match p = ((x)-[*]->(y)) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match p = ((y)-[*]->(x)) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
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
	public ArrayList<GraphPath> searchNodeLabelPath(ArrayList<GraphNode> nodes, ArrayList<GraphNodeLabel> ELL) {
		ArrayList<GraphPath> EEPS=new ArrayList<>();
		for(GraphNode node:nodes){
			for(GraphNodeLabel graphNodeLabel:ELL){
				try{
					StatementResult statementResult=session.run("match p = ((x)-[y]-(z:"+graphNodeLabel.getLabelName()+")) where id(x)="+node.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record, labelIdMap, typeIdMap);
						EEPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return EEPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodeRelationTypePath(ArrayList<GraphNode> nodes, ArrayList<GraphRelationshipType> RTL) {
		ArrayList<GraphPath> ERPS=new ArrayList<>();
		for(GraphNode node:nodes){
			for(GraphRelationshipType type:RTL){
				try{
					StatementResult statementResult=session.run("match p=((x)-[y:"+type.getTypeName()+"]-(z)) where id(x)="+node.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record, labelIdMap, typeIdMap);
						ERPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ERPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> sub_EL, ArrayList<GraphNode> EEL) {
		ArrayList<GraphPath> EPPS=new ArrayList<>();
		for(int i=0;i<sub_EL.size();i++){
			for(int j=0;j<EEL.size();j++){
				GraphNode g1=sub_EL.get(i);
				GraphNode g2=EEL.get(j);
				try{
					StatementResult statementResult=session.run("match p = ((x)-[*]->(y)) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match p = ((y)-[*]->(x)) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return p");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
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
	public ArrayList<GraphPathPattern> searchPattern(ArrayList<GraphNodeLabel> sub_ELL, ArrayList<GraphRelationshipType> sub_RTL) {
		ArrayList<GraphPathPattern> SResult=new ArrayList<>();
		for(GraphNodeLabel graphNodeLabel:sub_ELL){
			for(GraphRelationshipType graphRelationshipType:sub_RTL){
				try{
					StatementResult statementResult=session.run("match (x:"+graphNodeLabel.getLabelName()+")-[y:"+graphRelationshipType.getTypeName()+"]->(z) return x,y");
					if(statementResult.list().size()!=0){
						GraphPathPattern graphPathPattern=new GraphPathPattern();
						graphPathPattern.setLabel(graphNodeLabel);
						graphPathPattern.setRelationType(graphRelationshipType);
						graphPathPattern.setDirection(0);
						SResult.add(graphPathPattern);
					}
					statementResult=session.run("match (x:"+graphNodeLabel.getLabelName()+")<-[y:"+graphRelationshipType.getTypeName()+"]-(z) return x,y");
					if(statementResult.list().size()!=0){
						GraphPathPattern graphPathPattern=new GraphPathPattern();
						graphPathPattern.setLabel(graphNodeLabel);
						graphPathPattern.setRelationType(graphRelationshipType);
						graphPathPattern.setDirection(1);
						SResult.add(graphPathPattern);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return SResult;
	}

}
