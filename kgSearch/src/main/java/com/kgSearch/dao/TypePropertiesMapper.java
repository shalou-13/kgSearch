package com.kgSearch.dao;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.kgSearch.pojo.TypeProperties;

@Service
public interface TypePropertiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TypeProperties record);

    int insertSelective(TypeProperties record);

    TypeProperties selectByPrimaryKey(Integer id);
    
    ArrayList<TypeProperties> selectAll();

    int updateByPrimaryKeySelective(TypeProperties record);

    int updateByPrimaryKeyWithBLOBs(TypeProperties record);

    int updateByPrimaryKey(TypeProperties record);
}