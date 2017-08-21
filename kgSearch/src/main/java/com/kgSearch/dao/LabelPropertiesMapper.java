package com.kgSearch.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

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
    
    ArrayList<LabelProperties> fuzzySelectLabelByStringFromGraph(@Param("keyword")String keyword, @Param("graphID")String graphID);
    
    ArrayList<LabelProperties> fuzzySelectPropertiesByString(String property);
    
    ArrayList<LabelProperties> fuzzySelectPropertiesByStringFromGraph(@Param("keyword")String keyword, @Param("graphID")String graphID);
    
    ArrayList<LabelProperties> selectLabelByGraphID(String graphID);
}