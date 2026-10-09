package fr.arsir.http.exo7.serveur;

import java.net.ServerSocket;
import java.net.Socket;

import fr.arsir.http.exo7.service.ServiceReponse;
import fr.arsir.http.exo7.service.ServiceRequete;
import fr.arsir.http.exo7.service.ServiceSites;

public class ServeurHTTP {

    private static final int PORT = 8080;

    private final ServiceRequete requetes;
    private final ServiceReponse reponses;
    private final ServiceSites sites;

    public ServeurHTTP() {
        this.requetes = new ServiceRequete();
        this.reponses = new ServiceReponse();
        this.sites = new ServiceSites();
    }

    public void demarrer() {

        try {
            ServerSocket socketServeur = new ServerSocket(PORT);

            System.out.println(
                    "Serveur HTTP démarré sur http://localhost:" + PORT);

            while (true) {

                Socket socketClient = socketServeur.accept();

                SessionHTTP session = new SessionHTTP(
                        socketClient, requetes, reponses, sites);

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
