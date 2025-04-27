package caisse;

import java.util.*;

public class Client extends Personne {
    private int id;
    private Service service;
    private Date dernierPassage;

    public Client(String nom, String prenom, int id, Service service) {
        super(nom, prenom);
        this.id = id;
        this.service = service;
    }

    public int getId() {return id;}

    public Service getService() {return service;}

    public Date getDernierPassage() {return dernierPassage;}

    public void setDernierPassage(Date dernierPassage) {this.dernierPassage = dernierPassage;}

    @Override
    public String toString() {
        return "Client: " + nom + " " + prenom + " (ID: " + id + "), Service demandé: " + service.getNom();
    }
}
