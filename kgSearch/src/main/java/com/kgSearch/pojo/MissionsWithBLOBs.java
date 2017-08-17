package com.kgSearch.pojo;

public class MissionsWithBLOBs extends Missions {
    private String rkeyword;

    private String nkeyword;

    private String vkeyword;

    private String akeyword;

    public String getRkeyword() {
        return rkeyword;
    }

    public void setRkeyword(String rkeyword) {
        this.rkeyword = rkeyword == null ? null : rkeyword.trim();
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
    
    public void show(){
    	System.out.print("id:"+this.getId());
    	if(this.rkeyword!=null){
    		System.out.print(" Rkeyword:"+rkeyword);
    	}
    	if(this.nkeyword!=null){
    		System.out.print(" Nkeyword:"+nkeyword);
    	}
    	if(this.vkeyword!=null){
    		System.out.print(" vkeyword:"+vkeyword);
    	}
    	if(this.akeyword!=null){
    		System.out.print(" akeyword:"+akeyword);
    	}
    	if(this.getState()!=null){
    		System.out.print(" state:"+this.getState());
    	}
    	if(this.getAddTime()!=null){
    		System.out.print(" AddTime:"+this.getAddTime());
    	}
    	if(this.getFinishTime()!=null){
    		System.out.print(" FinishTime:"+this.getFinishTime());
    	}
    	System.out.print("\n");
    }
}