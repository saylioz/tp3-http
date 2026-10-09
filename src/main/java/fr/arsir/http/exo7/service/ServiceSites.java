package fr.arsir.http.exo7.service;

import java.util.HashMap;
import java.util.Map;

// Association nom d'hôte (champ Host de la requête) -> site web
public class ServiceSites {

    private final Map<String, ServiceChemin> sites = new HashMap<>();

    public ServiceSites() {

        ServiceChemin site1 = new ServiceChemin("www/site1");
        ServiceChemin site2 = new ServiceChemin("www/site2");

        // les noms *.localhost désignent la machine locale
        sites.put("site1.localhost", site1);
        sites.put("site2.localhost", site2);

        // http://localhost:6666 affiche le site 1 (site par défaut)
        sites.put("localhost", site1);
    }

    // null si aucun site ne correspond à cet hôte
    public ServiceChemin getSite(String hote) {
        return sites.get(hote);
    }
}
