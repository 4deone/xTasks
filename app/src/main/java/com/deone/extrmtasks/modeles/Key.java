package com.deone.extrmtasks.modeles;

public class Key implements Comparable<Key>{
    private String kid;
    private String kmessage;
    private String kdate;

    public Key() {
    }

    public Key(String kid, String kmessage, String kdate) {
        this.kid = kid;
        this.kmessage = kmessage;
        this.kdate = kdate;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getKmessage() {
        return kmessage;
    }

    public void setKmessage(String kmessage) {
        this.kmessage = kmessage;
    }

    public String getKdate() {
        return kdate;
    }

    public void setKdate(String kdate) {
        this.kdate = kdate;
    }

    @Override
    public int compareTo(Key commentaire) {
        return 0;
    }

}
