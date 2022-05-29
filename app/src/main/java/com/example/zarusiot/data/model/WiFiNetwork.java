package com.example.zarusiot.data.model;

import java.util.List;

public class WiFiNetwork {
    String ssid;
    String bssid;
    String capabilities;

    public WiFiNetwork(String ssid, String bssid, String capabilities) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public static int searchIndexInList(List<WiFiNetwork> wiFiNetworks, WiFiNetwork wiFiNetwork){
        for (int i=0; i<wiFiNetworks.size(); i++){
            if(wiFiNetworks.get(i).getBssid().equals(wiFiNetwork.getBssid())
            && wiFiNetworks.get(i).getSsid().equals(wiFiNetwork.getSsid())) return i;
        }
        return -1;
    }
}

