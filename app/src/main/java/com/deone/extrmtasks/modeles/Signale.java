package com.deone.extrmtasks.modeles;

public class Signale implements Comparable<Signale>{
    private String sid;
    private String smessage;
    private String sdate;
    private String tid;
    private String ttitre;
    private String tcover;
    private String tdescription;
    private String uid;
    private String unoms;
    private String uavatar;

    public Signale() {
    }

    public Signale(String sid, String smessage, String sdate, String uid, String unoms, String uavatar) {
        this.sid = sid;
        this.smessage = smessage;
        this.sdate = sdate;
        this.uid = uid;
        this.unoms = unoms;
        this.uavatar = uavatar;
    }

    public Signale(String sid, String smessage,
                   String sdate, String tid,
                   String ttitre, String tcover,
                   String tdescription, String uid,
                   String unoms, String uavatar) {
        this.sid = sid;
        this.smessage = smessage;
        this.sdate = sdate;
        this.tid = tid;
        this.ttitre = ttitre;
        this.tcover = tcover;
        this.tdescription = tdescription;
        this.uid = uid;
        this.unoms = unoms;
        this.uavatar = uavatar;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSmessage() {
        return smessage;
    }

    public void setSmessage(String smessage) {
        this.smessage = smessage;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTtitre() {
        return ttitre;
    }

    public void setTtitre(String ttitre) {
        this.ttitre = ttitre;
    }

    public String getTcover() {
        return tcover;
    }

    public void setTcover(String tcover) {
        this.tcover = tcover;
    }

    public String getTdescription() {
        return tdescription;
    }

    public void setTdescription(String tdescription) {
        this.tdescription = tdescription;
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
    public int compareTo(Signale commentaire) {
        return 0;
    }

}
