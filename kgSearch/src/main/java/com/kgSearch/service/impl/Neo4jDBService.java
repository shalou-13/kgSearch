package com.kgSearch.service.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kgSearch.dao.LabelPropertiesMapper;
import com.kgSearch.dao.TypePropertiesMapper;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.INeo4jDBService;

@Service
public class Neo4jDBService implements INeo4jDBService{

	@Resource
	private TypePropertiesMapper typePropertiesDao;
	
	@Resource
	private LabelPropertiesMapper labelPropertiesDao;
	
	@Override
	public ArrayList<LabelProperties> selectAllLabels() {
		return (ArrayList<LabelProperties>)labelPropertiesDao.selectAll();
	}

	@Override
	public ArrayList<TypeProperties> selectAllTypes() {
		return (ArrayList<TypeProperties>)typePropertiesDao.selectAll();
	}

}
