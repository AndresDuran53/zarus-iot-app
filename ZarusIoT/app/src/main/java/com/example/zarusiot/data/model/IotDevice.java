package com.example.zarusiot.data.model;

import java.util.List;

public class IotDevice {
    String name = "Unknown";
    String type = "Unknown";
    String ip = "Unknown";
    boolean added = false;

    public IotDevice() {

    }

    public IotDevice(String ip) {
        this.ip = ip;
    }

    public IotDevice(String name, String type, String ip) {
        this.name = name;
        this.type = type;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public static int searchIndexIotDeviceByIp(List<IotDevice> iotDeviceList, String ip){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getIp().equals(ip)) return i;
        }
        return -1;
    }

    public static int searchIndexIotDeviceByName(List<IotDevice> iotDeviceList, String name){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public static boolean existsInList(List<IotDevice> iotDeviceList, String name, String ip){
        if(searchIndexIotDeviceByIp(iotDeviceList,ip)>=0 && searchIndexIotDeviceByName(iotDeviceList,name)>=0) return true;
        else return false;
    }

    public static boolean addToListIfNotDuplicated(List<IotDevice> iotDeviceList, IotDevice iotDevice){
        if(!existsInList(iotDeviceList,iotDevice.getName(),iotDevice.getIp())) {
            iotDeviceList.add(iotDevice);
            return true;
        }
        return false;
    }
}

