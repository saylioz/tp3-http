package fr.arsir.http.exo7.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServiceChemin {

    // dossier du site web
    private final Path racine;

    public ServiceChemin(String dossier) {
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
    }

    // Q1 : construit le chemin du fichier demandé par une requête.
    // Retourne null si l'adresse sort du dossier du site.
    public Path construireChemin(String requete) {

        // adresse demandée = 2e élément de la ligne de requête
        String adresse = requete.split("\\s+")[1];

        // http://hote:port/chemin -> /chemin
        if (adresse.startsWith("http://") || adresse.startsWith("https://")) {

            int debut = adresse.indexOf('/', adresse.indexOf("//") + 2);

            adresse = (debut == -1) ? "/" : adresse.substring(debut);
        }

        // on supprime tout ce qui suit un éventuel ? (inclus)
        int question = adresse.indexOf('?');

        if (question != -1) {
            adresse = adresse.substring(0, question);
        }

        // %xx -> caractère correspondant
        adresse = decoder(adresse);

        if (adresse.indexOf('\0') != -1 || adresse.indexOf('\\') != -1) {
            return null;
        }

        Path chemin = racine.resolve(adresse.replaceFirst("^/+", "")).normalize();

        // un dossier est remplacé par son fichier index.html
        if (Files.isDirectory(chemin)) {
            chemin = chemin.resolve("index.html");
        }

        // le chemin doit rester dans le dossier du site (.. et liens symboliques)
        if (!chemin.startsWith(racine)) {
            return null;
        }

        try {
            if (Files.exists(chemin)
                    && !chemin.toRealPath().startsWith(racine.toRealPath())) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        return chemin;
    }

    // remplace chaque séquence %xx par l'octet correspondant
    // (les octets sont ensuite lus comme de l'UTF-8 : %C3%A9 -> é)
    private String decoder(String texte) {

        ByteArrayOutputStream octets = new ByteArrayOutputStream();

        for (int i = 0; i < texte.length(); i++) {

            char c = texte.charAt(i);

            if (c == '%' && i + 2 < texte.length()) {

                int haut = Character.digit(texte.charAt(i + 1), 16);
                int bas = Character.digit(texte.charAt(i + 2), 16);

                if (haut != -1 && bas != -1) {
                    octets.write(haut * 16 + bas);
                    i += 2;
                    continue;
                }
            }
            octets.write(c);
        }
        return new String(octets.toByteArray(), StandardCharsets.UTF_8);
    }
}
