package com.kgSearch.method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.kgSearch.pojo.GraphNode;
import com.kgSearch.pojo.GraphNodeLabel;
import com.kgSearch.pojo.GraphPath;
import com.kgSearch.pojo.GraphPathPattern;
import com.kgSearch.pojo.GraphRelationship;
import com.kgSearch.pojo.GraphRelationshipType;
import com.kgSearch.pojo.MatchTags;

public class GraphHandler {
	
	private ArrayList<String> verbList;
	private ArrayList<String> adList;
	private ArrayList<String> nounList;
	private ArrayList<GraphRelationshipType> RTL;
	private ArrayList<GraphNodeLabel> ELL;
	private ArrayList<GraphNode> EL;
	private ArrayList<GraphPath> TResult;
	private ArrayList<GraphNode> EEL;
	private ArrayList<GraphRelationshipType> sub_RTL;
	private ArrayList<GraphNodeLabel> sub_ELL;
	private ArrayList<GraphNode> sub_EL;
	private ArrayList<GraphPathPattern> SResult;
	
	public GraphHandler(ArrayList<String> verbList, ArrayList<String> adList, ArrayList<String> nounList){
		this.verbList = verbList;
		this.adList = adList;
		this.nounList = nounList;
	}
		
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb(){
		return null;
	}

	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj(){
		return null;
	}
	
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun(){
		return null;
	}

	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj(){
		return null;
	}
	
	public ArrayList<GraphNode> searchGraphNodeByNoun(){
		return null;
	}
	
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> EL){
		return null;
	}
	
	public ArrayList<GraphPath> searchNodeLabelPath(ArrayList<GraphNode> nodes, ArrayList<GraphNodeLabel> ELL){
		return null;
	}
	
	public ArrayList<GraphPath> searchNodeRelationTypePath(ArrayList<GraphNode> nodes, ArrayList<GraphRelationshipType> RTL){
		return null;
	}
	
	public ArrayList<GraphPath> searchNodePairPath(ArrayList<GraphNode> sub_EL,  ArrayList<GraphNode> EEL){
		return null;
	}
	
	public ArrayList<GraphPathPattern> searchPattern(ArrayList<GraphNodeLabel> sub_ELL, ArrayList<GraphRelationshipType> sub_RTL){
		return null;
	}
	
	public void searchAction(){
		ArrayList<GraphRelationshipType> vRTL = searchRelationshipTypeByVerb();
		ArrayList<GraphRelationshipType> aRTL = searchRelationshipTypeByAdj();
		ArrayList<GraphNodeLabel> nELL = searchGraphNodeLabelByNoun();
		ArrayList<GraphNodeLabel> aELL = searchGraphNodeLabelByAdj();
		ArrayList<GraphNode> EL = searchGraphNodeByNoun();
		setRTL(vRTL, aRTL);
		setELL(nELL, aELL);
		setEL(this.ELL, EL);
		if(this.EL!=null){
			ArrayList<GraphPath> EPPS = searchNodePairPath(this.EL);
			ArrayList<GraphPath> EEPS = searchNodeLabelPath(this.EL, this.ELL);
			ArrayList<GraphPath> ERPS = searchNodeRelationTypePath(this.EL, this.RTL);
			setTResult(EPPS, EEPS, ERPS);
		}
		setEELFromTResult(this.TResult, this.EL, this.ELL, this.EEL);
		setSub_ELSub_ELLSub_RTLFromTResult(this.TResult, this.EL, this.ELL, this.RTL);
		extendTResult();
		ArrayList<GraphPathPattern> SResult = searchPattern(this.sub_ELL, this.sub_RTL);
		if(SResult!=null)
			this.SResult = SResult;
		updateSub_ELLSub_RTLFromSResult();
		
		
	}
	
	//merge VRTL and ARTL
	public void setRTL(ArrayList<GraphRelationshipType> vRTL, ArrayList<GraphRelationshipType> aRTL){
		if(vRTL != null && aRTL != null){
			ArrayList<GraphRelationshipType> RTL = new ArrayList<GraphRelationshipType>();
			Map<Integer, GraphRelationshipType> temp = new HashMap<Integer, GraphRelationshipType>();
			for(GraphRelationshipType t1 : vRTL){
				temp.put(t1.getId(), t1);
			}
			for(GraphRelationshipType t2 : aRTL){
				if(temp.containsKey(t2.getId())){
					GraphRelationshipType t = temp.get(t2.getId());
					MatchTags tags = t.getMatchTags();
					ArrayList<String> tags2 = t2.getMatchTags().getAdjMatchList();
					tags.setAdjMatch(true);
					tags.setAdjMatchList(tags2);
					t.setWeight(temp.get(t2.getId()).getWeight() + tags2.size());
				}else{
					temp.put(t2.getId(), t2);
				}
			}
			for (GraphRelationshipType value : temp.values()) {  
				  RTL.add(value);
			}  
			this.RTL = RTL;
		}else if(vRTL!=null){
			this.RTL = vRTL;
		}else{
			this.RTL = aRTL;
		}
	}
	
	//merge NELL and AELL
	public void setELL(ArrayList<GraphNodeLabel> nELL, ArrayList<GraphNodeLabel> aELL){
		if(nELL != null && aELL != null){
			ArrayList<GraphNodeLabel> ELL = new ArrayList<GraphNodeLabel>();
			Map<Integer, GraphNodeLabel> temp = new HashMap<Integer, GraphNodeLabel>();
			for(GraphNodeLabel l1 : nELL){
				temp.put(l1.getId(), l1);
			}
			for(GraphNodeLabel l2 : aELL){
				if(temp.containsKey(l2.getId())){
					GraphNodeLabel l = temp.get(l2.getId());
					MatchTags tags = l.getMatchTags();
					ArrayList<String> tags2 = l2.getMatchTags().getAdjMatchList();
					tags.setAdjMatch(true);
					tags.setAdjMatchList(tags2);
					l.setWeight(temp.get(l2.getId()).getWeight() + tags2.size());
				}else{
					temp.put(l2.getId(), l2);
				}
			}
			for (GraphNodeLabel value : temp.values()) {  
				ELL.add(value);
			}  
			this.ELL= ELL;
		}else if(nELL!=null){
			this.ELL = nELL;
		}else{
			this.ELL = aELL;
		}
	}
	
	//merge ELL and EL
	public void setEL(ArrayList<GraphNodeLabel> ELL, ArrayList<GraphNode> EL){
		if(ELL != null && EL != null){
			Map<Integer, GraphNodeLabel> temp = new HashMap<Integer, GraphNodeLabel>();
			for(GraphNodeLabel l1 : ELL){
				temp.put(l1.getId(), l1);
			}
			Map<Integer,Boolean> exisit_label = new HashMap<Integer,Boolean>();
			for(GraphNode el : EL){
				ArrayList<Integer> labels = el.getLabels();
				for(Integer labelID : labels){
					if(temp.containsKey(labelID)){
						if(!exisit_label.containsKey(labelID))
							exisit_label.put(labelID, true);
						GraphNodeLabel l = temp.get(labelID);
						MatchTags tags = temp.get(labelID).getMatchTags();
						el.setWeight(el.getWeight() + l.getWeight());
						el.getMatchTag().setAdjMatch(tags.isAdjMatch());
						el.getMatchTag().setAdjMatchList(tags.getAdjMatchList());
						el.getMatchTag().setNounLabelMatchList(tags.getNounLabelMatchList());
					}
				}
			}
			ArrayList<GraphNodeLabel> EL_MIN = new ArrayList<GraphNodeLabel>();
			for (GraphNodeLabel label : ELL) {  
				  if(!exisit_label.containsKey(label.getId()))
					  EL_MIN.add(label);
			}  
			this.EL = EL;
			this.ELL = EL_MIN;
		}
	}
	
	//merge EPPS,EEPS,ERPS
	public void setTResult(ArrayList<GraphPath> EPPS, ArrayList<GraphPath> EEPS, ArrayList<GraphPath> ERPS) {
		if(EPPS==null){
			EPPS = new ArrayList<GraphPath>();
		}
		if(EEPS!=null)
			EPPS.addAll(EEPS);
		if(ERPS!=null)
			EPPS.addAll(ERPS);
		if(EPPS.size()!=0){
			TreeSet<GraphPath> set = new TreeSet<GraphPath>(new Mycompare());  
			set.addAll(EPPS);
			this.TResult = new ArrayList<GraphPath>(set);
		}
	}
	
	//generate sub_EL,sub_ELL,sub_RTL from TResult
	public void setSub_ELSub_ELLSub_RTLFromTResult(ArrayList<GraphPath> paths, ArrayList<GraphNode> EL, ArrayList<GraphNodeLabel> ELL, ArrayList<GraphRelationshipType> RTL){
		if(paths!=null){
			Map<Long, String> t1 = new HashMap<Long, String>();
			Map<Integer, String> t2 = new HashMap<Integer, String>();
			Map<Integer, String> t3 = new HashMap<Integer, String>();
			Map<Long, String> t4 = new HashMap<Long, String>();
			Map<Integer, String> t5 = new HashMap<Integer, String>();
			Map<Integer, String> t6 = new HashMap<Integer, String>();
			if(EL!=null){
				for(GraphNode gn : EL){
					t1.put(gn.getId(), "");
				}
			}
			if(ELL!=null){
				for(GraphNodeLabel gnl : ELL){
					t2.put(gnl.getId(), "");
				}
			}
			if(RTL!=null){
				for(GraphRelationshipType grt : RTL){
					t3.put(grt.getId(), "");
				}
			}
			for(GraphPath gp : paths){
				for(GraphNode gn1 : gp.getNodes()){
					if(t1.containsKey(gn1.getId()) && !t4.containsKey(gn1.getId()))
						t4.put(gn1.getId(), "");
					for(Integer labelID : gn1.getLabels()){
						if(t2.containsKey(labelID) && !t5.containsKey(labelID))
							t5.put(labelID, "");
					}
				}
				for(GraphRelationship gr : gp.getEdges()){
					if(t3.containsKey(gr.getType()) && !t6.containsKey(gr.getType()))
						t6.put(gr.getType(), "");
				}
			}
			if(!t6.isEmpty()){
				ArrayList<GraphRelationshipType> sub_RTL = new ArrayList<GraphRelationshipType>();
				for(GraphRelationshipType grt1 : this.RTL){
					if(!t6.containsKey(grt1.getId()))
						sub_RTL.add(grt1);
				}
				if(sub_RTL.size()!=0)
					this.sub_RTL = sub_RTL;
			}else{
				this.sub_RTL = RTL;
			}
			if(!t5.isEmpty()){
				ArrayList<GraphNodeLabel> sub_ELL = new ArrayList<GraphNodeLabel>();
				for(GraphNodeLabel gnl1 : this.ELL){
					if(!t5.containsKey(gnl1.getId()))
						sub_ELL.add(gnl1);
				}
				if(sub_ELL.size()!=0)
					this.sub_ELL = sub_ELL;
			}else{
				this.sub_ELL = ELL;
			}
			if(!t4.isEmpty()){
				ArrayList<GraphNode> sub_EL = new ArrayList<GraphNode>();
				for(GraphNode gn2 : this.EL){
					if(!t4.containsKey(gn2.getId()))
						sub_EL.add(gn2);
				}
				if(sub_EL.size()!=0)
					this.sub_EL = sub_EL;
			}else{
				this.sub_EL = EL;
			}
		}
	}
	
		//generate EEL from TResult
		public void setEELFromTResult(ArrayList<GraphPath> paths, ArrayList<GraphNode> EL, ArrayList<GraphNodeLabel> ELL, ArrayList<GraphNode> EEL){
			if(paths!=null){
				Map<Long, String> t1 = new HashMap<Long, String>();
				Map<Integer, String> t2 = new HashMap<Integer, String>();
				Map<Long, String> t3 = new HashMap<Long, String>();
				if(EL!=null){
					for(GraphNode gn : EL){
						t1.put(gn.getId(), "");
					}
				}
				if(ELL!=null){
					for(GraphNodeLabel gnl : ELL){
						t2.put(gnl.getId(), "");
					}
				}
				if(EEL!=null){
					for(GraphNode gn : EEL){
						t3.put(gn.getId(), "");
					}
				}
				ArrayList<GraphNode> new_EEL = new ArrayList<GraphNode>();
				for(GraphPath gp : this.TResult){
					for(GraphNode gn1 : gp.getNodes()){
						if(t1.containsKey(gn1.getId()))
							continue;
						if(t3.containsKey(gn1.getId()))
							continue;
						for(Integer labelID : gn1.getLabels()){
							if(!t2.containsKey(labelID))
								break;
						}
						new_EEL.add(gn1);
						t1.put(gn1.getId(), "");
					}
				}
				if(new_EEL.size()!=0)
					this.EEL = new_EEL;
			}
		}
	
		//extend TResult
		public void extendTResult(){
			if(this.EEL!=null){
				ArrayList<GraphPath> EPPS = new ArrayList<GraphPath>();
				ArrayList<GraphPath> EEPS = new ArrayList<GraphPath>();
				ArrayList<GraphPath> ERPS = new ArrayList<GraphPath>();
				if(this.sub_EL!=null){
					EPPS = searchNodePairPath(this.sub_EL,  this.EEL);
				}
				if(this.sub_ELL!=null){
					EEPS = searchNodeLabelPath(this.EEL, this.sub_ELL);
				}
				if(this.sub_RTL!=null){
					ERPS = searchNodeRelationTypePath(this.EEL, this.sub_RTL);
				}
				EPPS.addAll(EEPS);
				EPPS.addAll(ERPS);
				if(EPPS.size()!=0){
					TreeSet<GraphPath> set = new TreeSet<GraphPath>(new Mycompare());  
					set.addAll(EPPS);
					EPPS = new ArrayList<GraphPath>(set);
					setEELFromTResult(EPPS, this.sub_EL, this.sub_ELL, this.EEL);
					setSub_ELSub_ELLSub_RTLFromTResult(EPPS, this.sub_EL, this.sub_ELL, this.sub_RTL);
					this.TResult.addAll(EPPS);
					extendTResult();
				}
			}
		}
		
		//update sub_ELL and sub_RTL from SResult
		public void updateSub_ELLSub_RTLFromSResult(){
			if(this.SResult!=null){
				Map<Integer, String> t1 = new HashMap<Integer, String>();
				Map<Integer, String> t2 = new HashMap<Integer, String>();
				for(GraphNodeLabel gnl : this.sub_ELL){
					t2.put(gnl.getId(), "");
				}
				for(GraphRelationshipType grt : this.sub_RTL){
					t1.put(grt.getId(), "");
				}
				Map<Integer, String> t3 = new HashMap<Integer, String>();
				Map<Integer, String> t4 = new HashMap<Integer, String>();
				for(GraphPathPattern gpp : this.SResult){
					if(t2.containsKey(gpp.getLabel().getId()) && !t3.containsKey(gpp.getLabel().getId()))
						t3.put(gpp.getLabel().getId(), "");
					if(t1.containsKey(gpp.getRelationType().getId()) && !t4.containsKey(gpp.getRelationType().getId()))
						t4.put(gpp.getRelationType().getId(), "");
				}
				if(!t3.isEmpty()){
					ArrayList<GraphNodeLabel> sub_ELL = new ArrayList<GraphNodeLabel>();
					for(GraphNodeLabel gnl1 : this.sub_ELL){
						if(!t3.containsKey(gnl1.getId()))
							sub_ELL.add(gnl1);
					}
					if(sub_ELL.size()!=0)
						this.sub_ELL = sub_ELL;
				}
				if(!t4.isEmpty()){
					ArrayList<GraphRelationshipType> sub_RTL = new ArrayList<GraphRelationshipType>();
					for(GraphRelationshipType grt1 : this.sub_RTL){
						if(!t4.containsKey(grt1.getId()))
							sub_RTL.add(grt1);
					}
					if(sub_RTL.size()!=0)
						this.sub_RTL = sub_RTL;
				}
			}
		}
		
}
//自定义一个比较器  
class Mycompare implements Comparator<GraphPath>{  

	@Override
	public int compare(GraphPath o1, GraphPath o2) {
		GraphPath gp1 = o1;  
		GraphPath gp2 = o2;
		int i = 1;
		if(gp1.compare(gp2) || gp1.contains(gp2))
		  i = 0;
	    return i;  
	}  
  }  