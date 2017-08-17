package com.kgSearch.service;

import com.kgSearch.pojo.ChildrenMissions;
import com.kgSearch.pojo.ChildrenMissionsWithBLOBs;

public interface IChildrenMissionService {
	
	public boolean insertChildMission(ChildrenMissionsWithBLOBs childrenMission); 
	
	public ChildrenMissions GetChildMissionById(int id);
	
	public boolean changeState(int id,int newstate);
	
	public int updateByPrimaryKeyWithBLOBs(ChildrenMissionsWithBLOBs record);
}
