package com.example.zarusiot.data.model;

public class IotDeviceListItem {
    private String tittle;
    private String subtitle;

    public IotDeviceListItem(String tittle, String subtitle) {
        this.tittle = tittle;
        this.subtitle = subtitle;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
