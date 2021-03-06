package com.kgSearch.method.impl;

import java.util.ArrayList;

import org.neo4j.driver.v1.Session;

import com.kgSearch.method.GraphHandler;
import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphPathPattern;
import com.kgSearch.pojo.GraphRelationshipType;
import com.kgSearch.service.impl.LabelService;
import com.kgSearch.service.impl.RelationTypeService;

public class ExampleGraphHandler extends GraphHandler {
	
	private Session session;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	
	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb(RelationTypeService relationTypeService, String graphID) {
		// TODO Auto-generated method stub
		return super.searchRelationshipTypeByVerb(relationTypeService, graphID);
	}
	
	@Override
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj(RelationTypeService relationTypeService, String graphID) {
		// TODO Auto-generated method stub
		return super.searchRelationshipTypeByAdj(relationTypeService, graphID);
	}
	
	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun(LabelService labelService, String graphID) {
		// TODO Auto-generated method stub
		return super.searchGraphNodeLabelByNoun(labelService, graphID);
	}
	
	@Override
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj(LabelService labelService, String graphID) {
		// TODO Auto-generated method stub
		return super.searchGraphNodeLabelByAdj(labelService, graphID);
	}
	
	@Override
	public ArrayList<GraphNode> searchGraphNodeByNoun() {
		// TODO Auto-generated method stub
		return super.searchGraphNodeByNoun();
	}
	
	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> EL) {
		// TODO Auto-generated method stub
		return super.searchNodePairPath(EL);
	}
	
	@Override
	public ArrayList<GraphPath> searchNodeLabelPath(ArrayList<GraphNode> nodes, ArrayList<GraphNodeLabel> ELL) {
		// TODO Auto-generated method stub
		return super.searchNodeLabelPath(nodes, ELL);
	}
	
	@Override
	public ArrayList<GraphPath> searchNodeRelationTypePath(ArrayList<GraphNode> nodes,
			ArrayList<GraphRelationshipType> RTL) {
		// TODO Auto-generated method stub
		return super.searchNodeRelationTypePath(nodes, RTL);
	}
	
	@Override
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> sub_EL, ArrayList<GraphNode> EEL) {
		// TODO Auto-generated method stub
		return super.searchNodePairPath(sub_EL, EEL);
	}
	
	@Override
	public ArrayList<GraphPathPattern> searchPattern(ArrayList<GraphNodeLabel> sub_ELL,
			ArrayList<GraphRelationshipType> sub_RTL) {
		// TODO Auto-generated method stub
		return super.searchPattern(sub_ELL, sub_RTL);
	}
	

	/*public static void main(String[] args) {
		ArrayList<String> verbList = new ArrayList<String>();
		ArrayList<String> adList = new ArrayList<String>();
		ArrayList<String> nounList = new ArrayList<String>();
		AuthToken token = new AuthToken();
		token.setUserName("neo4j");
		token.setPassword("123456");
		ExampleGraphHandler handler = new ExampleGraphHandler(verbList, adList, nounList, "neo4j", "bolt://localhost",token);
		handler.searchAction();
	}*/

}
