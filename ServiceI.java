package caisse;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

public class ServiceI extends Frame {
    // Bank system components
    private ArrayList<Guichet> guichets = new ArrayList<>();
    private ArrayList<Client> clientsQueue = new ArrayList<>();

    // GUI components
    private Button btnAddClient;
    private Button btnReleaseCounter1;
    private Button btnReleaseCounter2;
    private Button btnExit;
    private TextArea counterStatus;
    private TextArea clientsWaiting;

    // Services and employees
    private Service serviceDepot;
    private Service serviceRetrait;
    private Employe employe1;
    private Employe employe2;
    private Guichet guichet1;
    private Guichet guichet2;

    public ServiceI() {
        // Initialize bank system
        initBankSystem();

        // Setup GUI
        setTitle("Simple Bank System");
        setSize(600, 400);
        setLayout(new BorderLayout(10, 10));

        // Create panels
        Panel northPanel = new Panel(new FlowLayout());
        Panel centerPanel = new Panel(new GridLayout(1, 2, 10, 10));
        Panel southPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Create and add components
        btnAddClient = new Button("Add Client");
        btnReleaseCounter1 = new Button("Release Counter 1");
        btnReleaseCounter2 = new Button("Release Counter 2");
        btnExit = new Button("Exit");

        counterStatus = new TextArea(10, 30);
        counterStatus.setEditable(false);

        clientsWaiting = new TextArea(10, 30);
        clientsWaiting.setEditable(false);

        // Labels
        Label titleLabel = new Label("BANK COUNTER SYSTEM", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Add components to panels
        northPanel.add(titleLabel);

        centerPanel.add(counterStatus);
        centerPanel.add(clientsWaiting);

        southPanel.add(btnAddClient);
        southPanel.add(btnReleaseCounter1);
        southPanel.add(btnReleaseCounter2);
        southPanel.add(btnExit);

        // Add panels to frame
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add button actions
        btnAddClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddClientDialog();
            }
        });

        btnReleaseCounter1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                releaseCounter(guichet1);
                updateDisplay();
            }
        });

        btnReleaseCounter2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                releaseCounter(guichet2);
                updateDisplay();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // Update display and show frame
        updateDisplay();
        setVisible(true);
    }

    private void initBankSystem() {
        // Create services
        serviceDepot = new Service("Depot", "Money deposit service");
        serviceRetrait = new Service("Retrait", "Money withdrawal service");

        // Create employees
        employe1 = new Employe("Smith", "John", 101, "Cashier");
        employe2 = new Employe("Johnson", "Mary", 102, "Cashier");

        // Create counters
        guichet1 = new Guichet(serviceDepot, employe1);
        guichet2 = new Guichet(serviceRetrait, employe2);

        guichets.add(guichet1);
        guichets.add(guichet2);
    }

    private void showAddClientDialog() {
        Dialog dialog = new Dialog(this, "Add Client", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10)); // Reduced grid size (removed priority option)

        Label lblName = new Label("Name:");
        TextField txtName = new TextField(20);

        Label lblFirstName = new Label("First Name:");
        TextField txtFirstName = new TextField(20);

        Label lblID = new Label("ID:");
        TextField txtID = new TextField(20);

        Label lblService = new Label("Service:");
        Choice serviceChoice = new Choice();
        serviceChoice.add("Depot");
        serviceChoice.add("Retrait");

        Button btnOK = new Button("OK");
        Button btnCancel = new Button("Cancel");

        dialog.add(lblName);
        dialog.add(txtName);
        dialog.add(lblFirstName);
        dialog.add(txtFirstName);
        dialog.add(lblID);
        dialog.add(txtID);
        dialog.add(lblService);
        dialog.add(serviceChoice);
        dialog.add(btnOK);
        dialog.add(btnCancel);

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = txtName.getText();
                    String firstName = txtFirstName.getText();
                    int id = Integer.parseInt(txtID.getText());
                    String selectedService = serviceChoice.getSelectedItem();

                    Service service = selectedService.equals("Depot") ? serviceDepot : serviceRetrait;

                    Client client = new Client(name, firstName, id, service);
                    clientsQueue.add(client);

                    assignClientToCounter();
                    updateDisplay();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    // Show error message
                    Dialog errorDialog = new Dialog(dialog, "Error", true);
                    errorDialog.setLayout(new FlowLayout());
                    errorDialog.add(new Label("Please enter a valid ID number"));
                    Button okButton = new Button("OK");
                    okButton.addActionListener(e1 -> errorDialog.dispose());
                    errorDialog.add(okButton);
                    errorDialog.pack();
                    errorDialog.setVisible(true);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void assignClientToCounter() {
        // Process clients in FIFO order
        for (int i = 0; i < clientsQueue.size(); i++) {
            Client client = clientsQueue.get(i);
            for (Guichet guichet : guichets) {
                if (guichet.getService().getNom().equals(client.getService().getNom()) &&
                        guichet.isDisponible() && canBeServed(client)) {
                    guichet.traiterClient(client);
                    clientsQueue.remove(i);
                    return;
                }
            }
        }
    }

    // Check if a client can be served (no consecutive service)
    private boolean canBeServed(Client client) {
        Date lastVisit = client.getDernierPassage();
        if (lastVisit == null) {
            return true;  // First visit
        }

        // Check if last visit was at least 5 minutes ago
        Date now = new Date();
        long differenceMinutes = (now.getTime() - lastVisit.getTime()) / (60 * 1000);
        return differenceMinutes >= 5;
    }

    private void releaseCounter(Guichet guichet) {
        guichet.terminerTraitement();
        assignClientToCounter();
    }

    private void updateDisplay() {
        // Update counter status display
        counterStatus.setText("=== COUNTER STATUS ===\n\n");
        for (int i = 0; i < guichets.size(); i++) {
            Guichet g = guichets.get(i);
            counterStatus.append("Counter " + (i+1) + " (" + g.getService().getNom() + "):\n");
            counterStatus.append("Employee: " + g.getEmploye().getNom() + " " + g.getEmploye().getPrenom() + "\n");
            counterStatus.append("Status: " + (g.isDisponible() ? "Available" : "Busy") + "\n");

            Client client = g.getClientEnCours();
            if (client != null) {
                counterStatus.append("Current client: " + client.getNom() + " " + client.getPrenom() + " (ID: " + client.getId() + ")\n");
            } else {
                counterStatus.append("Current client: None\n");
            }
            counterStatus.append("\n");
        }

        // Update clients waiting display
        clientsWaiting.setText("=== WAITING CLIENTS ===\n\n");

        if (clientsQueue.isEmpty()) {
            clientsWaiting.append("No clients waiting\n");
        } else {
            for (int i = 0; i < clientsQueue.size(); i++) {
                Client c = clientsQueue.get(i);
                clientsWaiting.append((i+1) + ". " + c.getNom() + " " + c.getPrenom() + " (ID: " + c.getId() +
                        ") - Service: " + c.getService().getNom() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        new ServiceI();
    }
}