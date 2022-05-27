package com.example.zarusiot.ui.devices;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.zarusiot.R;
import com.example.zarusiot.component.HttpRequest;
import com.example.zarusiot.component.NetworkScan;
import com.example.zarusiot.data.model.IotDevice;
import com.example.zarusiot.databinding.FragmentDevicesBinding;
import com.example.zarusiot.ui.DiscoveredListViewItemAdapter;
import com.example.zarusiot.ui.home.HomeViewModel;
import com.stealthcopter.networktools.subnet.Device;

import java.util.ArrayList;
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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fragmentActivity = getActivity();

        networkScan = NetworkScan.getInstance();
        httpRequest = new HttpRequest(getContext());

        //Shared Data
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        devicesViewModel = new ViewModelProvider(requireActivity()).get(DevicesViewModel.class);
        iotDeviceDiscoveredList = devicesViewModel.getDiscoveredIotDeviceList().getValue();
        if(devicesViewModel.getActionsText().getValue().equals("")) devicesViewModel.setActionsText(getString(R.string.search_for_network_devices));
        setButtonState(!devicesViewModel.isSearching());

        devicesViewModel.getActionsText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String value) {
                final TextView textView = binding.textTest;
                textView.setText(value);
            }
        });

        final View button = root.findViewById(R.id.buttonScanNetwork);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devicesViewModel.setSearching(true);
                        setButtonState(false);
                        iotDeviceDiscoveredList.clear();
                        updateListView();
                        devicesViewModel.setDiscoveredIotDeviceList(iotDeviceDiscoveredList);
                        devicesViewModel.setActionsText(getString(R.string.scanning_all_devices));
                        networkScan.scanNetworkDevices((devicesFound) -> validatingZarusDevice(devicesFound));
                    }
                }
        );
        updateListView();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                ListView listView = DevicesFragment.binding.getRoot().findViewById(R.id.listViewDevices);

                DiscoveredListViewItemAdapter discoveredListViewItemAdapter = new DiscoveredListViewItemAdapter(fragmentActivity, iotDeviceDiscoveredList);
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
                if(iotDeviceDiscoveredList.size()>0)
                    devicesViewModel.setActionsText(iotDeviceDiscoveredList.size()+" Devices found.");
                else if(!devicesViewModel.isSearching() &&
                       !devicesViewModel.getActionsText().getValue().equals(
                               fragmentActivity.getString(R.string.search_for_network_devices))){
                    devicesViewModel.setActionsText(fragmentActivity.getString(R.string.no_device_found));
                }
                setButtonState(!devicesViewModel.isSearching());
            }
        });
    }

    private void setText(String text){
        if(DevicesFragment.fragmentActivity==null || DevicesFragment.binding==null) return;
        try{
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final TextView textView = binding.textTest;
                    textView.setText(text);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setButtonState(boolean state){
        if(DevicesFragment.fragmentActivity==null || DevicesFragment.binding==null) return;
        try{
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Button buttonScanNetwork = binding.buttonScanNetwork;
                    buttonScanNetwork.setEnabled(state);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
