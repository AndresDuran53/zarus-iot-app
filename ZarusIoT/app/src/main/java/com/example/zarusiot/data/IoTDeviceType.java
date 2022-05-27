package com.example.zarusiot.data;

public enum IoTDeviceType {
    UNKNOWN("unknown","UNKNOWN"),
    RGB_LIGHT("rgb-light","RGB Light"),
    GARAGE_SENSOR("garage-sensor","Garage Sensor"),
    SPEAKER_SWITCH("speaker-switch","Speaker Switch");

    private String value;
    private String name;

    IoTDeviceType(String value, String name){
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static IoTDeviceType findByValue(String value){
        for(IoTDeviceType ioTDeviceType : values()){
            if( ioTDeviceType.getValue().equals(value)){
                return ioTDeviceType;
            }
        }
        return UNKNOWN;
    }
}
