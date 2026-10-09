package fr.arsir.http.exo6.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServiceChemin {

    private final Path racine;

    public ServiceChemin(String dossier) {
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
    }

    public Path construireChemin(String requete) {

        String adresse = requete.split("\\s+")[1];

        if (adresse.startsWith("http://") || adresse.startsWith("https://")) {

            int debut = adresse.indexOf('/', adresse.indexOf("//") + 2);

            adresse = (debut == -1) ? "/" : adresse.substring(debut);
        }

        int question = adresse.indexOf('?');

        if (question != -1) {
            adresse = adresse.substring(0, question);
        }

        adresse = decoder(adresse);

        if (adresse.indexOf('\0') != -1 || adresse.indexOf('\\') != -1) {
            return null;
        }

        Path chemin = racine.resolve(adresse.replaceFirst("^/+", "")).normalize();

        if (Files.isDirectory(chemin)) {
            chemin = chemin.resolve("index.html");
        }

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
