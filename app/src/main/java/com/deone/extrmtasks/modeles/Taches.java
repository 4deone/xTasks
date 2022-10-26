package com.deone.extrmtasks.modeles;

public class Taches implements Comparable<Taches>{
    private String tid;
    private String tcover;
    private String ttitre;
    private String tdescription;
    private String tpays;
    private String tville;
    private String tadresse;
    private String tcodepostal;
    private String tlongitude;
    private String tlatitude;
    private String tstate;
    private String tdate;
    private String tncomment;
    private String tnlike;
    private String uid;
    private String unoms;
    private String uavatar;

    public Taches() {
    }

    public Taches(String tid, String tcover, String ttitre, String tdescription, String tpays, String tville, String tadresse, String tcodepostal, String tlongitude, String tlatitude, String tstate, String tdate, String tncomment, String tnlike, String uid, String unoms, String uavatar) {
        this.tid = tid;
        this.tcover = tcover;
        this.ttitre = ttitre;
        this.tdescription = tdescription;
        this.tpays = tpays;
        this.tville = tville;
        this.tadresse = tadresse;
        this.tcodepostal = tcodepostal;
        this.tlongitude = tlongitude;
        this.tlatitude = tlatitude;
        this.tstate = tstate;
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

    public String getTpays() {
        return tpays;
    }

    public void setTpays(String tpays) {
        this.tpays = tpays;
    }

    public String getTville() {
        return tville;
    }

    public void setTville(String tville) {
        this.tville = tville;
    }

    public String getTadresse() {
        return tadresse;
    }

    public void setTadresse(String tadresse) {
        this.tadresse = tadresse;
    }

    public String getTcodepostal() {
        return tcodepostal;
    }

    public void setTcodepostal(String tcodepostal) {
        this.tcodepostal = tcodepostal;
    }

    public String getTlongitude() {
        return tlongitude;
    }

    public void setTlongitude(String tlongitude) {
        this.tlongitude = tlongitude;
    }

    public String getTlatitude() {
        return tlatitude;
    }

    public void setTlatitude(String tlatitude) {
        this.tlatitude = tlatitude;
    }

    public String getTstate() {
        return tstate;
    }

    public void setTstate(String tstate) {
        this.tstate = tstate;
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
