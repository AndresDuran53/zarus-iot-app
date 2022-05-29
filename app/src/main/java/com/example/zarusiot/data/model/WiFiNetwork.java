package com.example.zarusiot.data.model;

import java.util.List;

public class WiFiNetwork {
    String ssid;
    String bssid;
    String capabilities;
    String nameDevice;
    String typeDevice;

    public WiFiNetwork(String ssid, String bssid, String capabilities) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.nameDevice = "";
        this.typeDevice = "";
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

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getTypeDevice() {
        return typeDevice;
    }

    public void setTypeDevice(String typeDevice) {
        this.typeDevice = typeDevice;
    }

    public static int searchIndexInList(List<WiFiNetwork> wiFiNetworks, WiFiNetwork wiFiNetwork){
        for (int i=0; i<wiFiNetworks.size(); i++){
            if(wiFiNetworks.get(i).getBssid().equals(wiFiNetwork.getBssid())
            && wiFiNetworks.get(i).getSsid().equals(wiFiNetwork.getSsid())) return i;
        }
        return -1;
    }
}

