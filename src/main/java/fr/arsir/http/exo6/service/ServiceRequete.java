package fr.arsir.http.exo6.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceRequete {

    // taille maximale d'une requête (protection contre les requêtes sans fin)
    private static final int TAILLE_MAX = 16384;

    // ligne de requête : METHODE cible HTTP/x.y
    private static final Pattern LIGNE_REQUETE =
            Pattern.compile("^([A-Z]+) (\\S+) HTTP/(\\d\\.\\d)$");

    // ligne d'en-tête : clé:valeur
    private static final Pattern LIGNE_ENTETE =
            Pattern.compile("^([A-Za-z0-9-]+):(.*)$");

    // Q1 : reçoit une requête HTTP (jusqu'à la première ligne vide)
    // et la stocke sous forme de chaîne de caractères
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
                        // les lignes vides avant la requête sont ignorées,
                        // sinon la ligne vide marque la fin de la requête
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
            // requête incomplète : on la garde pour la vérifier ;
            // si rien n'a été reçu, la connexion était simplement inactive
            if (requete.length() == 0) {
                throw e;
            }
        }

        return (requete.length() == 0) ? null : requete.toString();
    }

    // Q2 : vérifie qu'une requête est bien formée
    //   400 : requête incorrecte
    //   405 : méthode non supportée (seule GET l'est)
    //   200 : tout est correct
    public int verifier(String requete) {

        // la requête doit se terminer par une ligne vide
        if (!requete.endsWith("\n\r\n") && !requete.endsWith("\n\n")) {
            return 400;
        }

        String[] lignes = requete.split("\r?\n");

        if (lignes.length == 0) {
            return 400;
        }

        // première ligne : METHODE cible HTTP/x.y
        Matcher m = LIGNE_REQUETE.matcher(lignes[0]);

        if (!m.matches()) {
            return 400;
        }

        // lignes suivantes : en-têtes clé:valeur
        for (int i = 1; i < lignes.length; i++) {
            if (!LIGNE_ENTETE.matcher(lignes[i]).matches()) {
                return 400;
            }
        }

        if (!m.group(1).equals("GET")) {
            return 405;
        }

        // la cible est soit /chemin, soit http://hote:port/chemin
        String cible = m.group(2);

        if (!cible.startsWith("/")
                && !cible.startsWith("http://")
                && !cible.startsWith("https://")) {
            return 400;
        }

        return 200;
    }
}
