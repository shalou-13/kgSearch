package com.kgSearch.dao;

import java.util.ArrayList;

import com.kgSearch.pojo.LabelMapGraph;
import com.kgSearch.pojo.LabelProperties;

public interface LabelMapGraphMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LabelMapGraph record);

    int insertSelective(LabelMapGraph record);

    LabelMapGraph selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LabelMapGraph record);

    int updateByPrimaryKey(LabelMapGraph record);
    
}