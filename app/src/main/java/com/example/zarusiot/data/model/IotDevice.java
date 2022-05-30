package com.example.zarusiot.data.model;

import com.example.zarusiot.data.IoTDeviceType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class IotDevice implements Serializable {
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

    public static IotDevice fromSSID(String ssid){
        try {
            String[] deviceSSIDValues = ssid.split("-",0);
            String nameAux = deviceSSIDValues[2];
            String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
            return new IotDevice(nameAux, typeAux, "0.0.0.0");
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
            String nameAux = deviceSSIDValues[2];
            String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
            return new IotDevice(nameAux, typeAux, ip);
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

    public static int searchIndexIotDeviceByName(List<IotDevice> iotDeviceList, String name){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public static int searchIndexByNameAndIp(List<IotDevice> iotDeviceList, String name, String ip){
        for (int i=0; i<iotDeviceList.size(); i++){
            if(iotDeviceList.get(i).getName().equals(name) && iotDeviceList.get(i).getIp().equals(ip))
                return i;
        }
        return -1;
    }

    public static boolean existsInList(List<IotDevice> iotDeviceList, String name, String ip){
        if(searchIndexByNameAndIp(iotDeviceList,name,ip)>=0) return true;
        else return false;
    }

    public static boolean addToListIfNotDuplicated(List<IotDevice> iotDeviceList, IotDevice iotDevice){
        if(!existsInList(iotDeviceList,iotDevice.getName(),iotDevice.getIp())) {
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

