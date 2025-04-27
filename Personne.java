package caisse;

public abstract class Personne {
    public String nom;
    public String prenom;
    public Personne(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
}
