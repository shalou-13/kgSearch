package com.kgSearch.dao;

import com.kgSearch.pojo.ChildrenMissionsWithBLOBs;
import com.kgSearch.pojo.ChildrenMissions;

public interface ChildrenMissionsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChildrenMissionsWithBLOBs record);

    int insertSelective(ChildrenMissionsWithBLOBs record);

    ChildrenMissionsWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChildrenMissionsWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ChildrenMissionsWithBLOBs record);

    int updateByPrimaryKey(ChildrenMissions record);
}