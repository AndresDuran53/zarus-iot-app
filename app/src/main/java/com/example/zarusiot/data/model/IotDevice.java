package com.example.zarusiot.data.model;

import com.example.zarusiot.data.IoTDeviceType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class IotDevice implements Serializable {
    String id = "";
    String name = "";
    String type = "";
    String ip = "";
    String mac = "";
    boolean added = false;
    boolean lastStatusConnected = true;

    public IotDevice() {

    }

    public IotDevice(String id, String type, String ip) {
        this.id = id;
        this.name = type + ": " +id;
        this.type = type;
        this.ip = ip;
    }

    public IotDevice(String id, String name, String type, String ip) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ip = ip;
    }

    public IotDevice(String id, String name, String type, String ip, String mac) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ip = ip;
        this.mac = mac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if(name.isEmpty()) name = type + ": " +id;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public boolean isLastStatusConnected() {
        return lastStatusConnected;
    }

    public void setLastStatusConnected(boolean lastStatusConnected) {
        this.lastStatusConnected = lastStatusConnected;
    }

    public static IotDevice fromSSID(String ssid){
        try {
            String[] deviceSSIDValues = ssid.split("-",0);
            String idAux = deviceSSIDValues[2];
            String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
            return new IotDevice(idAux, typeAux, "0.0.0.0");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static IotDevice fromJson(String json,String ip){
        try {
            String[] deviceSSIDValues = new JSONObject(json)
                    .getString("deviceSSID")
                    .split("-",0);
            String idAux = deviceSSIDValues[2];
            String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
            return new IotDevice(idAux, typeAux, ip);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static int searchIndexIotDeviceByIp(List<IotDevice> iotDeviceList, String ip){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getIp().equals(ip)) return i;
        }
        return -1;
    }

    public static int searchIndexByIdAndIp(List<IotDevice> iotDeviceList, String id, String ip){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getId().equals(id) && iotDeviceList.get(i).getIp().equals(ip))
                return i;
        }
        return -1;
    }

    public static boolean existsInList(List<IotDevice> iotDeviceList, String id, String ip){
        if(searchIndexByIdAndIp(iotDeviceList,id,ip)>=0) return true;
        else return false;
    }

    public static boolean addToListIfNotDuplicated(List<IotDevice> iotDeviceList, IotDevice iotDevice){
        if(!existsInList(iotDeviceList,iotDevice.getId(),iotDevice.getIp())) {
            iotDeviceList.add(iotDevice);
            return true;
        }
        return false;
    }

    public static boolean validZarusDeviceResponse(String response){
        try {
            String deviceSSID = new JSONObject(response).getString("deviceSSID");
            String[] deviceSSIDValues = deviceSSID.split("-",0);
            return deviceSSIDValues.length>=3;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidSSID(String deviceSSID){
        String[] deviceSSIDValues = deviceSSID.split("-",0);
        if(deviceSSIDValues.length>=3){
            String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
            IoTDeviceType typeFound = IoTDeviceType.findByValue(typeAux);
            if(!typeFound.equals(IoTDeviceType.UNKNOWN)) return true;
        }
        return false;
    }


}

