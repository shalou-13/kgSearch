package com.kgSearch.pojo;

import java.util.ArrayList;

public class MatchTags {
	private boolean isVerbMatch;
	private boolean isAdjMatch;
	private boolean isNounMatch;
	private ArrayList<String> verbMatchList;
	private ArrayList<String> adjMatchList;
	private ArrayList<String> nounMatchList;
	private ArrayList<String> nounLabelMatchList;
	
	public boolean isVerbMatch() {
		return isVerbMatch;
	}
	public void setVerbMatch(boolean isVerbMatch) {
		this.isVerbMatch = isVerbMatch;
	}
	public boolean isAdjMatch() {
		return isAdjMatch;
	}
	public void setAdjMatch(boolean isAdjMatch) {
		this.isAdjMatch = isAdjMatch;
	}
	public boolean isNounMatch() {
		return isNounMatch;
	}
	public void setNounMatch(boolean isNounMatch) {
		this.isNounMatch = isNounMatch;
	}
	
	public ArrayList<String> getVerbMatchList() {
		return verbMatchList;
	}
	public void setVerbMatchList(ArrayList<String> verbMatchList) {
		this.verbMatchList = verbMatchList;
	}
	public ArrayList<String> getAdjMatchList() {
		return adjMatchList;
	}
	public void setAdjMatchList(ArrayList<String> adjMatchList) {
		this.adjMatchList = adjMatchList;
	}
	public ArrayList<String> getNounMatchList() {
		return nounMatchList;
	}
	public void setNounMatchList(ArrayList<String> nounMatchList) {
		this.nounMatchList = nounMatchList;
	}
	public void addVerbMatchTag(String tag){
		if(this.verbMatchList!=null)
			this.verbMatchList.add(tag);
	}
	
	public void addAdjMatchTag(String tag){
		if(this.adjMatchList!=null)
			this.adjMatchList.add(tag);
	}
	
	public void addNounMatchTag(String tag){
		if(this.nounMatchList!=null)
			this.nounMatchList.add(tag);
	}
	public ArrayList<String> getNounLabelMatchList() {
		return nounLabelMatchList;
	}
	public void setNounLabelMatchList(ArrayList<String> nounLabelMatchList) {
		this.nounLabelMatchList = nounLabelMatchList;
	}
	
}
