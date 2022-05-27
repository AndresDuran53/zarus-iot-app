package com.example.zarusiot.ui.devices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.IotDevice;

import java.util.ArrayList;
import java.util.List;

public class DevicesViewModel extends ViewModel {

    private final MutableLiveData<List<IotDevice>> discoveredIotDeviceList;
    private final MutableLiveData<String> actionsText;
    private boolean searching;

    public DevicesViewModel() {
        discoveredIotDeviceList = new MutableLiveData<>();
        discoveredIotDeviceList.setValue(new ArrayList<>());
        actionsText = new MutableLiveData<>();
        actionsText.setValue("");
    }

    public LiveData<List<IotDevice>> getDiscoveredIotDeviceList() {
        return discoveredIotDeviceList;
    }

    public MutableLiveData<String> getActionsText() {
        return actionsText;
    }

    public void setActionsText(String value){
        actionsText.setValue(value);
    }

    public boolean isSearching() {
        return searching;
    }

    public void setSearching(boolean state){
        searching = state;
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