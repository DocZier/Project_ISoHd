package com.example.kr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.kr.R;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.model.AdapterRecyclerView;
import com.example.kr.model.HDDViewModel;
import com.example.kr.web.WebPageParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseFragment extends Fragment {

    private HDDViewModel hddViewModel;
    private AdapterRecyclerView adapterRecyclerView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_database, container, false);

        ImageButton sync = root.findViewById(R.id.act_sync);

        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startParsing();
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.hdd_recycler_view);
        recyclerView.addItemDecoration(new DecoratorRecyclerView(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecyclerView = new AdapterRecyclerView();
        recyclerView.setAdapter(adapterRecyclerView);

        hddViewModel = new ViewModelProvider(this).get(HDDViewModel.class);
        hddViewModel.getDrivers().observe(getViewLifecycleOwner(), hardDriveDataList -> {
            adapterRecyclerView.setHardDriveDataList(hardDriveDataList);
        });

        return root;
    }

    private void startParsing()
    {
        ProgressBar progressBar = getActivity().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(5);

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "HDDDataBase.db").build();
                WebPageParser WebPageParser = new WebPageParser();
                try
                {
                    WebPageParser.getLinks();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                for (int i = 0; i < 5; i++)
                {
                    final int progress = i + 1;
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });

                    ArrayList<HardDriveData> driveData;
                    try {
                        driveData = WebPageParser.getData(i);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    try {
                        Thread.sleep(new Random().nextInt(12500) + 2500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    for (HardDriveData j : driveData)
                    {
                        db.hardDriveDao().insertAll(j);
                    }
                }
                progressBar.post(new Runnable()
                {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.setProgress(0);
                    }
                });
            }
        }).start();
    }
}