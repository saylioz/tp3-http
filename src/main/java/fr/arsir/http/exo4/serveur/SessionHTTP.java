package fr.arsir.http.exo4.serveur;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import fr.arsir.http.exo4.service.ServiceRequete;

// Une session = un thread par client (cf. CM). Le serveur reçoit la requête,
// l'affiche et indique le code qui lui correspond (400, 405 ou 200).
// Il n'envoie pas encore de réponse (exercices 5 et 6).
public class SessionHTTP extends Thread {

    private static final int DELAI = 10000;

    private final Socket socketClient;
    private final ServiceRequete requetes;

    public SessionHTTP(Socket socketClient, ServiceRequete requetes) {

        this.socketClient = socketClient;
        this.requetes = requetes;
    }

    @Override
    public void run() {

        try {
            // un client qui n'envoie rien ne doit pas bloquer le thread
            socketClient.setSoTimeout(DELAI);

            // 1 - Ouverture du flux de la socket
            InputStream fluxEntree =
                    new BufferedInputStream(socketClient.getInputStream());

            // 2 - Recevoir la requête
            String requete = requetes.recevoirRequete(fluxEntree);

            if (requete != null) {

                // 3 - Vérifier la requête
                int code = requetes.verifier(requete);

                System.out.println("--- Requête de "
                        + socketClient.getInetAddress() + " ---\n"
                        + requete.replaceAll("[^\\x20-\\x7E\\r\\n]", "?")
                        + "--- Code : " + code + " ---");
            }

            // 4 - Libérer le canal
            fermer();

        } catch (SocketTimeoutException e) {
            // connexion inactive : rien à afficher
            fermer();

        } catch (Exception e) {
            System.err.println("Erreur session : " + e.getMessage());
            fermer();
        }
    }

    private void fermer() {

        try {
            if (!socketClient.isClosed()) {
                socketClient.close();
            }
        } catch (Exception e) {
            // fermeture au mieux
        }
    }
}
