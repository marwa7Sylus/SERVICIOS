package caisse;

import java.util.*;

public class Guichet {
    private boolean disponible;
    private Service service;
    private Employe employe;
    private Client clientEnCours;
    private List<String> historique;

    public Guichet(Service service, Employe employe) {
        this.service = service;
        this.employe = employe;
        this.disponible = true;
        this.clientEnCours = null;
        this.historique = new ArrayList<>();
    }

    public Service getService() {return service;}
    public Employe getEmploye() {return employe;}
    public boolean isDisponible() {return disponible;}
    public Client getClientEnCours() {return clientEnCours;}
    public List<String> getHistorique() {return historique;}
    public void traiterClient(Client client) {
        if (!disponible) {
            System.out.println("Guichet est occupé");
            return;
        }
        Date maintenant = new Date();
        client.setDernierPassage(maintenant);
        clientEnCours = client;
        disponible = false;
        String message = "Le client " + client.getNom() + " " + client.getPrenom() + " est pris en charge au guichet par " + employe.getNom() + " pour le service " + service.getNom() + " à " + maintenant;
        historique.add(message);
        System.out.println(message);
    }
    public void terminerTraitement() {
        if (clientEnCours != null) {
            String message = "Traitement terminé pour le client " + clientEnCours.getNom() + " " + clientEnCours.getPrenom() + " à " + new Date();
            historique.add(message);
            System.out.println(message);
            clientEnCours = null;
            disponible = true;
        }
    }
    @Override
    public String toString() {
        String status = disponible ? "Disponible" : "Occupé";
        String clientInfo = clientEnCours == null ? "Aucun client" : "Client: " + clientEnCours.getNom() + " " + clientEnCours.getPrenom();
        return "Guichet de " + service.getNom() + " (" + status + ") - " + "Employé: " + employe.getNom() + " " + employe.getPrenom() + " - " + clientInfo;
    }
}