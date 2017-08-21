package com.kgSearch.pojo;

public class RelationTypeMapGraph {
    private Integer id;

    private Integer relationTypeID;

    private String graphID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRelationTypeID() {
        return relationTypeID;
    }

    public void setRelationTypeID(Integer relationTypeID) {
        this.relationTypeID = relationTypeID;
    }

    public String getGraphID() {
        return graphID;
    }

    public void setGraphID(String graphID) {
        this.graphID = graphID == null ? null : graphID.trim();
    }
}