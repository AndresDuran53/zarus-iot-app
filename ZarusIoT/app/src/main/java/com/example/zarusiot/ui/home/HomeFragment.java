package com.example.zarusiot.ui.home;

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

import com.example.zarusiot.ui.ListViewItemAdapter;
import com.example.zarusiot.R;
import com.example.zarusiot.databinding.FragmentHomeBinding;
import com.example.zarusiot.data.model.IotDevice;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getStoredIotDeviceList().observe(getViewLifecycleOwner(), new Observer<List<IotDevice>>() {
            @Override
            public void onChanged(List<IotDevice> iotDevices) {
                updateListView(iotDevices);
            }
        });

        updateListView(new ArrayList<>());

        return root;
    }

    private void updateListView(List<IotDevice> iotDevices){

        ListView listView = binding.getRoot().findViewById(R.id.listView);

        TextView noDevicesText = binding.getRoot().findViewById(R.id.noDevicesTextView);
        if(iotDevices.size()==0){
            //iotDevices.add(new IotDevice("Test Device","TypeTest","0.0.0.0"));
            noDevicesText.setVisibility(View.VISIBLE);
        } else noDevicesText.setVisibility(View.INVISIBLE);

        ListViewItemAdapter listViewItemAdapter = new ListViewItemAdapter(requireActivity(),iotDevices);
        listView.setAdapter(listViewItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String deviceIp = iotDevices.get(position).getIp();
                Uri uri = Uri.parse("http://"+deviceIp); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}