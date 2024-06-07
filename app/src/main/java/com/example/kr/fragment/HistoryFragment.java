package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.database.HardDriveData;
import com.example.kr.database.HistoryData;
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.dialog.SpecsBottomSheet;
import com.example.kr.model.AdapterCallback;
import com.example.kr.model.AdapterRecyclerView;
import com.example.kr.model.HistoryRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class HistoryFragment extends Fragment implements AdapterCallback {

    private HistoryRecyclerViewAdapter adapterRecyclerView;
    private RecyclerView recyclerView;

    public HistoryFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).hideFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = root.findViewById(R.id.history_recycler_view);
        recyclerView.addItemDecoration(new DecoratorRecyclerView(8));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterRecyclerView = new HistoryRecyclerViewAdapter(recyclerView, this);
        recyclerView.setAdapter(adapterRecyclerView);

        loadHardDriveData();

        return root;
    }


    public void loadHardDriveData()
    {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("history_data", Context.MODE_PRIVATE);

        Map<String, ?> fullHistory = sharedPreferences.getAll();

        for (Map.Entry<String, ?> dayHistory : fullHistory.entrySet())
        {
            String jsonHistory = (String) dayHistory.getValue();
            Type historyDataType = new TypeToken<HistoryData>() {}.getType();

            adapterRecyclerView.addHistoryDataList(new Gson().fromJson(jsonHistory, historyDataType));
        }

    }


    @Override
    public void onShowBottomSheet(HardDriveData hardDriveData) {
        SpecsBottomSheet dialog = new SpecsBottomSheet(requireContext(), hardDriveData);
        dialog.show();
    }

    @Override
    public void saveHistoryData(HardDriveData hardDriveData, String currentDate)
    {

    }
}