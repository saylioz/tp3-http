package fr.arsir.http.exo3.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Une session = un thread par client (cf. CM). Pour l'instant le serveur
// se contente d'afficher la requête reçue, sans la vérifier ni répondre.
public class SessionHTTP extends Thread {

    private static final int DELAI = 10000;

    private final Socket socketClient;

    public SessionHTTP(Socket socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void run() {

        try {
            // un client qui n'envoie rien ne doit pas bloquer le thread
            socketClient.setSoTimeout(DELAI);

            // 1 - Ouverture du flux de la socket
            BufferedReader fluxEntree = new BufferedReader(
                    new InputStreamReader(
                            socketClient.getInputStream(),
                            StandardCharsets.ISO_8859_1));

            StringBuilder requete = new StringBuilder();

            String ligne;

            // 2 - Recevoir la requête, jusqu'à la première ligne vide
            while ((ligne = fluxEntree.readLine()) != null
                    && !ligne.isEmpty()) {

                requete.append(ligne).append("\n");
            }

            // 3 - Afficher la requête (en un seul bloc : plusieurs
            //     clients peuvent écrire en même temps)
            if (requete.length() > 0) {
                System.out.println("--- Requête de "
                        + socketClient.getInetAddress() + " ---\n"
                        + requete.toString().replaceAll("[^\\x20-\\x7E\\n]", "?")
                        + "--- Fin de la requête ---");
            }

            // 4 - Libérer le canal
            fermer();

        } catch (Exception e) {
            // délai dépassé, connexion coupée...
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
