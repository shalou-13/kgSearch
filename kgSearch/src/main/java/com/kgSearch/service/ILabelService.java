package com.kgSearch.service;

import java.util.ArrayList;

import com.kgSearch.pojo.LabelProperties;

public interface ILabelService {
	
	public ArrayList<LabelProperties> getAllLabel();
    
    public ArrayList<LabelProperties> fuzzySelectLabelByString(String label);
    
    public ArrayList<LabelProperties> fuzzySelectPropertiesByString(String property);
    
    public ArrayList<LabelProperties> getLabelByGraphID(String graphID);
    
    public ArrayList<LabelProperties> fuzzySelectLabelByStringFromGraph(String keyword, String graphID);
    
    public ArrayList<LabelProperties> fuzzySelectPropertiesByStringFromGraph(String keyword, String graphID);

}
