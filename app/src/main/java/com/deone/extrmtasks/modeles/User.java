package com.deone.extrmtasks.modeles;

public class User implements Comparable<User>{
    private  String uid;
    private  String unoms;
    private  String ucover;
    private  String uavatar;
    private  String utelephone;
    private  String udescription;
    private  String uemail;
    private  String ucity;
    private  String ucountry;
    private  String udate;
    private  String untask;
    private  String unfavoris;
    private  String unlikes;
    private  String uncomments;
    private  String ungroups;

    public User() {
    }

    public User(String uid, String unoms, String ucover, String uavatar, String utelephone, String udescription, String uemail, String ucity, String ucountry, String udate, String untask, String unfavoris, String unlikes, String uncomments, String ungroups) {
        this.uid = uid;
        this.unoms = unoms;
        this.ucover = ucover;
        this.uavatar = uavatar;
        this.utelephone = utelephone;
        this.udescription = udescription;
        this.uemail = uemail;
        this.ucity = ucity;
        this.ucountry = ucountry;
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

    public String getUnoms() {
        return unoms;
    }

    public void setUnoms(String unoms) {
        this.unoms = unoms;
    }

    public String getUcover() {
        return ucover;
    }

    public void setUcover(String ucover) {
        this.ucover = ucover;
    }

    public String getUavatar() {
        return uavatar;
    }

    public void setUavatar(String uavatar) {
        this.uavatar = uavatar;
    }

    public String getUtelephone() {
        return utelephone;
    }

    public void setUtelephone(String utelephone) {
        this.utelephone = utelephone;
    }

    public String getUdescription() {
        return udescription;
    }

    public void setUdescription(String udescription) {
        this.udescription = udescription;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUcity() {
        return ucity;
    }

    public void setUcity(String ucity) {
        this.ucity = ucity;
    }

    public String getUcountry() {
        return ucountry;
    }

    public void setUcountry(String ucountry) {
        this.ucountry = ucountry;
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
