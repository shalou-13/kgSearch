package com.kgSearch.dao;

import java.util.ArrayList;

import com.kgSearch.pojo.LabelProperties;

public interface LabelPropertiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LabelProperties record);

    int insertSelective(LabelProperties record);

    LabelProperties selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LabelProperties record);

    int updateByPrimaryKeyWithBLOBs(LabelProperties record);

    int updateByPrimaryKey(LabelProperties record);
    
    ArrayList<LabelProperties> selectAll();
    
    ArrayList<LabelProperties> fuzzySelectLabelByString(String label);
    
    ArrayList<LabelProperties> fuzzySelectPropertiesByString(String property);
}