package com.kgSearch.service.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kgSearch.dao.LabelPropertiesMapper;
import com.kgSearch.pojo.LabelProperties;
import com.kgSearch.service.ILabelService;

@Service
public class LabelService implements ILabelService{
	
	@Resource
	private LabelPropertiesMapper labelPropertiesMapper;

	@Override
	public ArrayList<LabelProperties> getAllLabel() {
		return this.labelPropertiesMapper.selectAll();
	}

	@Override
	public ArrayList<LabelProperties> fuzzySelectLabelByString(String label) {
		return this.labelPropertiesMapper.fuzzySelectLabelByString(label);
	}

	@Override
	public ArrayList<LabelProperties> fuzzySelectPropertiesByString(String property) {
		return this.labelPropertiesMapper.fuzzySelectPropertiesByString(property);
	}

}
