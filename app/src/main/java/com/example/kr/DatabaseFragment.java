package com.example.kr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatabaseFragment extends Fragment {

    public DatabaseFragment() {
    }


    public static DatabaseFragment newInstance()
    {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_database, container, false);


        return root;
    }
}