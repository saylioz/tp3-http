package fr.arsir.http.exo7.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceRequete {

    private static final int TAILLE_MAX = 16384;

    private static final Pattern LIGNE_REQUETE =
            Pattern.compile("^([A-Z]+) (\\S+) HTTP/(\\d\\.\\d)$");

    private static final Pattern LIGNE_ENTETE =
            Pattern.compile("^([A-Za-z0-9-]+):(.*)$");

    public String recevoirRequete(InputStream flux) throws IOException {

        StringBuilder requete = new StringBuilder();

        int longueurLigne = 0;
        int lu;

        try {
            while ((lu = flux.read()) != -1) {

                if (requete.length() >= TAILLE_MAX) {
                    throw new IOException("requête trop longue");
                }

                requete.append((char) lu);

                if (lu == '\n') {

                    if (longueurLigne == 0) {
                        if (requete.toString().trim().isEmpty()) {
                            requete.setLength(0);
                        } else {
                            break;
                        }
                    }
                    longueurLigne = 0;

                } else if (lu != '\r') {
                    longueurLigne++;
                }
            }

        } catch (SocketTimeoutException e) {
            if (requete.length() == 0) {
                throw e;
            }
        }

        return (requete.length() == 0) ? null : requete.toString();
    }

    public int verifier(String requete) {

        if (!requete.endsWith("\n\r\n") && !requete.endsWith("\n\n")) {
            return 400;
        }

        String[] lignes = requete.split("\r?\n");

        if (lignes.length == 0) {
            return 400;
        }

        Matcher m = LIGNE_REQUETE.matcher(lignes[0]);

        if (!m.matches()) {
            return 400;
        }

        for (int i = 1; i < lignes.length; i++) {
            if (!LIGNE_ENTETE.matcher(lignes[i]).matches()) {
                return 400;
            }
        }

        if (!m.group(1).equals("GET")) {
            return 405;
        }

        String cible = m.group(2);

        if (!cible.startsWith("/")
                && !cible.startsWith("http://")
                && !cible.startsWith("https://")) {
            return 400;
        }

        return 200;
    }

    public String getHote(String requete) {

        String[] lignes = requete.split("\r?\n");

        for (int i = 1; i < lignes.length; i++) {

            Matcher m = LIGNE_ENTETE.matcher(lignes[i]);

            if (m.matches() && m.group(1).equalsIgnoreCase("Host")) {

                String hote = m.group(2).trim().toLowerCase();

                if (hote.startsWith("[")) {
                    hote = hote.substring(0, hote.indexOf(']') + 1);
                } else if (hote.indexOf(':') != -1) {
                    hote = hote.substring(0, hote.indexOf(':'));
                }

                return hote.isEmpty() ? null : hote;
            }
        }
        return null;
    }
}
