package com.kgSearch.method;

import java.util.ArrayList;
import java.util.Collections;
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
import com.kgSearch.service.impl.LabelService;
import com.kgSearch.service.impl.RelationTypeService;


public class GraphHandler {
	
	protected ArrayList<String> verbList;
	protected ArrayList<String> adList;
	protected ArrayList<String> nounList;
	private ArrayList<GraphRelationshipType> RTL;
	private ArrayList<GraphNodeLabel> ELL;
	private ArrayList<GraphNode> EL;
	private ArrayList<GraphPath> TResult;
	private ArrayList<GraphNode> EEL;
	private ArrayList<GraphRelationshipType> sub_RTL;
	private ArrayList<GraphNodeLabel> sub_ELL;
	private ArrayList<GraphNode> sub_EL;
	private ArrayList<GraphPathPattern> SResult;
	private double weight;
	
	
	public ArrayList<GraphRelationshipType> searchRelationshipTypeByVerb(RelationTypeService relationTypeService, String graphID){
		return null;
	}

	public ArrayList<GraphRelationshipType> searchRelationshipTypeByAdj(RelationTypeService relationTypeService, String graphID){
		return null;
	}
	
	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByNoun(LabelService labelService, String graphID){
		return null;
	}

	public ArrayList<GraphNodeLabel> searchGraphNodeLabelByAdj(LabelService labelService, String graphID){
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
	
	public void searchAction(LabelService labelService, RelationTypeService relationTypeService, String graphID){
		ArrayList<GraphRelationshipType> vRTL = searchRelationshipTypeByVerb(relationTypeService, graphID);
		ArrayList<GraphRelationshipType> aRTL = searchRelationshipTypeByAdj(relationTypeService, graphID);
		ArrayList<GraphNodeLabel> nELL = searchGraphNodeLabelByNoun(labelService, graphID);
		ArrayList<GraphNodeLabel> aELL = searchGraphNodeLabelByAdj(labelService, graphID);
		ArrayList<GraphNode> EL = searchGraphNodeByNoun();
		setRTL(vRTL, aRTL);
		setELL(nELL, aELL);
		setEL(this.ELL, EL);
		if(this.EL!=null){
			ArrayList<GraphPath> EPPS = null;
			ArrayList<GraphPath> EEPS = null;
			ArrayList<GraphPath> ERPS = null;
			if(this.EL.size()>1)
				EPPS = searchNodePairPath(this.EL);
			if(this.ELL!=null)
				EEPS = searchNodeLabelPath(this.EL, this.ELL);
			if(this.RTL!=null)
				ERPS = searchNodeRelationTypePath(this.EL, this.RTL);
			setTResult(EPPS, EEPS, ERPS);
		}
		setEELFromTResult(this.TResult, this.EL, this.ELL, this.EEL);
		setSub_ELSub_ELLSub_RTLFromTResult(this.TResult, this.EL, this.ELL, this.RTL);
		extendTResult();
		if(this.sub_ELL!=null && this.sub_RTL!=null){
			ArrayList<GraphPathPattern> SResult = searchPattern(this.sub_ELL, this.sub_RTL);
			if(SResult!=null && SResult.size()!=0)
				this.SResult = SResult;
			updateSub_ELLSub_RTLFromSResult();
		}
		updateTResultWeight();
		updateSResultWeight();
		computeGraphResultWeight();
	}
	
	//merge VRTL and ARTL
	public void setRTL(ArrayList<GraphRelationshipType> vRTL, ArrayList<GraphRelationshipType> aRTL){
		if(vRTL != null && vRTL.size()!=0 && aRTL != null && aRTL.size()!=0){
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
		}else if(vRTL!=null && vRTL.size()!=0){
			this.RTL = vRTL;
		}else if(aRTL!=null && aRTL.size()!=0){
			this.RTL = aRTL;
		}
	}
	
	//merge NELL and AELL
	public void setELL(ArrayList<GraphNodeLabel> nELL, ArrayList<GraphNodeLabel> aELL){
		if(nELL != null && nELL.size()!=0 && aELL != null && aELL.size()!=0){
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
		}else if(nELL!=null && nELL.size()!=0){
			this.ELL = nELL;
		}else if(aELL!=null && aELL.size()!=0){
			this.ELL = aELL;
		}
	}
	
	//merge ELL and EL
	public void setEL(ArrayList<GraphNodeLabel> ELL, ArrayList<GraphNode> EL){
		if(ELL != null && EL != null && EL.size()!=0){
			Map<Integer, GraphNodeLabel> temp = new HashMap<Integer, GraphNodeLabel>();
			for(GraphNodeLabel l1 : ELL){
				temp.put(l1.getId(), l1);
			}
			Map<Integer,Boolean> exisit_label = new HashMap<Integer,Boolean>();
			for(GraphNode el : EL){
				HashMap<Integer, String> labels = el.getLabels();
				for(Integer labelID : labels.keySet()){
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
			ArrayList<GraphNodeLabel> ELL_MIN = new ArrayList<GraphNodeLabel>();
			for (GraphNodeLabel label : ELL) {  
				  if(!exisit_label.containsKey(label.getId()))
					  ELL_MIN.add(label);
			}  
			this.EL = EL;
			this.ELL = ELL_MIN;
		}else if(EL != null && EL.size()!=0){
			this.EL = EL;
		}
	}
	
	//merge EPPS,EEPS,ERPS
	public void setTResult(ArrayList<GraphPath> EPPS, ArrayList<GraphPath> EEPS, ArrayList<GraphPath> ERPS) {
		ArrayList<GraphPath> TResult =  new ArrayList<GraphPath>();
		if(EPPS!=null && EPPS.size()!=0)
			TResult.addAll(EPPS);
		if(EEPS!=null && EEPS.size()!=0)
			TResult.addAll(EEPS);
		if(ERPS!=null && ERPS.size()!=0)
			TResult.addAll(ERPS);
		if(TResult.size()!=0){
			TreeSet<GraphPath> set = new TreeSet<GraphPath>(new Mycompare());  
			set.addAll(TResult);
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
					for(Integer labelID : gn1.getLabels().keySet()){
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
				ArrayList<GraphRelationshipType> subRTL = new ArrayList<GraphRelationshipType>();
				for(GraphRelationshipType grt1 : RTL){
					if(!t6.containsKey(grt1.getId()))
						subRTL.add(grt1);
				}
				if(subRTL.size()!=0)
					this.sub_RTL = subRTL;
			}else if(RTL!=null){
				this.sub_RTL = RTL;
			}
			if(!t5.isEmpty()){
				ArrayList<GraphNodeLabel> subELL = new ArrayList<GraphNodeLabel>();
				for(GraphNodeLabel gnl1 : ELL){
					if(!t5.containsKey(gnl1.getId()))
						subELL.add(gnl1);
				}
				if(subELL.size()!=0)
					this.sub_ELL = subELL;
			}else if(ELL!=null){
				this.sub_ELL = ELL;
			}
			if(!t4.isEmpty()){
				ArrayList<GraphNode> subEL = new ArrayList<GraphNode>();
				for(GraphNode gn2 : EL){
					if(!t4.containsKey(gn2.getId()))
						subEL.add(gn2);
				}
				if(subEL.size()!=0)
					this.sub_EL = subEL;
			}else if(EL!=null){
				this.sub_EL = EL;
			}
		}else{
			if(EL!=null)
				this.sub_EL = EL;
			if(ELL!=null)
				this.sub_ELL = ELL;
			if(RTL!=null)
				this.sub_RTL = RTL;
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
				for(GraphPath gp : paths){
					for(GraphNode gn1 : gp.getNodes()){
						if(t1.containsKey(gn1.getId()))
							continue;
						if(t3.containsKey(gn1.getId()))
							continue;
						boolean haveLabel = false;
						for(Integer labelID : gn1.getLabels().keySet()){
							if(t2.containsKey(labelID)){
								haveLabel = true;
								break;
							}
						}
						if(!haveLabel)
							new_EEL.add(gn1);
						if(!t1.containsKey(gn1.getId()))
							t1.put(gn1.getId(), "");
					}
				}
				if(new_EEL.size()!=0){
					this.EEL = new_EEL;
				}else{
					this.EEL = null;
				}
			}
		}
	
		//extend TResult
		public void extendTResult(){
			if(this.EEL!=null){
				ArrayList<GraphPath> ex_TResult = new ArrayList<GraphPath>();
				ArrayList<GraphPath> EPPS = null;
				ArrayList<GraphPath> EEPS = null;
				ArrayList<GraphPath> ERPS = null;
				if(this.sub_EL!=null){
					EPPS = searchNodePairPath(this.sub_EL,  this.EEL);
					if(EPPS!=null && EPPS.size()!=0)
						ex_TResult.addAll(EPPS);
				}
				if(this.sub_ELL!=null){
					EEPS = searchNodeLabelPath(this.EEL, this.sub_ELL);
					if(EEPS!=null && EEPS.size()!=0)
						ex_TResult.addAll(EEPS);
				}
				if(this.sub_RTL!=null){
					ERPS = searchNodeRelationTypePath(this.EEL, this.sub_RTL);
					if(ERPS!=null && ERPS.size()!=0)
						ex_TResult.addAll(ERPS);
				}
				if(ex_TResult.size()!=0){
					TreeSet<GraphPath> set = new TreeSet<GraphPath>(new Mycompare());  
					set.addAll(ex_TResult);
					ex_TResult = new ArrayList<GraphPath>(set);
					setEELFromTResult(ex_TResult, this.sub_EL, this.sub_ELL, this.EEL);
					setSub_ELSub_ELLSub_RTLFromTResult(ex_TResult, this.sub_EL, this.sub_ELL, this.sub_RTL);
					this.TResult.addAll(ex_TResult);
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
				if(t3.size()!=0){
					ArrayList<GraphNodeLabel> subELL = new ArrayList<GraphNodeLabel>();
					for(GraphNodeLabel gnl1 : this.sub_ELL){
						if(!t3.containsKey(gnl1.getId()))
							subELL.add(gnl1);
					}
					if(subELL.size()!=0){
						this.sub_ELL = subELL;
					}else{
						this.sub_ELL = null;
					}
				}
				if(t4.size()!=0){
					ArrayList<GraphRelationshipType> subRTL = new ArrayList<GraphRelationshipType>();
					for(GraphRelationshipType grt1 : this.sub_RTL){
						if(!t4.containsKey(grt1.getId()))
							subRTL.add(grt1);
					}
					if(subRTL.size()!=0){
						this.sub_RTL = subRTL;
					}else{
						this.sub_RTL = null;
					}
				}
			}
		}
		
		
		//update TResult's node and relation weight;
		public void updateTResultWeight(){
			if(this.TResult!=null){
				Map<Integer, GraphRelationshipType> rtl_map = new HashMap<Integer, GraphRelationshipType>();
				Map<Integer, GraphNodeLabel> ell_map = new HashMap<Integer, GraphNodeLabel>();
				Map<Long, GraphNode> el_map = new HashMap<Long, GraphNode>();
				if(this.RTL!=null){
					for(GraphRelationshipType rtl : this.RTL){
						rtl_map.put(rtl.getId(), rtl);
					}
				}
				if(this.ELL!=null){
					for(GraphNodeLabel ell : this.ELL){
						ell_map.put(ell.getId(), ell);
					}
				}
				if(this.EL!=null){
					for(GraphNode el  : this.EL){
						el_map.put(el.getId(), el);
					}
				}
				
				for(GraphPath gp : this.TResult){
					for(GraphNode gn : gp.getNodes()){
						if(el_map.containsKey(gn.getId())){
							gn.setMatchTag(el_map.get(gn.getId()).getMatchTag());
							gn.setWeight(el_map.get(gn.getId()).getWeight());
						}
						if(this.ELL!=null){
							for( int gnl : gn.getLabels().keySet()){
								if(ell_map.containsKey(gnl)){
									if(gn.getMatchTag()!=null){
										gn.getMatchTag().setNounLabelMatchList(ell_map.get(gnl).getMatchTags().getNounLabelMatchList());
										gn.getMatchTag().setAdjMatchList(ell_map.get(gnl).getMatchTags().getAdjMatchList());
										gn.setWeight(gn.getWeight() + ell_map.get(gnl).getWeight());
									}else{
										gn.setMatchTag(ell_map.get(gnl).getMatchTags());
										gn.setWeight(ell_map.get(gnl).getWeight());
									}
								}
							}
						}
						gp.setWeight(gp.getWeight() + gn.getWeight());
					}
					if(this.RTL!=null){
						for(GraphRelationship gr : gp.getEdges()){
							if(rtl_map.containsKey(gr.getType())){
								gr.setMatchTags(rtl_map.get(gr.getType()).getMatchTags());
								gr.setWeight(rtl_map.get(gr.getType()).getWeight());
							}
							gp.setWeight(gp.getWeight() + gr.getWeight());
						}
					}
				}
				Collections.sort(this.TResult, new CompareGraphPathByWeight());
			}
		}
		
		//update SResult's  weight;
		public void updateSResultWeight(){
			if(this.SResult!=null){
				for(GraphPathPattern gpp : this.SResult){
					gpp.setWeight(gpp.getLabel().getWeight() + gpp.getRelationType().getWeight());
				}
				Collections.sort(this.SResult, new CompareGraphPathPatternByWeight());
			}
		}
		
		// compute graph search result weight
		public void computeGraphResultWeight(){
			double allTResultWeight = 0;
			double allSResultWeight = 0;
			double allSub_ELLWeight = 0;
			double allSub_ELWeight = 0;
			double allSub_RTLWeight = 0;
			double allWeight = 0;
			if(this.TResult!=null){
				Map<Long, GraphNode> el_map = new HashMap<Long, GraphNode>();
				Map<Long, GraphRelationship> rtl_map = new HashMap<Long, GraphRelationship>();
				for(GraphPath gp : this.TResult){
					for(GraphNode gn : gp.getNodes()){
						if(!el_map.containsKey(gn.getId()))
							el_map.put(gn.getId(), gn);
						}
					for(GraphRelationship gr : gp.getEdges()){
						if(!rtl_map.containsKey(gr.getType()))
							rtl_map.put(gr.getId(), gr);
					}
				}
				for(Long key : el_map.keySet()){
					allTResultWeight = allTResultWeight + el_map.get(key).getWeight();
				}
				for(Long key : rtl_map.keySet()){
					allTResultWeight = allTResultWeight + rtl_map.get(key).getWeight();
				}
			}
			if(this.SResult!=null){
				Map<Integer, GraphRelationshipType> rtl_map = new HashMap<Integer, GraphRelationshipType>();
				Map<Integer, GraphNodeLabel> ell_map = new HashMap<Integer, GraphNodeLabel>();
				for(GraphPathPattern gpp : this.SResult){
					if(!ell_map.containsKey(gpp.getLabel().getId()))
						ell_map.put(gpp.getLabel().getId(), gpp.getLabel());
					if(!rtl_map.containsKey(gpp.getRelationType().getId()))
						rtl_map.put(gpp.getRelationType().getId(), gpp.getRelationType());
				}
				for(Integer key : rtl_map.keySet()){
					allSResultWeight = allSResultWeight + rtl_map.get(key).getWeight();
				}
				for(Integer key : ell_map.keySet()){
					allSResultWeight = allSResultWeight + ell_map.get(key).getWeight();
				}
			}
			if(this.sub_ELL!=null){
				for(GraphNodeLabel gnl : this.sub_ELL){
					allSub_ELLWeight = allSub_ELLWeight + gnl.getWeight();
				}
			}
			if(this.sub_EL!=null){
				for(GraphNode gn : this.sub_EL){
					allSub_ELWeight = allSub_ELWeight + gn.getWeight();
				}
			}
			if(this.sub_RTL!=null){
				for(GraphRelationshipType grt : this.sub_RTL){
					allSub_RTLWeight = allSub_RTLWeight + grt.getWeight();
				}
			}
			allWeight = allTResultWeight + allSResultWeight + allSub_ELLWeight + allSub_ELWeight + allSub_RTLWeight;
			if(allWeight!=0){
				allTResultWeight= allTResultWeight/allWeight;
				allSResultWeight= allSResultWeight/allWeight;
				allSub_ELLWeight= allSub_ELLWeight/allWeight;
				allSub_ELWeight= allSub_ELWeight/allWeight;
				allSub_RTLWeight= allSub_RTLWeight/allWeight;
			}
			this.weight = 0.8*(allTResultWeight + allSub_ELWeight) + 0.2*(0.8*allSResultWeight + 0.2*(allSub_ELLWeight + allSub_RTLWeight));
		}
		
		public ArrayList<String> getVerbList() {
			return verbList;
		}

		public void setVerbList(ArrayList<String> verbList) {
			this.verbList = verbList;
		}

		public ArrayList<String> getAdList() {
			return adList;
		}

		public void setAdList(ArrayList<String> adList) {
			this.adList = adList;
		}

		public ArrayList<String> getNounList() {
			return nounList;
		}

		public void setNounList(ArrayList<String> nounList) {
			this.nounList = nounList;
		}

		public ArrayList<GraphRelationshipType> getRTL() {
			return RTL;
		}

		public void setRTL(ArrayList<GraphRelationshipType> rTL) {
			RTL = rTL;
		}

		public ArrayList<GraphNodeLabel> getELL() {
			return ELL;
		}

		public void setELL(ArrayList<GraphNodeLabel> eLL) {
			ELL = eLL;
		}

		public ArrayList<GraphNode> getEL() {
			return EL;
		}

		public void setEL(ArrayList<GraphNode> eL) {
			EL = eL;
		}

		public ArrayList<GraphPath> getTResult() {
			return TResult;
		}

		public void setTResult(ArrayList<GraphPath> tResult) {
			TResult = tResult;
		}

		public ArrayList<GraphNode> getEEL() {
			return EEL;
		}

		public void setEEL(ArrayList<GraphNode> eEL) {
			EEL = eEL;
		}

		public ArrayList<GraphRelationshipType> getSub_RTL() {
			return sub_RTL;
		}

		public void setSub_RTL(ArrayList<GraphRelationshipType> sub_RTL) {
			this.sub_RTL = sub_RTL;
		}

		public ArrayList<GraphNodeLabel> getSub_ELL() {
			return sub_ELL;
		}

		public void setSub_ELL(ArrayList<GraphNodeLabel> sub_ELL) {
			this.sub_ELL = sub_ELL;
		}

		public ArrayList<GraphNode> getSub_EL() {
			return sub_EL;
		}

		public void setSub_EL(ArrayList<GraphNode> sub_EL) {
			this.sub_EL = sub_EL;
		}

		public ArrayList<GraphPathPattern> getSResult() {
			return SResult;
		}

		public void setSResult(ArrayList<GraphPathPattern> sResult) {
			SResult = sResult;
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
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


class CompareGraphPathByWeight implements Comparator<GraphPath>{  

	@Override
	public int compare(GraphPath o1, GraphPath o2) {
	    return (o2.getWeight()<o1.getWeight()?-1:(o2.getWeight()==o1.getWeight()?0:1));  
	}  
  }  

class CompareGraphPathPatternByWeight implements Comparator<GraphPathPattern>{  

	@Override
	public int compare(GraphPathPattern o1, GraphPathPattern o2) {
	    return (o2.getWeight()<o1.getWeight()?-1:(o2.getWeight()==o1.getWeight()?0:1));  
	}  
  }  