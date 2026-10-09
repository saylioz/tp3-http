package fr.arsir.http.exo4.serveur;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import fr.arsir.http.exo4.service.ServiceRequete;

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
            socketClient.setSoTimeout(DELAI);

            InputStream fluxEntree =
                    new BufferedInputStream(socketClient.getInputStream());

            String requete = requetes.recevoirRequete(fluxEntree);

            if (requete != null) {

                int code = requetes.verifier(requete);

                System.out.println("--- Requête de "
                        + socketClient.getInetAddress() + " ---\n"
                        + requete.replaceAll("[^\\x20-\\x7E\\r\\n]", "?")
                        + "--- Code : " + code + " ---");
            }

            fermer();

        } catch (SocketTimeoutException e) {
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
