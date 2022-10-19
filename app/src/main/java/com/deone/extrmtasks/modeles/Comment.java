package com.deone.extrmtasks.modeles;

public class Comment implements Comparable<Comment>{
    private String cid;
    private String cmessage;
    private String cdate;
    private String tid;
    private String uid;
    private String unoms;
    private String uavatar;

    public Comment() {
    }

    public Comment(String cid, String cmessage, String cdate, String tid, String uid, String unoms, String uavatar) {
        this.cid = cid;
        this.cmessage = cmessage;
        this.cdate = cdate;
        this.tid = tid;
        this.uid = uid;
        this.unoms = unoms;
        this.uavatar = uavatar;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCmessage() {
        return cmessage;
    }

    public void setCmessage(String cmessage) {
        this.cmessage = cmessage;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnoms() {
        return unoms;
    }

    public void setUnoms(String unoms) {
        this.unoms = unoms;
    }

    public String getUavatar() {
        return uavatar;
    }

    public void setUavatar(String uavatar) {
        this.uavatar = uavatar;
    }

    @Override
    public int compareTo(Comment comment) {
        return 0;
    }
}
