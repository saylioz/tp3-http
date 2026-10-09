package fr.arsir.http.exo3.serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SessionHTTP extends Thread {

    private static final int DELAI = 10000;

    private final Socket socketClient;

    public SessionHTTP(Socket socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void run() {

        try {
            socketClient.setSoTimeout(DELAI);

            BufferedReader fluxEntree = new BufferedReader(
                    new InputStreamReader(
                            socketClient.getInputStream(),
                            StandardCharsets.ISO_8859_1));

            StringBuilder requete = new StringBuilder();

            String ligne;

            while ((ligne = fluxEntree.readLine()) != null
                    && !ligne.isEmpty()) {

                requete.append(ligne).append("\n");
            }

            if (requete.length() > 0) {
                System.out.println("--- Requête de "
                        + socketClient.getInetAddress() + " ---\n"
                        + requete.toString().replaceAll("[^\\x20-\\x7E\\n]", "?")
                        + "--- Fin de la requête ---");
            }

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
        }
    }
}
