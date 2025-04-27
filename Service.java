package caisse;

public class Service {
    private String nom;
    private String description;

    public Service(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() {return nom;}

    public String getDescription() {return description;}
}