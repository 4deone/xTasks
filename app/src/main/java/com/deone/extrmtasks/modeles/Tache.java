package com.deone.extrmtasks.modeles;

import static com.deone.extrmtasks.tools.Other.tacheToString;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Tache implements Comparable<Tache>{
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
    private Localize localize;

    public Tache() {
    }

    public Tache(String tid, String ttitre, String tdescription) {
        this.tid = tid;
        this.ttitre = ttitre;
        this.tdescription = tdescription;
    }

    public Tache(String tid, String tcover, String ttitre,
                 String tdescription, String tdate, String tncomment,
                 String tnlike, String uid, String unoms, String uavatar, Localize localize) {
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
        this.localize = localize;
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

    public Localize getLocalize() {
        return localize;
    }

    public void setLocalize(Localize localize) {
        this.localize = localize;
    }

    @Override
    public int compareTo(Tache tache) {
        return 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @NonNull
    @Override
    public String toString() {
        return tacheToString(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
