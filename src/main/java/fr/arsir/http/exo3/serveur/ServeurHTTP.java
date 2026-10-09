package fr.arsir.http.exo3.serveur;

import java.net.ServerSocket;
import java.net.Socket;

public class ServeurHTTP {

    private static final int PORT = 6666;

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
                SessionHTTP session = new SessionHTTP(socketClient);

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
