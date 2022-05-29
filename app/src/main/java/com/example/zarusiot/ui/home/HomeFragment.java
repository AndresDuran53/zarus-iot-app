package com.example.zarusiot.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.IotDevice;
import com.example.zarusiot.databinding.FragmentHomeBinding;
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
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

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

        updateListView(homeViewModel.getStoredIotDeviceList().getValue());
        return root;
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
                String deviceIp = iotDevices.get(position).getIp();
                Uri uri = Uri.parse("http://" + deviceIp);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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