package com.kgSearch.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kgSearch.dao.ChildrenMissionsMapper;
import com.kgSearch.pojo.ChildrenMissions;
import com.kgSearch.pojo.ChildrenMissionsWithBLOBs;
import com.kgSearch.service.IChildrenMissionService;

@Service
public class ChildrenMissionService implements IChildrenMissionService{
	
	@Resource
	private ChildrenMissionsMapper childrenMissionsDao;


	@Override
	public ChildrenMissions GetChildMissionById(int id) {
		// TODO Auto-generated method stub
		return this.childrenMissionsDao.selectByPrimaryKey(id);
	}

	@Override
	public boolean changeState(int id,int newstate) {
		// TODO Auto-generated method stub
		ChildrenMissionsWithBLOBs childrenMission=new ChildrenMissionsWithBLOBs();
		childrenMission.setState(newstate);
		childrenMission.setId(id);
		try{
			this.childrenMissionsDao.updateByPrimaryKeySelective(childrenMission);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean insertChildMission(ChildrenMissionsWithBLOBs childrenMission) {
		// TODO Auto-generated method stub
		/*ChildrenMissions childrenMission=new ChildrenMissions();
		childrenMission.setState(state);
		childrenMission.setPId(PId);*/
		try{
			this.childrenMissionsDao.insertSelective(childrenMission);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(ChildrenMissionsWithBLOBs record) {
		return this.childrenMissionsDao.updateByPrimaryKeySelective(record);
	}
	
}
