package com.example.kr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingsFragment extends Fragment  implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> items;
    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).setCurrentPage(0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        items = new ArrayList<>(Arrays.asList("Как в системе", "Светлая", "Тёмная"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner spinner = root.findViewById(R.id.theme_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item , items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        Button authorButton = root.findViewById(R.id.author_button);
        Button resourceButton = root.findViewById(R.id.resources_button);
        Button instructionButton = root.findViewById(R.id.instruction_button);

        authorButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("author");
        });

        resourceButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_resources");
        });

        instructionButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("instructions");
        });

        return root;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}