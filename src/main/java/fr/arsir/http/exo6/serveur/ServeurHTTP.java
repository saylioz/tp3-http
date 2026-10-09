package fr.arsir.http.exo6.serveur;

import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.http.exo6.service.ServiceChemin;
import fr.arsir.http.exo6.service.ServiceReponse;
import fr.arsir.http.exo6.service.ServiceRequete;

public class ServeurHTTP {

    private static final int PORT = 8080;

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
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur HTTP démarré sur http://localhost:" + PORT);

            while (true) {

                Socket socketClient = socketServeur.accept();

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
