package com.example.zarusiot.ui.devices;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.zarusiot.R;
import com.example.zarusiot.component.HttpRequest;
import com.example.zarusiot.component.NetworkScan;
import com.example.zarusiot.data.model.IotDevice;
import com.example.zarusiot.databinding.FragmentDevicesBinding;
import com.example.zarusiot.ui.DiscoveredListViewItemAdapter;
import com.example.zarusiot.ui.WifiScannerActivity;
import com.example.zarusiot.ui.home.HomeViewModel;
import com.stealthcopter.networktools.subnet.Device;

import java.util.List;
import java.util.stream.Collectors;

public class DevicesFragment extends Fragment {

    private static FragmentDevicesBinding binding;
    private static FragmentActivity fragmentActivity;

    private DevicesViewModel devicesViewModel;
    private HomeViewModel homeViewModel;
    private NetworkScan networkScan;
    private HttpRequest httpRequest;
    private List<IotDevice> iotDeviceDiscoveredList;
    private TextView textWarningNoWifi;
    private ListView listViewDevices;
    private TextView textActionHeader;
    private Button buttonScanNetwork;
    private TextView textScanWifi;
    private Button buttonScannerWifi;
    private Button buttonRefresh;
    private ActivityResultLauncher<Intent> wifiScanActivityResultLauncher;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fragmentActivity = getActivity();

        networkScan = new NetworkScan();
        httpRequest = new HttpRequest(getContext());

        //UI objects
        textWarningNoWifi = binding.warningNoWifi;
        listViewDevices = binding.listViewDevices;
        textActionHeader = binding.textViewActionHeader;
        buttonScanNetwork = binding.buttonScanNetwork;
        textScanWifi = binding.textViewScanWifi;
        buttonScannerWifi = binding.buttonScannerWifi;
        buttonRefresh = binding.buttonRefresh;

        //Shared Data
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        devicesViewModel = new ViewModelProvider(requireActivity()).get(DevicesViewModel.class);
        iotDeviceDiscoveredList = devicesViewModel.getDiscoveredIotDeviceList().getValue();
        if(devicesViewModel.getActionsText().getValue().equals("")){
            devicesViewModel.setActionsText(getString(R.string.search_for_network_devices));
        }
        devicesViewModel.setScanNetworkEnable(!devicesViewModel.isSearching());

        devicesViewModel.getActionsText().observe(getViewLifecycleOwner(),
                new Observer<String>() {
            @Override
            public void onChanged(String value) {
                textActionHeader.setText(value);
            }
        });

        devicesViewModel.getScanNetworkEnable().observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean state) {
                buttonScanNetwork.setEnabled(Boolean.valueOf(state));
            }
        });

        //Setting OnClick actions
        buttonScanNetwork.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartScanningNetwork();
                    }
                }
        );

        buttonScannerWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedWifi()) {
                    Intent intent = new Intent(getContext(), WifiScannerActivity.class);
                    wifiScanActivityResultLauncher.launch(intent);
                } else updateUiByConnectionWiFi(false);
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListView();
            }
        });


        //Register Activity on Result
        wifiScanActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        updateListView();
                    }
                });

        updateListView();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void StartScanningNetwork(){
        if(isConnectedWifi()){
            try{
                devicesViewModel.setSearching(true);
                devicesViewModel.setScanNetworkEnable(false);
                iotDeviceDiscoveredList.clear();
                updateListView();
                devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
                devicesViewModel.setActionsText(getString(R.string.scanning_all_devices));
                networkScan.scanNetworkDevices((devicesFound) -> validatingZarusDevice(devicesFound));
            }catch (Exception e){
                devicesViewModel.setSearching(false);
                devicesViewModel.setScanNetworkEnable(true);
            }
        } else updateUiByConnectionWiFi(false);
    }

    private void updateUiByConnectionWiFi(Boolean isConnectedWifi){
        try{
            if(isConnectedWifi){
                listViewDevices.setVisibility(View.VISIBLE);
                textActionHeader.setVisibility(View.VISIBLE);
                buttonScanNetwork.setVisibility(View.VISIBLE);
                textScanWifi.setVisibility(View.VISIBLE);
                buttonScannerWifi.setVisibility(View.VISIBLE);
                textWarningNoWifi.setVisibility(View.INVISIBLE);
                buttonRefresh.setVisibility(View.INVISIBLE);
            }
            else{
                listViewDevices.setVisibility(View.INVISIBLE);
                textActionHeader.setVisibility(View.INVISIBLE);
                buttonScanNetwork.setVisibility(View.INVISIBLE);
                textScanWifi.setVisibility(View.INVISIBLE);
                buttonScannerWifi.setVisibility(View.INVISIBLE);
                textWarningNoWifi.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isConnectedWifi() {
        ConnectivityManager cm =
                (ConnectivityManager)fragmentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    private void validatingZarusDevice(List<Device> devicesFound){
        if(devicesFound.size()!=0){
            List<String> listIp = devicesFound.stream().map(device -> device.ip).collect(Collectors.toList());
            httpRequest.callGetListRequests(listIp,
                    IotDevice::validZarusDeviceResponse,
                    this::saveToIotDevicesList);
        }
        devicesViewModel.setSearching(false);
        updateListView();
    }

    public void saveToIotDevicesList(String ip,String response){
        IotDevice iotDeviceAux = IotDevice.fromJson(response,ip);
        boolean deviceAlreadyStored = homeViewModel.deviceAlreadyAdded(iotDeviceAux);
        IotDevice.addToListIfNotDuplicated(iotDeviceDiscoveredList,iotDeviceAux);
        if(deviceAlreadyStored){
            int deviceDuplicatedIndex =
                    IotDevice.searchIndexIotDeviceByIp(iotDeviceDiscoveredList,ip);
            iotDeviceDiscoveredList.get(deviceDuplicatedIndex).setAdded(true);
        }
        devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
        updateListView();
    }

    private void updateListView(){
        if(DevicesFragment.fragmentActivity==null || DevicesFragment.binding==null) return;
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateUiByConnectionWiFi(isConnectedWifi());
                ListView listView =
                        DevicesFragment.binding.getRoot().findViewById(R.id.listViewDevices);

                DiscoveredListViewItemAdapter discoveredListViewItemAdapter =
                        new DiscoveredListViewItemAdapter(fragmentActivity, iotDeviceDiscoveredList);

                listView.setAdapter(discoveredListViewItemAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        onClickDeviceDiscovered(view, position);
                    }
                });

                if(iotDeviceDiscoveredList.size()>0){
                    String message = iotDeviceDiscoveredList.size()+" "+getString(R.string.devices_found);
                    devicesViewModel.setActionsText(message);
                }
                else if(!devicesViewModel.isSearching() &&
                        !devicesViewModel.getActionsText().getValue().equals(
                               fragmentActivity.getString(R.string.search_for_network_devices))){
                    devicesViewModel.setActionsText(fragmentActivity.getString(R.string.no_device_found));
                }
                devicesViewModel.setScanNetworkEnable(!devicesViewModel.isSearching());
            }
        });
    }

    private void onClickDeviceDiscovered(View view, int position) {
        IotDevice iotDevice = iotDeviceDiscoveredList.get(position);
        if(!iotDevice.isAdded()){
            iotDevice.setAdded(true);
            TextView addText = (TextView) view.findViewById(R.id.discoveredIotAdd);;
            addText.setText(R.string.already_added);
            addText.setTextColor(ContextCompat.getColor(getContext(), R.color.zarus_text_muted));
            homeViewModel.addToIotDeviceList(iotDevice);
        }
    }

}
