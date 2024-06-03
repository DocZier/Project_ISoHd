package com.example.kr.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;

public class MainMenuFragment extends Fragment {


    public MainMenuFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_main_menu, container, false);

        Button accountButton = root.findViewById(R.id.account_button);
        Button databaseButton = root.findViewById(R.id.hdd_db_button);
        Button settingButton = root.findViewById(R.id.settings_button);
        Button infoButton = root.findViewById(R.id.info_button);
        Button mapButton = root.findViewById(R.id.map_button);

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).setCurrentPage(1);
            }
        });

        databaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).showFragment("database");
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).setCurrentPage(2);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).showFragment("info");
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).showFragment("map");
            }
        });

        return root;
    }
}