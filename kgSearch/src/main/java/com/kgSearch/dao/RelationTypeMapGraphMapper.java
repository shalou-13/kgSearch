package com.kgSearch.dao;

import com.kgSearch.pojo.RelationTypeMapGraph;

public interface RelationTypeMapGraphMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RelationTypeMapGraph record);

    int insertSelective(RelationTypeMapGraph record);

    RelationTypeMapGraph selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RelationTypeMapGraph record);

    int updateByPrimaryKey(RelationTypeMapGraph record);
    
}