package com.deone.extrmtasks.modeles;

public class Taches implements Comparable<Taches>{
    private String tid;
    private String tcover;
    private String ttitre;
    private String tdescription;
    private String tdate;
    private String tncomment;
    private String tnlike;
    private String uid;
    private String unoms;
    private String uavatar;

    public Taches() {
    }

    public Taches(String tid, String tcover, String ttitre, String tdescription, String tdate, String tncomment, String tnlike, String uid, String unoms, String uavatar) {
        this.tid = tid;
        this.tcover = tcover;
        this.ttitre = ttitre;
        this.tdescription = tdescription;
        this.tdate = tdate;
        this.tncomment = tncomment;
        this.tnlike = tnlike;
        this.uid = uid;
        this.unoms = unoms;
        this.uavatar = uavatar;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTcover() {
        return tcover;
    }

    public void setTcover(String tcover) {
        this.tcover = tcover;
    }

    public String getTtitre() {
        return ttitre;
    }

    public void setTtitre(String ttitre) {
        this.ttitre = ttitre;
    }

    public String getTdescription() {
        return tdescription;
    }

    public void setTdescription(String tdescription) {
        this.tdescription = tdescription;
    }

    public String getTdate() {
        return tdate;
    }

    public void setTdate(String tdate) {
        this.tdate = tdate;
    }

    public String getTncomment() {
        return tncomment;
    }

    public void setTncomment(String tncomment) {
        this.tncomment = tncomment;
    }

    public String getTnlike() {
        return tnlike;
    }

    public void setTnlike(String tnlike) {
        this.tnlike = tnlike;
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
    public int compareTo(Taches taches) {
        return 0;
    }
}
