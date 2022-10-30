package com.deone.extrmtasks.modeles;

public class Condition implements Comparable<Condition>{
    private String titre;
    private String description;
    public Condition() {
    }

    public Condition(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Condition condition) {
        return 0;
    }

    // Le comportement de notre classe Condition

}
