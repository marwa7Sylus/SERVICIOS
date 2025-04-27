package caisse;

public class Employe extends Personne {
    private int matricule;
    private String fonction;
    public Employe(String nom, String prenom, int matricule, String fonction) {
        super(nom, prenom);
        this.matricule = matricule;
        this.fonction = fonction;
    }
    public int getMatricule() {return matricule;}

    public void setMatricule(int matricule) {this.matricule = matricule;}
    public String getFonction() {return fonction;}
    public void setFonction(String fonction) {
        this.fonction = fonction;
    }
    @Override
    public String toString() {
        return "Employe: " + nom + " " + prenom + " (Matricule: " + matricule + "), Fonction: " + fonction;
    }
}