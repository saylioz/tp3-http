package fr.arsir.http.exo6.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final Map<String, String> TYPES = new HashMap<>();

    static {
        MESSAGES.put(200, "OK");
        MESSAGES.put(400, "Bad Request");
        MESSAGES.put(403, "Forbidden");
        MESSAGES.put(404, "Not Found");
        MESSAGES.put(405, "Method Not Allowed");
        MESSAGES.put(500, "Internal Server Error");

        TYPES.put("html", "text/html; charset=UTF-8");
        TYPES.put("htm", "text/html; charset=UTF-8");
        TYPES.put("css", "text/css; charset=UTF-8");
        TYPES.put("js", "application/javascript; charset=UTF-8");
        TYPES.put("txt", "text/plain; charset=UTF-8");
        TYPES.put("png", "image/png");
        TYPES.put("jpg", "image/jpeg");
        TYPES.put("jpeg", "image/jpeg");
        TYPES.put("gif", "image/gif");
        TYPES.put("svg", "image/svg+xml");
        TYPES.put("ico", "image/x-icon");
        TYPES.put("pdf", "application/pdf");
        TYPES.put("mp3", "audio/mpeg");
    }

    public String genererEntete(int code) {
        return genererEntete(code, -1, "text/html");
    }

    public String genererEntete(int code, long taille) {
        return genererEntete(code, taille, "text/html");
    }

    public String genererEntete(int code, long taille, String type) {

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

        entete.append("Content-Type: ").append(type).append("\r\n");

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

    public byte[] genererReponse(Path fichier) throws IOException {

        byte[] contenu = Files.readAllBytes(fichier);

        String entete = genererEntete(
                200, contenu.length, typeContenu(fichier));

        byte[] octetsEntete = entete.getBytes(StandardCharsets.ISO_8859_1);

        byte[] reponse = new byte[octetsEntete.length + contenu.length];

        System.arraycopy(octetsEntete, 0, reponse, 0, octetsEntete.length);
        System.arraycopy(contenu, 0, reponse, octetsEntete.length, contenu.length);

        return reponse;
    }

    private String typeContenu(Path fichier) {

        String nom = fichier.getFileName().toString().toLowerCase();

        int point = nom.lastIndexOf('.');

        if (point == -1) {
            return "application/octet-stream";
        }
        return TYPES.getOrDefault(
                nom.substring(point + 1), "application/octet-stream");
    }
}
