package fr.arsir.http.exo5.service;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ServiceReponse {

    private static final String NOM_SERVEUR = "ServeurHTTP-ARSIR/1.0";

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss zzz", Locale.FRENCH);

    private static final Map<Integer, String> MESSAGES = new HashMap<>();

    static {
        MESSAGES.put(200, "OK");
        MESSAGES.put(400, "Bad Request");
        MESSAGES.put(403, "Forbidden");
        MESSAGES.put(404, "Not Found");
        MESSAGES.put(405, "Method Not Allowed");
        MESSAGES.put(500, "Internal Server Error");
    }

    public String genererEntete(int code) {
        return genererEntete(code, -1);
    }

    public String genererEntete(int code, long taille) {

        if (!MESSAGES.containsKey(code)) {
            code = 500;
        }

        StringBuilder entete = new StringBuilder();

        entete.append("HTTP/1.1 ").append(code).append(" ")
                .append(MESSAGES.get(code)).append("\r\n");

        entete.append("Date: ")
                .append(ZonedDateTime.now().format(FORMAT_DATE)).append("\r\n");

        entete.append("Server: ").append(NOM_SERVEUR).append("\r\n");

        entete.append("Connection: close\r\n");

        if (taille >= 0) {
            entete.append("Content-Length: ").append(taille).append("\r\n");
        }

        entete.append("Content-Type: text/html\r\n");

        entete.append("\r\n");

        return entete.toString();
    }

    public String genererReponseErreur(int code) {

        if (!MESSAGES.containsKey(code)) {
            code = 500;
        }

        String titre = code + " " + MESSAGES.get(code);

        String page = "<html><head><title>" + titre + "</title></head>"
                + "<body><h1>" + titre + "</h1></body></html>\r\n";

        long taille = page.getBytes(StandardCharsets.ISO_8859_1).length;

        return genererEntete(code, taille) + page;
    }
}
