package com.example.zarusiot.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.IotDevice;
import com.example.zarusiot.databinding.FragmentHomeBinding;
import com.example.zarusiot.ui.DeviceInformation;
import com.example.zarusiot.ui.ListViewItemAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final String DEVICES_STORED_FILENAME = "devices_stored.data";
    private final String IOT_DEVICE_INFORMATION = "IOT_DEVICE_INFORMATION";
    private final String ACTION_NAME = "ACTION_NAME";
    private final String EDIT_ACTION_NAME = "EDIT_ACTION_NAME";
    private final String DELETE_ACTION_NAME = "DELETE_ACTION_NAME";
    private final String DEVICE_ACTION_NAME = "DEVICE_ACTION_NAME";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ActivityResultLauncher<Intent> deviceInformationActivityResultLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getStoredIotDeviceList().observe(getViewLifecycleOwner(), new Observer<List<IotDevice>>() {
            @Override
            public void onChanged(List<IotDevice> iotDevices) {
                updateListView(iotDevices);
            }
        });

        if (homeViewModel.getStoredIotDeviceList().getValue().size() == 0) {
            List<IotDevice> iotDevicesStoredInternally = readData();
            if (iotDevicesStoredInternally != null)
                homeViewModel.setStoredIotDeviceList(iotDevicesStoredInternally);
        }

        deviceInformationActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            applyChangesOnActivityResult(result);
                        }
                        updateListView(homeViewModel.getStoredIotDeviceList().getValue());
                    }
                });

        updateListView(homeViewModel.getStoredIotDeviceList().getValue());
        return root;
    }

    private void applyChangesOnActivityResult(ActivityResult result) {
        Intent intent = result.getData();
        IotDevice iotDevice = (IotDevice) intent.getSerializableExtra(IOT_DEVICE_INFORMATION);
        String actionName = intent.getStringExtra(ACTION_NAME);
        if(actionName.equals(EDIT_ACTION_NAME)){
            String deviceActionName = intent.getStringExtra(DEVICE_ACTION_NAME);
            int indexDevice = IotDevice.searchIndexByIotDevice(
                    homeViewModel.getStoredIotDeviceList().getValue(), iotDevice);
            homeViewModel.getStoredIotDeviceList().getValue().get(indexDevice)
                    .setName(deviceActionName);
        } else if(actionName.equals(DELETE_ACTION_NAME)){
            removeDeviceFromStorage(iotDevice);
        }
    }

    private void removeDeviceFromStorage(IotDevice iotDevice) {
        List<IotDevice> homeListStoredAux = homeViewModel.getStoredIotDeviceList().getValue();
        int index = IotDevice.searchIndexByIdAndIp(
                homeListStoredAux,
                iotDevice.getId(),
                iotDevice.getIp());
        homeListStoredAux.remove(index);
        homeViewModel.setStoredIotDeviceList(homeListStoredAux);
    }

    private void updateListView(List<IotDevice> iotDevices) {
        ListView listView = binding.getRoot().findViewById(R.id.listView);

        TextView noDevicesText = binding.getRoot().findViewById(R.id.noDevicesTextView);
        if (iotDevices.size() == 0) {
            noDevicesText.setVisibility(View.VISIBLE);
        } else noDevicesText.setVisibility(View.INVISIBLE);

        writeData(iotDevices);
        ListViewItemAdapter listViewItemAdapter = new ListViewItemAdapter(requireActivity(), iotDevices);
        listView.setAdapter(listViewItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DeviceInformation.class);
                intent.putExtra(IOT_DEVICE_INFORMATION,iotDevices.get(position));
                deviceInformationActivityResultLauncher.launch(intent);
                //startActivity(intent);
            }
        });
    }

    private List<IotDevice> readData() {
        List<IotDevice> valueStored = new ArrayList<>();
        try {
            FileInputStream fis = getActivity().openFileInput(DEVICES_STORED_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            valueStored = (List<IotDevice>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return valueStored;
    }

    private void writeData(List<IotDevice> deviceToStore) {
        try {
            ObjectOutputStream oos;
            FileOutputStream fos = getActivity().openFileOutput(DEVICES_STORED_FILENAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(deviceToStore);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}