package com.kgSearch.dao;

import java.util.List;

import com.kgSearch.pojo.ChildrenMissions;


public interface ChildrenMissionsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChildrenMissions record);

    int insertSelective(ChildrenMissions record);

    ChildrenMissions selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChildrenMissions record);

    int updateByPrimaryKey(ChildrenMissions record);
    
    List<ChildrenMissions> selectAllChildrenMissions();
}