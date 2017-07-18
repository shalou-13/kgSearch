package com.kgSearch.method;

public interface GraphHandler {
	
	public void initRelationshipTypeList();

	public void initEntityLabelList();
	
	public void initEntityList();
	
	public void adjustEntityLabelList();

	public void initEntityPairsPathSet();
	
	public void initEntityEntityPathSet();
	
	public void initEntityRelationshipSet();
	
	public void getTResult();
	
	public void adjustRelationshipTypeList();
	
	public void adjustEntityList();

	public void initNewEntityLabelList();
}
