package com.kgSearch.pojo;

public class ChildrenMissionsWithBLOBs extends ChildrenMissions {
    private String nkeyword;

    private String vkeyword;

    private String akeyword;

    private String result;

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
}