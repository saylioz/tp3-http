package fr.arsir.http.exo7.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceSites {

    private final Map<String, ServiceChemin> sites = new HashMap<>();

    public ServiceSites() {

        ServiceChemin site1 = new ServiceChemin("www/site1");
        ServiceChemin site2 = new ServiceChemin("www/site2");

        sites.put("site1.localhost", site1);
        sites.put("site2.localhost", site2);

        sites.put("localhost", site1);
    }

    public ServiceChemin getSite(String hote) {
        return sites.get(hote);
    }
}
