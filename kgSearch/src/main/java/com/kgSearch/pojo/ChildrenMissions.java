package com.kgSearch.pojo;

import java.util.Date;

public class ChildrenMissions {
    private Integer id;

    private Integer PId;

    private Integer state;

    private Date addTime;

    private Date finishTime;
    
    private String nkeyword;

    private String vkeyword;

    private String akeyword;

    private String result;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPId() {
        return PId;
    }

    public void setPId(Integer PId) {
        this.PId = PId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
    
    public String getNkeyword() {
        return nkeyword;
    }

    public void setNkeyword(String nkeyword) {
        this.nkeyword = nkeyword == null ? null : nkeyword.trim();
    }

    public String getVkeyword() {
        return vkeyword;
    }

    public void setVkeyword(String vkeyword) {
        this.vkeyword = vkeyword == null ? null : vkeyword.trim();
    }

    public String getAkeyword() {
        return akeyword;
    }

    public void setAkeyword(String akeyword) {
        this.akeyword = akeyword == null ? null : akeyword.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }
    
    public void show(){
    	if(id!=null){
    		System.out.print("id:"+id);
    	}
    	if(PId!=null){
    		System.out.print(" PId:"+PId);
    	}
    	if(state!=null){
    		System.out.print(" state:"+state);
    	}
    	if(addTime!=null){
    		System.out.print(" addTime:"+addTime);
    	}
    	if(finishTime!=null){
    		System.out.print(" finishTime:"+finishTime);
    	}
    	if(nkeyword!=null){
			System.out.print(" nkeyword:"+nkeyword);
		}
    	if(vkeyword!=null){
    		System.out.print(" vkeyword:"+vkeyword);
    	}
    	if(akeyword!=null){
    		System.out.print(" akeyword:"+akeyword);
    	}
    	if(result!=null){
    		System.out.print(" result:"+result);
    	}
    	System.out.print("\n");
    }
}