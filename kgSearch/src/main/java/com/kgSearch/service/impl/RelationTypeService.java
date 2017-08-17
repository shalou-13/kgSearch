package com.kgSearch.service.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kgSearch.dao.TypePropertiesMapper;
import com.kgSearch.pojo.TypeProperties;
import com.kgSearch.service.IRelationTypeService;

@Service
public class RelationTypeService implements IRelationTypeService{
	
	@Resource
	private TypePropertiesMapper typePropertiesMapper;

	@Override
	public ArrayList<TypeProperties> GetAllRelationType() {
		return this.typePropertiesMapper.selectAll();
	}

	@Override
	public ArrayList<TypeProperties> fuzzySelectTypeByString(String type) {
		return this.typePropertiesMapper.fuzzySelectTypeByString(type);
	}

	@Override
	public ArrayList<TypeProperties> fuzzySelectPropertiesByString(String property) {
		return this.typePropertiesMapper.fuzzySelectPropertiesByString(property);
	}
	
	

}
