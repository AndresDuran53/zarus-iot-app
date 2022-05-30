package com.example.zarusiot.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zarusiot.data.model.IotDevice;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<IotDevice>> storedIotDeviceList;

    public HomeViewModel() {
        storedIotDeviceList = new MutableLiveData<>();
        storedIotDeviceList.setValue(new ArrayList<>());
    }

    public LiveData<List<IotDevice>> getStoredIotDeviceList() {
        return storedIotDeviceList;
    }

    public void setStoredIotDeviceList(List<IotDevice> pStoredIotDeviceList) {
        this.storedIotDeviceList.setValue(pStoredIotDeviceList);
    }

    public boolean deviceAlreadyAdded(IotDevice iotDevice){
        return IotDevice.existsInList((List<IotDevice>)storedIotDeviceList.getValue(),iotDevice.getId(),iotDevice.getIp());
    }

    public void addToIotDeviceList(IotDevice iotDevice) {
        if(!deviceAlreadyAdded(iotDevice))
        this.storedIotDeviceList.getValue().add(iotDevice);
    }
}