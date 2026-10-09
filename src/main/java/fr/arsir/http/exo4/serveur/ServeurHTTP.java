package fr.arsir.http.exo4.serveur;

import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.http.exo4.service.ServiceRequete;

public class ServeurHTTP {

    private static final int PORT = 8080;

    private final ServiceRequete requetes;

    public ServeurHTTP() {
        this.requetes = new ServiceRequete();
    }

    public void demarrer() {

        try {
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur HTTP démarré sur http://localhost:" + PORT);

            while (true) {

                Socket socketClient = socketServeur.accept();

                SessionHTTP session = new SessionHTTP(socketClient, requetes);

                session.start();
            }

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServeurHTTP serveur = new ServeurHTTP();
        serveur.demarrer();
    }
}
