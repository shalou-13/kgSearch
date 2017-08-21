package com.kgSearch.pojo;

public class LabelMapGraph {
    private Integer id;

    private Integer labelID;

    private String graphID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLabelID() {
        return labelID;
    }

    public void setLabelID(Integer labelID) {
        this.labelID = labelID;
    }

    public String getGraphID() {
        return graphID;
    }

    public void setGraphID(String graphID) {
        this.graphID = graphID == null ? null : graphID.trim();
    }
}