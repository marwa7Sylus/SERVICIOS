package caisse;

import java.util.*;
import java.text.SimpleDateFormat;

public class Gestion {
    private List<Guichet> guichets;
    private List<Client> fileStandard;
    private List<Client> filePrioritaire;
    private SimpleDateFormat dateFormat;

    public Gestion() {
        this.guichets = new ArrayList<>();
        this.fileStandard = new ArrayList<>();
        this.filePrioritaire = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        initialiserSysteme();
    }

    private void initialiserSysteme() {
        // Création des services
        Service serviceDepot = new Service("Dépôt", "Service de dépôt d'argent");
        Service serviceRetrait = new Service("Retrait", "Service de retrait d'argent");

        // Création des employés
        Employe employe1 = new Employe("Dupont", "Jean", 101, "Caissier");
        Employe employe2 = new Employe("Martin", "Marie", 102, "Caissier");

        // Création des guichets
        Guichet guichet1 = new Guichet(serviceDepot, employe1);
        Guichet guichet2 = new Guichet(serviceRetrait, employe2);

        guichets.add(guichet1);
        guichets.add(guichet2);
    }

    public void ajouterClient(Client client, boolean prioritaire) {
        if (prioritaire) {
            filePrioritaire.add(client);
        } else {
            fileStandard.add(client);
        }

        assignerClientAuGuichet();
    }

    public void assignerClientAuGuichet() {
        // Vérifier d'abord les clients prioritaires
        for (int i = 0; i < filePrioritaire.size(); i++) {
            Client client = filePrioritaire.get(i);

            for (Guichet guichet : guichets) {
                if (guichet.getService().getNom().equals(client.getService().getNom()) &&
                        guichet.isDisponible() &&
                        peutEtreServi(client)) {

                    guichet.traiterClient(client);
                    filePrioritaire.remove(i);
                    return;
                }
            }
        }

        // Ensuite, vérifier les clients standard
        for (int i = 0; i < fileStandard.size(); i++) {
            Client client = fileStandard.get(i);

            for (Guichet guichet : guichets) {
                if (guichet.getService().getNom().equals(client.getService().getNom()) &&
                        guichet.isDisponible() &&
                        peutEtreServi(client)) {

                    guichet.traiterClient(client);
                    fileStandard.remove(i);
                    return;
                }
            }
        }
    }

    // Vérifier si un client peut être servi (pas de passage consécutif)
    private boolean peutEtreServi(Client client) {
        Date dernierPassage = client.getDernierPassage();
        if (dernierPassage == null) {
            return true;  // Premier passage du client
        }

        // Vérifier si le dernier passage date d'au moins 5 minutes (à ajuster selon besoin)
        Date maintenant = new Date();
        long differenceMinutes = (maintenant.getTime() - dernierPassage.getTime()) / (60 * 1000);
        return differenceMinutes >= 5;
    }

    public void libererGuichet(int numeroGuichet) {
        if (numeroGuichet >= 1 && numeroGuichet <= guichets.size()) {
            Guichet guichet = guichets.get(numeroGuichet - 1);
            guichet.terminerTraitement();
            assignerClientAuGuichet();
        } else {
            System.out.println("Numéro de guichet invalide");
        }
    }

    public void afficherGuichets() {
        System.out.println("=== ÉTAT DES GUICHETS ===");
        for (int i = 0; i < guichets.size(); i++) {
            System.out.println("Guichet " + (i+1) + ": " + guichets.get(i));
        }
    }

    public void afficherClients() {
        System.out.println("=== CLIENTS EN ATTENTE ===");

        System.out.println("File prioritaire:");
        if (filePrioritaire.isEmpty()) {
            System.out.println("  Aucun client prioritaire en attente");
        } else {
            for (Client client : filePrioritaire) {
                System.out.println("  " + client);
            }
        }

        System.out.println("File standard:");
        if (fileStandard.isEmpty()) {
            System.out.println("  Aucun client standard en attente");
        } else {
            for (Client client : fileStandard) {
                System.out.println("  " + client);
            }
        }
    }

    public void afficherHistorique() {
        System.out.println("=== HISTORIQUE DE PASSAGE ===");
        for (int i = 0; i < guichets.size(); i++) {
            Guichet guichet = guichets.get(i);
            System.out.println("Historique du guichet " + (i+1) + " (" + guichet.getService().getNom() + "):");
            List<String> historique = guichet.getHistorique();

            if (historique.isEmpty()) {
                System.out.println("  Aucun passage enregistré");
            } else {
                for (String entree : historique) {
                    System.out.println("  " + entree);
                }
            }
            System.out.println();
        }
    }

    public void menuPrincipal() {
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== SYSTÈME DE GESTION DE GUICHETS ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Libérer un guichet");
            System.out.println("3. Afficher l'état des guichets");
            System.out.println("4. Afficher les clients en attente");
            System.out.println("5. Afficher l'historique des passages");
            System.out.println("0. Quitter");
            System.out.print("Votre choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine();  // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    menuAjoutClient(scanner);
                    break;
                case 2:
                    System.out.print("Numéro du guichet à libérer (1-" + guichets.size() + "): ");
                    int numeroGuichet = scanner.nextInt();
                    scanner.nextLine();  // Consommer le retour à la ligne
                    libererGuichet(numeroGuichet);
                    break;
                case 3:
                    afficherGuichets();
                    break;
                case 4:
                    afficherClients();
                    break;
                case 5:
                    afficherHistorique();
                    break;
                case 0:
                    continuer = false;
                    System.out.println("Merci d'avoir utilisé notre système. Au revoir!");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }

    private void menuAjoutClient(Scanner scanner) {
        System.out.println("\n=== AJOUT D'UN CLIENT ===");
        System.out.print("Nom du client: ");
        String nom = scanner.nextLine();

        System.out.print("Prénom du client: ");
        String prenom = scanner.nextLine();

        System.out.print("ID du client: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consommer le retour à la ligne

        System.out.println("Services disponibles:");
        for (int i = 0; i < guichets.size(); i++) {
            Service service = guichets.get(i).getService();
            System.out.println((i + 1) + ". " + service.getNom() + " - " + service.getDescription());
        }

        System.out.print("Choisissez un service (numéro): ");
        int choixService = scanner.nextInt();
        scanner.nextLine();  // Consommer le retour à la ligne

        Service serviceChoisi = guichets.get(choixService - 1).getService();

        System.out.print("Client prioritaire? (oui/non): ");
        String reponse = scanner.nextLine().toLowerCase();
        boolean prioritaire = reponse.equals("oui") || reponse.equals("o");

        Client client = new Client(nom, prenom, id, serviceChoisi);
        ajouterClient(client, prioritaire);

        System.out.println("Client ajouté avec succès.");
    }

}