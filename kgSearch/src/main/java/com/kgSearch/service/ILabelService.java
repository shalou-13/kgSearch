package com.kgSearch.service;

import java.util.ArrayList;

import com.kgSearch.pojo.LabelProperties;

public interface ILabelService {
	
	public ArrayList<LabelProperties> getAllLabel();
    
    public ArrayList<LabelProperties> fuzzySelectLabelByString(String label);
    
    public ArrayList<LabelProperties> fuzzySelectPropertiesByString(String property);

}
