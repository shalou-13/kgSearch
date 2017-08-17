package com.kgSearch.service;

import java.util.ArrayList;

import com.kgSearch.pojo.TypeProperties;

public interface IRelationTypeService {
	
	public ArrayList<TypeProperties> GetAllRelationType();
	
	public ArrayList<TypeProperties> fuzzySelectTypeByString(String type);
    
    public ArrayList<TypeProperties> fuzzySelectPropertiesByString(String property);

}
