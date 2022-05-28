package com.example.zarusiot.component;

import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetworkScan {
    private boolean scanningStarted;

    public NetworkScan(boolean scanningStarted){
        this.scanningStarted = scanningStarted;
    }

    public boolean isScanningStarted() {
        return scanningStarted;
    }

    public void scanNetworkDevices(Consumer<List<Device>> callback){
        if(scanningStarted) return;
        scanningStarted=true;
        SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
            @Override
            public void onDeviceFound(Device device) {
                // Stub: Found subnet device
            }

            @Override
            public void onFinished(ArrayList<Device> devicesFound) {
                // Stub: Finished scanning
                List<Device> filteredDevices = filterAccesibleDevices(devicesFound);
                //filteredDevices=devicesFound.stream().collect(Collectors.toList());
                callback.accept(filteredDevices);
                scanningStarted=false;
            }
        });
    }

    public boolean checkPort80(String ip){
        try {
            ArrayList<Integer> openPorts = PortScan.onAddress(ip).setMethodTCP().setPort(80).doScan();
            for (Integer integer : openPorts){
                if(integer!=null && integer.intValue()==80) return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidInet4Address(String ip)
    {
        String[] groups = ip.split("\\.",0);

        if (groups.length != 4) return false;

        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() > 0)
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public List<Device> filterAccesibleDevices(List<Device> devicesFound){
        List<Device> devicesFoundFiltered = devicesFound.stream()
                .filter(device -> isValidInet4Address(device.ip))
                .filter(device -> checkPort80(device.ip))
                .collect(Collectors.toList());
        return devicesFoundFiltered;
    }
}
