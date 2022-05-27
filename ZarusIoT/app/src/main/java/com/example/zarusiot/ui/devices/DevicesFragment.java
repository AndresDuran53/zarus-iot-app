package com.example.zarusiot.ui.devices;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.zarusiot.R;
import com.example.zarusiot.data.model.IotDevice;
import com.example.zarusiot.databinding.FragmentDevicesBinding;
import com.example.zarusiot.ui.DiscoveredListViewItemAdapter;
import com.example.zarusiot.ui.home.HomeViewModel;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevicesFragment extends Fragment {

    private DevicesViewModel devicesViewModel;
    private HomeViewModel homeViewModel;
    private FragmentDevicesBinding binding;
    private WifiManager wifiManager;
    private List<IotDevice> iotDeviceDiscoveredList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Shared Data
        devicesViewModel = new ViewModelProvider(requireActivity()).get(DevicesViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context applicationContext = getActivity().getApplicationContext();
        wifiManager = (WifiManager)applicationContext.getSystemService(WIFI_SERVICE);
        iotDeviceDiscoveredList = new ArrayList<>();
        devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
        final View button = root.findViewById(R.id.buttonScanNetwork);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setButtonState(false);
                        iotDeviceDiscoveredList.clear();
                        devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
                        setText("Scanning all devices...");
                        SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                            @Override
                            public void onDeviceFound(Device device) {
                                // Stub: Found subnet device
                            }

                            @Override
                            public void onFinished(ArrayList<Device> devicesFound) {
                                // Stub: Finished scanning
                                saveToIotDevicesList(devicesFound);
                            }
                        });
                    }
                }
        );
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void callGetRequests(String ip){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url ="http://"+ip+"/getRedInformation";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response.charAt(0) == '{'){
                            try {
                                String deviceSSID = new JSONObject(response).getString("deviceSSID");
                                String[] deviceSSIDValues = deviceSSID.split("-",0);

                                if(deviceSSIDValues.length>=3){
                                    String nameAux = deviceSSIDValues[2];
                                    String typeAux = deviceSSIDValues[0]+"-"+deviceSSIDValues[1];
                                    IotDevice iotDeviceAux = new IotDevice(nameAux, typeAux, ip);
                                    boolean deviceAlreadyStored = homeViewModel.deviceAlreadyAdded(iotDeviceAux);
                                    IotDevice.addToListIfNotDuplicated(iotDeviceDiscoveredList,iotDeviceAux);
                                    if(deviceAlreadyStored){
                                        int deviceDuplicatedIndex =
                                                IotDevice.searchIndexIotDeviceByIp(iotDeviceDiscoveredList,ip);
                                        iotDeviceDiscoveredList.get(deviceDuplicatedIndex).setAdded(true);
                                    }

                                    devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        updateListView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                updateListView();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean checkPort80(String ip){
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
    
    private void saveToIotDevicesList(List<Device> devicesFound){
        setText("Looking for Zarus devices...");
        int devicesfound = 0;
        for (Device device : devicesFound){
            if(!isValidInet4Address(device.ip)) continue;
            if(checkPort80(device.ip)){
                devicesfound++;
                callGetRequests(device.ip);
            }
            /**/

            /*devicesfound++;
            String nameAux = "Name:"+device.ip;
            String typeAux = "Type:"+device.ip;
            IotDevice iotDeviceAux = new IotDevice(nameAux, typeAux, device.ip);
            boolean deviceAlreadyStored = homeViewModel.deviceAlreadyAdded(iotDeviceAux);
            IotDevice.addToListIfNotDuplicated(iotDeviceDiscoveredList,iotDeviceAux);
            if(deviceAlreadyStored){
                int deviceDuplicatedIndex =
                        IotDevice.searchIndexIotDeviceByIp(iotDeviceDiscoveredList,device.ip);
                iotDeviceDiscoveredList.get(deviceDuplicatedIndex).setAdded(true);
            }

            devicesViewModel.addToDiscoveredIotDeviceList(new IotDevice(
                    "Name:"+device.ip,
                    "Type:"+device.ip,
                    device.ip));
            updateListView();

            /**/
        }
        if(devicesfound==0) setText("No Devices Found.");
        setButtonState(true);
    }

    // Deprecated of formatIpAddress() because the function doesn't support ipv6
    // but neither does WifiInfo.
    @SuppressWarnings("deprecation")
    private String getOwnIp(){
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ownIp = Formatter.formatIpAddress(ip);
        return ownIp;
    }

    public static boolean isValidInet4Address(String ip)
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

    private void updateListView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = binding.getRoot().findViewById(R.id.listViewDevices);

                DiscoveredListViewItemAdapter discoveredListViewItemAdapter = new DiscoveredListViewItemAdapter(requireActivity(), iotDeviceDiscoveredList);
                listView.setAdapter(discoveredListViewItemAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        IotDevice iotDevice = iotDeviceDiscoveredList.get(position);
                        if(!iotDevice.isAdded()){
                            iotDevice.setAdded(true);
                            TextView addText = (TextView) view.findViewById(R.id.discoveredIotAdd);;
                            addText.setText(R.string.already_added);
                            addText.setTextColor(ContextCompat.getColor(getContext(), R.color.zarus_text_muted));
                            homeViewModel.addToIotDeviceList(iotDevice);
                        }
                    }
                });
                if(iotDeviceDiscoveredList.size()>0) setText(iotDeviceDiscoveredList.size()+" Devices found.");
                else setText("No Devices Found.");
            }
        });
    }

    private void setText(String text){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView textView = binding.textTest;
                textView.setText(text);
            }
        });
    }

    private void setButtonState(boolean state){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Button buttonScanNetwork = binding.buttonScanNetwork;
                buttonScanNetwork.setEnabled(state);
            }
        });
    }
}
