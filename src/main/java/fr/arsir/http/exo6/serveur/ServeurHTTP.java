package fr.arsir.http.exo6.serveur;

import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.http.exo6.service.ServiceChemin;
import fr.arsir.http.exo6.service.ServiceReponse;
import fr.arsir.http.exo6.service.ServiceRequete;

public class ServeurHTTP {

    private static final int PORT = 6666;

    // dossier du site web
    private static final String DOSSIER_SITE = "www/site1";

    private final ServiceRequete requetes;
    private final ServiceReponse reponses;
    private final ServiceChemin site;

    public ServeurHTTP() {
        this.requetes = new ServiceRequete();
        this.reponses = new ServiceReponse();
        this.site = new ServiceChemin(DOSSIER_SITE);
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
                SessionHTTP session = new SessionHTTP(
                        socketClient, requetes, reponses, site);

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
