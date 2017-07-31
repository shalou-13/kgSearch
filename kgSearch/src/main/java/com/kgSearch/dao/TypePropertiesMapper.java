package com.kgSearch.dao;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.asm.Type;
import com.kgSearch.pojo.TypeProperties;

@Service
public interface TypePropertiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TypeProperties record);

    int insertSelective(TypeProperties record);

    TypeProperties selectByPrimaryKey(Integer id);
    
    ArrayList<TypeProperties> selectAll();

    ArrayList<TypeProperties> fuzzySelectTypeByString(String type);
    
    ArrayList<TypeProperties> fuzzySelectPropertiesByString(String property);
    
    int updateByPrimaryKeySelective(TypeProperties record);

    int updateByPrimaryKeyWithBLOBs(TypeProperties record);

    int updateByPrimaryKey(TypeProperties record);
}