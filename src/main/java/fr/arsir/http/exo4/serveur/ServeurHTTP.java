package fr.arsir.http.exo4.serveur;

import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.http.exo4.service.ServiceRequete;

public class ServeurHTTP {

    private static final int PORT = 6666;

    private final ServiceRequete requetes;

    public ServeurHTTP() {
        this.requetes = new ServiceRequete();
    }

    public void demarrer() {

        try {
            // 1 - Création du canal et réservation du port
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur HTTP démarré sur http://localhost:" + PORT);

            while (true) {

                // 2 - Mise en attente et acceptation de la connexion
                Socket socketClient = socketServeur.accept();

                // 3 - Un thread par client (cf. CM) : les clients
                //     sont servis en parallèle
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
