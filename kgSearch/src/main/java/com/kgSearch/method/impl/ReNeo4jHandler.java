package com.kgSearch.method.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgSearch.dao.LabelPropertiesMapper;
import com.kgSearch.dao.TypePropertiesMapper;
import com.kgSearch.method.GraphHandler;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphPathPattern;
import com.kgSearch.pojo.GraphRelationshipType;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.MatchTags;
import com.kgSearch.pojo.TypeProperties;

@Service
public class ReNeo4jHandler extends GraphHandler{

	@Autowired
	private LabelPropertiesMapper labelPropertiesService;
	
	@Autowired
	private TypePropertiesMapper typePropertiesService;
	
	private org.neo4j.driver.v1.Session session;
	private Map<String,Integer> labelIdMap;
	private Map<String,Integer> typeIdMap;
	
	public void initSession(Driver driver){
		session=driver.session();
	}
	
	public void initIdMap(){
		labelIdMap=new HashMap<>();
		typeIdMap=new HashMap<>();
		
		ArrayList<LabelProperties> AllLabel=(ArrayList<LabelProperties>)labelPropertiesService.selectAll();
		ArrayList<TypeProperties> AllType=(ArrayList<TypeProperties>)typePropertiesService.selectAll();
		
		for(LabelProperties Alabel:AllLabel){
			labelIdMap.put(Alabel.getLabel(),Alabel.getId());
		}
		for(TypeProperties Atype:AllType){
			typeIdMap.put(Atype.getType(),Atype.getId());
		}
	}
	
	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb() {
		ArrayList<GraphRelationshipType> VRTL=new ArrayList<>();
		Set<Integer> idOfVRTL=new HashSet<>();
		for(String Averb:this.getVerbList()){
			ArrayList<TypeProperties> searchResult=(ArrayList<TypeProperties>)typePropertiesService.fuzzySelectTypeByString(Averb);
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
		System.out.println("VRTL");
		for(GraphRelationshipType graphRelationshipType:VRTL){
			System.out.print(graphRelationshipType.getTypeName()+" ");
		}
		System.out.print("\n");
		return VRTL;
	}

	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj() {
		ArrayList<GraphRelationshipType> ARTL=new ArrayList<>();
		Set<Integer> idOfARTL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			ArrayList<TypeProperties> searchResult=(ArrayList<TypeProperties>)typePropertiesService.fuzzySelectPropertiesByString(Aadj);
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
		System.out.println("ARTL:");
		for(GraphRelationshipType graphRelationshipType:ARTL){
			System.out.print(graphRelationshipType.getTypeName()+" ");
		}
		System.out.print("\n");
		return ARTL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun() {
		ArrayList<GraphNodeLabel> NELL=new ArrayList<>();	
		Set<Integer> idOfNELL=new HashSet<>();
		for(String Anoun:this.getNounList()){
			ArrayList<LabelProperties> searchResult=(ArrayList<LabelProperties>)labelPropertiesService.fuzzySelectLabelByString(Anoun);
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
		System.out.println("NELL:");
		for(GraphNodeLabel graphNodeLabel:NELL){
			System.out.print(graphNodeLabel.getLabelName()+" ");
		}
		System.out.print("\n");
		return NELL;
	}

	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj() {
		ArrayList<GraphNodeLabel> AELL=new ArrayList<>();
		Set<Integer> idOfAELL=new HashSet<>();
		for(String Aadj:this.getAdList()){
			ArrayList<LabelProperties> searchResult=(ArrayList<LabelProperties>)labelPropertiesService.fuzzySelectPropertiesByString(Aadj);
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
		System.out.println("AELL:");
		for(GraphNodeLabel graphNodeLabel:AELL){
			System.out.print(graphNodeLabel.getLabelName()+" ");
		}
		System.out.print("\n");
		return AELL;
	}

	@Override
	public ArrayList<GraphNode> searchGraphNodeByNoun() {
		// TODO Auto-generated method stub
		ArrayList<GraphNode> NEL=new ArrayList<>();
		Set<Long> idOfNELL=new HashSet<>();
		ArrayList<LabelProperties> all=(ArrayList<LabelProperties>)labelPropertiesService.selectAll();
		for(LabelProperties Alp:all){
			for(String Anoun:this.getNounList()){
				boolean flag=false;
				String where="";
				if(Alp.getProperties()!=null){
					for(String Aproperty:Alp.getProperties().split(",")){
						if(flag==true){
							where+=" or x."+Aproperty+" contains "+"'"+Anoun+"'";
						}
						else{
							where+=" x."+Aproperty+" contains "+"'"+Anoun+"'";
							flag=true;
						}
					}
				}
				if(flag==true){
					try{
						StatementResult statementResult=session.run("match (x:"+Alp.getLabel()+") where "+where+" return x");
						while(statementResult.hasNext()){
							Record record=statementResult.next();
							Node node=record.get(0).asNode();
							if(idOfNELL.contains(node.id())==false){
								ArrayList<Integer> labels=new ArrayList<>();
								Map<String,Object> properties=new HashMap<>();
								MatchTags matchTags=new MatchTags();
								for(String Alabel:node.labels()){
									labels.add(labelIdMap.get(Alabel));
								}
								for(String key:node.keys()){
									properties.put(key,node.get(key));
								}
								matchTags.setNounMatch(true);
								matchTags.setNounMatchList(new ArrayList<>());
								matchTags.addNounMatchTag(Anoun);
								GraphNode graphNode=new GraphNode();
								graphNode.setId(node.id());
								graphNode.setWeight(1);
								graphNode.setLabels(labels);
								graphNode.setMatchTag(matchTags);
								graphNode.setProperties(properties);
								idOfNELL.add(node.id());
								NEL.add(graphNode);
							}
							else{
								for(GraphNode graphNode:NEL){
									if(graphNode.getId()==node.id()){
										graphNode.setWeight(graphNode.getWeight()+1);
										graphNode.getMatchTag().addNounMatchTag(Anoun);
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("NEL:");
		for(GraphNode graphNode:NEL){
			System.out.print(graphNode.getId()+" ");
		}
		System.out.print("\n");
		return NEL;
	}

	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> EL) {
		ArrayList<GraphPath> EPPS=new ArrayList<>(); 
		for(int i=0;i<EL.size();i++){
			for(int j=i+1;j<EL.size();j++){
				GraphNode g1=EL.get(i);
				GraphNode g2=EL.get(j);
				try{
					StatementResult statementResult=session.run("match (x),(y) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return ((x)-[*]->(y))");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match (x),(y) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return ((x)<-[*]-(y))");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setRevPath(record,labelIdMap,typeIdMap);
						EPPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("EPPS:");
		for(GraphPath graphPath:EPPS){
			for(GraphNode graphNode:graphPath.getNodes()){
				System.out.print(graphNode.getId()+" ");
			}
		}
		System.out.print("\n");
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
						graphPath.setOnePath(record, labelIdMap, typeIdMap);
						EEPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("EEPS:");
		for(GraphPath graphPath:EEPS){
			for(GraphNode graphNode:graphPath.getNodes()){
				System.out.print(graphNode.getId()+" ");
			}
		}
		System.out.print("\n");
		return EEPS;
	}

	@Override
	public ArrayList<GraphPath> searchNodeRelationTypePath(ArrayList<GraphNode> nodes,
			ArrayList<GraphRelationshipType> RTL) {
		ArrayList<GraphPath> ERPS=new ArrayList<>();
		for(GraphNode node:nodes){
			for(GraphRelationshipType type:RTL){
				try{
					StatementResult statementResult=session.run("match (x)-[y:"+type.getTypeName()+"]-(z) where id(x)="+node.getId()+" return x,y,z");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setOnePath(record, labelIdMap, typeIdMap);
						ERPS.add(graphPath);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("ERPS:");
		for(GraphPath graphPath:ERPS){
			for(GraphNode graphNode:graphPath.getNodes()){
				System.out.print(graphNode.getId()+" ");
			}
		}
		System.out.print("\n");
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
					StatementResult statementResult=session.run("match (x),(y) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return ((x)-[*]->(y))");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setPath(record,labelIdMap,typeIdMap);
						EPPS.add(graphPath);
					}
					statementResult=session.run("match (x),(y) where id(x)="+g1.getId()+" and id(y)="+g2.getId()+" return ((x)<-[*]-(y))");
					while(statementResult.hasNext()){
						Record record=statementResult.next();
						GraphPath graphPath=new GraphPath();
						graphPath.setRevPath(record,labelIdMap,typeIdMap);
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
			for(GraphNode graphNode:graphPath.getNodes()){
				System.out.print(graphNode.getId()+" ");
			}
		}
		System.out.print("\n");
		return EPPS;
	}

	@Override
	public ArrayList<GraphPathPattern> searchPattern(ArrayList<GraphNodeLabel> sub_ELL,
			ArrayList<GraphRelationshipType> sub_RTL) {
		// TODO Auto-generated method stub
		if(sub_ELL==null||sub_RTL==null){
			System.out.println("sub_ELL or sub_RTL=null");
			return null;
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
						SResult.add(graphPathPattern);
						
					}
					statementResult=session.run("match (x:"+graphNodeLabel.getLabelName()+")<-[y:"+graphRelationshipType.getTypeName()+"]-(z) return x,y");
					while(statementResult.hasNext()){
						@SuppressWarnings("unused")
						Record record=statementResult.next();
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
