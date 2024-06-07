package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.database.HistoryData;
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.dialog.FilterBottomSheetDialog;
import com.example.kr.dialog.SortBottomSheet;
import com.example.kr.dialog.SpecsBottomSheet;
import com.example.kr.model.AdapterCallback;
import com.example.kr.model.AdapterRecyclerView;
import com.example.kr.model.HDDViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatabaseFragment extends Fragment implements AdapterCallback {

    private String userId = null;
    private HDDViewModel hddViewModel;
    private AdapterRecyclerView adapterRecyclerView;
    private RecyclerView recyclerView;

    public DatabaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

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
        View root = inflater.inflate(R.layout.fragment_database, container, false);
        SearchView searchView = root.findViewById(R.id.search_bar);

        ImageButton syncButton = root.findViewById(R.id.act_sync);
        ImageButton filterButton = root.findViewById(R.id.act_filter);
        ImageButton hintButton = root.findViewById(R.id.act_hint);
        ImageButton sortButton = root.findViewById(R.id.act_sort);

        recyclerView = root.findViewById(R.id.hdd_recycler_view);
        recyclerView.addItemDecoration(new DecoratorRecyclerView(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecyclerView = new AdapterRecyclerView(this);
        recyclerView.setAdapter(adapterRecyclerView);

        hddViewModel = new ViewModelProvider(this).get(HDDViewModel.class);
        hddViewModel.getSortedDrivers().observe(getViewLifecycleOwner(), sortedDrivers -> {
            adapterRecyclerView.setHardDriveDataList(sortedDrivers);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hddViewModel.searchHardDrives(query).observe(getViewLifecycleOwner(), filteredList -> {
                    adapterRecyclerView.setHardDriveDataList(filteredList);
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hddViewModel.searchHardDrives(newText).observe(getViewLifecycleOwner(), filteredList -> {
                    adapterRecyclerView.setHardDriveDataList(filteredList);
                });
                return false;
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startParsing();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBottomSheetDialog dialog = new FilterBottomSheetDialog(requireContext(), hddViewModel, false);
                dialog.show();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortBottomSheet dialog = new SortBottomSheet(requireContext(), hddViewModel);
                dialog.show();
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showCustomDialog("a_hdds");
            }
        });

        start();

        return root;
    }

    @Override
    public void onShowBottomSheet(HardDriveData hardDriveData) {
        SpecsBottomSheet dialog = new SpecsBottomSheet(requireContext(), hardDriveData);
        dialog.show();
    }

    @Override
    public void saveHistoryData(HardDriveData hardDriveData, String currentDate) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("history_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        HistoryData historyData = getHistoryDataForDate(sharedPreferences, currentDate);

        if (historyData != null)
        {
            if (!historyData.getHardDriveDataList().equals(hardDriveData))
            {
                ArrayList<HardDriveData> combinedList = new ArrayList<>(historyData.getHardDriveDataList());
                combinedList.add(hardDriveData);

                historyData.setHardDriveDataList(combinedList);

                String jsonHistory = new Gson().toJson(historyData);
                editor.putString(currentDate, jsonHistory);
                editor.apply();
            }
        } else {
            historyData = new HistoryData(currentDate, new ArrayList<>());
            historyData.getHardDriveDataList().add(hardDriveData);

            String jsonHistory = new Gson().toJson(historyData);
            editor.putString(currentDate, jsonHistory);
            editor.apply();
        }

        addToHistory(historyData, currentDate);
    }

    private HistoryData getHistoryDataForDate(SharedPreferences sharedPreferences, String date) {
        String jsonHistory = sharedPreferences.getString(date, null);
        if (jsonHistory != null) {
            Type historyDataType = new TypeToken<HistoryData>() {}.getType();
            return new Gson().fromJson(jsonHistory, historyDataType);
        }
        return null;
    }

    public void addToHistory(HistoryData historyData, String currentDate)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference historyRef = db.collection("users").document(userId).collection("history");

            historyRef.document(currentDate).set(historyData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Test", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Test", "Error writing document", e);
                        }
                    });
        }
    }

    private void startParsing()
    {
        AppDatabase.loadHardDrivesFromXml(getContext());
    }
    private void start()
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("first_start", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean("isFirst", true)) {
            AppDatabase.start(requireContext());
            editor.putBoolean("isFirst", false);
            editor.apply();
        }
    }

}