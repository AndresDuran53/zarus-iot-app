package com.example.zarusiot.ui.devices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zarusiot.data.model.IotDevice;

import java.util.ArrayList;
import java.util.List;

public class DevicesViewModel extends ViewModel {

    private final MutableLiveData<List<IotDevice>> discoveredIotDeviceList;

    public DevicesViewModel() {
        discoveredIotDeviceList = new MutableLiveData<>();
        discoveredIotDeviceList.setValue(new ArrayList<>());
    }

    public LiveData<List<IotDevice>> getDiscoveredIotDeviceList() {
        return discoveredIotDeviceList;
    }

    public void setDiscoveredIotDeviceList(List<IotDevice> pStoredIotDeviceList) {
        this.discoveredIotDeviceList.setValue(pStoredIotDeviceList);
    }

    public boolean deviceAlreadyAdded(IotDevice iotDevice){
        return IotDevice.existsInList((List<IotDevice>)discoveredIotDeviceList.getValue(),iotDevice.getName(),iotDevice.getIp());
    }

    public void addToDiscoveredIotDeviceList(IotDevice iotDevice) {
        if(!deviceAlreadyAdded(iotDevice))
            this.discoveredIotDeviceList.getValue().add(iotDevice);
    }
}