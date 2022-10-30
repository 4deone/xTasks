package com.deone.extrmtasks.modeles;

public class User implements Comparable<User>{
    private  String uid;
    private  String ucover;
    private  String uemail;
    private  String umotdepasse;
    private  String unoms;
    private  String utelephone;
    private  String uville;
    private  String upays;
    private  String uavatar;
    private  String udescription;
    private  String udate;
    private  String untask;
    private  String unfavoris;
    private  String unlikes;
    private  String uncomments;
    private  String ungroups;

    public User() {
    }

    public User(String uemail, String umotdepasse) {
        this.uemail = uemail;
        this.umotdepasse = umotdepasse;
    }

    public User(String uemail, String umotdepasse, String unoms, String utelephone, String uville, String upays) {
        this.uemail = uemail;
        this.umotdepasse = umotdepasse;
        this.unoms = unoms;
        this.utelephone = utelephone;
        this.uville = uville;
        this.upays = upays;
    }

    public User(String uid,
                String ucover,
                String uemail,
                String umotdepasse,
                String unoms,
                String utelephone,
                String uville,
                String upays,
                String uavatar,
                String udescription,
                String udate,
                String untask,
                String unfavoris,
                String unlikes,
                String uncomments,
                String ungroups) {
        this.uid = uid;
        this.ucover = ucover;
        this.uemail = uemail;
        this.umotdepasse = umotdepasse;
        this.unoms = unoms;
        this.utelephone = utelephone;
        this.uville = uville;
        this.upays = upays;
        this.uavatar = uavatar;
        this.udescription = udescription;
        this.udate = udate;
        this.untask = untask;
        this.unfavoris = unfavoris;
        this.unlikes = unlikes;
        this.uncomments = uncomments;
        this.ungroups = ungroups;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUcover() {
        return ucover;
    }

    public void setUcover(String ucover) {
        this.ucover = ucover;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUmotdepasse() {
        return umotdepasse;
    }

    public void setUmotdepasse(String umotdepasse) {
        this.umotdepasse = umotdepasse;
    }

    public String getUnoms() {
        return unoms;
    }

    public void setUnoms(String unoms) {
        this.unoms = unoms;
    }

    public String getUtelephone() {
        return utelephone;
    }

    public void setUtelephone(String utelephone) {
        this.utelephone = utelephone;
    }

    public String getUville() {
        return uville;
    }

    public void setUville(String uville) {
        this.uville = uville;
    }

    public String getUpays() {
        return upays;
    }

    public void setUpays(String upays) {
        this.upays = upays;
    }

    public String getUavatar() {
        return uavatar;
    }

    public void setUavatar(String uavatar) {
        this.uavatar = uavatar;
    }

    public String getUdescription() {
        return udescription;
    }

    public void setUdescription(String udescription) {
        this.udescription = udescription;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    public String getUntask() {
        return untask;
    }

    public void setUntask(String untask) {
        this.untask = untask;
    }

    public String getUnfavoris() {
        return unfavoris;
    }

    public void setUnfavoris(String unfavoris) {
        this.unfavoris = unfavoris;
    }

    public String getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(String unlikes) {
        this.unlikes = unlikes;
    }

    public String getUncomments() {
        return uncomments;
    }

    public void setUncomments(String uncomments) {
        this.uncomments = uncomments;
    }

    public String getUngroups() {
        return ungroups;
    }

    public void setUngroups(String ungroups) {
        this.ungroups = ungroups;
    }

    @Override
    public int compareTo(User user) {
        return 0;
    }

}
