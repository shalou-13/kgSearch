package com.kgSearch.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.kgSearch.pojo.TypeProperties;

@Service
public interface TypePropertiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TypeProperties record);

    int insertSelective(TypeProperties record);

    TypeProperties selectByPrimaryKey(Integer id);
    
    ArrayList<TypeProperties> selectAll();

    ArrayList<TypeProperties> fuzzySelectTypeByString(String type);
    
    ArrayList<TypeProperties> fuzzySelectTypeByStringFromGraph(@Param("keyword")String keyword, @Param("graphID")String graphID);
    
    ArrayList<TypeProperties> fuzzySelectPropertiesByString(String property);
    
    ArrayList<TypeProperties> fuzzySelectPropertiesByStringFromGraph(@Param("keyword")String keyword, @Param("graphID")String graphID);
    
    int updateByPrimaryKeySelective(TypeProperties record);

    int updateByPrimaryKeyWithBLOBs(TypeProperties record);

    int updateByPrimaryKey(TypeProperties record);
    
    ArrayList<TypeProperties> selectRelationTypeByGraphID(String graphID);
}