package fr.arsir.http.exo7.serveur;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.arsir.http.exo7.service.ServiceChemin;
import fr.arsir.http.exo7.service.ServiceReponse;
import fr.arsir.http.exo7.service.ServiceRequete;
import fr.arsir.http.exo7.service.ServiceSites;

public class SessionHTTP extends Thread {

    private static final int DELAI = 10000;

    private final Socket socketClient;
    private final ServiceRequete requetes;
    private final ServiceReponse reponses;
    private final ServiceSites sites;

    public SessionHTTP(Socket socketClient,
                       ServiceRequete requetes,
                       ServiceReponse reponses,
                       ServiceSites sites) {

        this.socketClient = socketClient;
        this.requetes = requetes;
        this.reponses = reponses;
        this.sites = sites;
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
                fluxSortie.write(erreur(400));
                fluxSortie.flush();
                fermer();
                return;
            }

            if (requete == null) {
                fermer();
                return;
            }

            byte[] reponse;

            try {
                reponse = repondre(requete);
            } catch (IOException e) {
                reponse = erreur(500);
            }

            fluxSortie.write(reponse);
            fluxSortie.flush();

            System.out.println(socketClient.getInetAddress() + " : "
                    + extraire(requete) + " -> " + extraire(new String(
                            reponse, 0, Math.min(reponse.length, 40),
                            StandardCharsets.ISO_8859_1)));

            fermer();

        } catch (Exception e) {
            System.err.println("Erreur session : " + e.getMessage());
            fermer();
        }
    }

    private byte[] repondre(String requete) throws IOException {

        int code = requetes.verifier(requete);

        if (code != 200) {
            return erreur(code);
        }

        String hote = requetes.getHote(requete);

        if (hote == null) {
            return erreur(400);
        }

        ServiceChemin site = sites.getSite(hote);

        if (site == null) {
            return erreur(404);
        }

        Path chemin = site.construireChemin(requete);

        if (chemin == null) {
            return erreur(403);
        }

        if (!Files.isRegularFile(chemin)) {
            return erreur(404);
        }

        return reponses.genererReponse(chemin);
    }

    private byte[] erreur(int code) {
        return reponses.genererReponseErreur(code)
                .getBytes(StandardCharsets.ISO_8859_1);
    }

    private String extraire(String message) {

        String ligne = message.split("\r?\n", 2)[0];

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
