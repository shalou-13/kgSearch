package com.kgSearch.service;

import java.util.ArrayList;

import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.TypeProperties;

public interface INeo4jDBService {
	
	public ArrayList<LabelProperties> selectAllLabels();
	
	public ArrayList<TypeProperties> selectAllTypes();
}
