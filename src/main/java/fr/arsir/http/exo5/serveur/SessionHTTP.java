package fr.arsir.http.exo5.serveur;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import fr.arsir.http.exo5.service.ServiceReponse;
import fr.arsir.http.exo5.service.ServiceRequete;

public class SessionHTTP extends Thread {

    private static final int DELAI = 10000;

    private final Socket socketClient;
    private final ServiceRequete requetes;
    private final ServiceReponse reponses;

    public SessionHTTP(Socket socketClient,
                       ServiceRequete requetes,
                       ServiceReponse reponses) {

        this.socketClient = socketClient;
        this.requetes = requetes;
        this.reponses = reponses;
    }

    @Override
    public void run() {

        try {
            socketClient.setSoTimeout(DELAI);

            InputStream fluxEntree =
                    new BufferedInputStream(socketClient.getInputStream());

            OutputStream fluxSortie = socketClient.getOutputStream();

            String requete;

            try {
                requete = requetes.recevoirRequete(fluxEntree);

            } catch (SocketTimeoutException e) {
                fermer();
                return;

            } catch (IOException e) {
                envoyer(fluxSortie, reponses.genererReponseErreur(400));
                fermer();
                return;
            }

            if (requete == null) {
                fermer();
                return;
            }

            int code = requetes.verifier(requete);

            String reponse;

            if (code == 200) {
                String page = "<html><body><h1>Serveur HTTP</h1>"
                        + "<p>Requete valide.</p></body></html>\r\n";

                reponse = reponses.genererEntete(200, page.length()) + page;

            } else {
                reponse = reponses.genererReponseErreur(code);
            }

            envoyer(fluxSortie, reponse);

            System.out.println(socketClient.getInetAddress() + " : "
                    + extraire(requete) + " -> " + code);

            fermer();

        } catch (Exception e) {
            System.err.println("Erreur session : " + e.getMessage());
            fermer();
        }
    }

    private void envoyer(OutputStream fluxSortie, String reponse)
            throws IOException {

        fluxSortie.write(reponse.getBytes(StandardCharsets.ISO_8859_1));
        fluxSortie.flush();
    }

    private String extraire(String requete) {

        String ligne = requete.split("\r?\n", 2)[0];

        return ligne.replaceAll("[^\\x20-\\x7E]", "?");
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
