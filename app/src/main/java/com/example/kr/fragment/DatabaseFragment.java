package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.kr.R;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.dialog.FilterBottomSheetDialog;
import com.example.kr.model.AdapterCallback;
import com.example.kr.model.AdapterRecyclerView;
import com.example.kr.model.HDDViewModel;
import com.example.kr.web.WebPageParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseFragment extends Fragment implements AdapterCallback {

    private String userId;
    private HDDViewModel hddViewModel;
    private AdapterRecyclerView adapterRecyclerView;
    private RecyclerView recyclerView;
    private ArrayList<String> selectedManufacturers = new ArrayList<>();
    private double minCapacity = 0;
    private double maxCapacity = Double.MAX_VALUE;
    private boolean isCapacityInTb = false;
    private ArrayList<String> selectedInterfaces = new ArrayList<>();
    private ArrayList<String> selectedFormFactors = new ArrayList<>();
    private int selectedSpeed = 0;
    public DatabaseFragment() {
    }


    public static DatabaseFragment newInstance()
    {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_database, container, false);

        ImageButton syncButton = root.findViewById(R.id.act_sync);
        ImageButton filterButton = root.findViewById(R.id.act_filter);

        recyclerView = root.findViewById(R.id.hdd_recycler_view);
        recyclerView.addItemDecoration(new DecoratorRecyclerView(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecyclerView = new AdapterRecyclerView(this);
        recyclerView.setAdapter(adapterRecyclerView);

        hddViewModel = new ViewModelProvider(this).get(HDDViewModel.class);
        hddViewModel.getSortedDrivers().observe(getViewLifecycleOwner(), hardDriveDataList -> {
            adapterRecyclerView.setHardDriveDataList(hardDriveDataList);
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
                FilterBottomSheetDialog dialog = new FilterBottomSheetDialog(requireContext(), hddViewModel);
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onShowBottomSheet(HardDriveData hardDriveData) {
        showBottomSheet(hardDriveData);
    }

    @Override
    public void onAddToFavorites(HardDriveData hardDriveData) {
        addToFavorites(userId, hardDriveData);
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

    private void showBottomSheet(HardDriveData hardDriveData) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.bottomsheet_specs);

        EditText modelEditText = bottomSheetDialog.findViewById(R.id.edit_modelname);
        EditText capacityEditText = bottomSheetDialog.findViewById(R.id.edit_capacity);
        EditText manufactorEditText = bottomSheetDialog.findViewById(R.id.edit_manufactor);
        EditText interfaceEditText = bottomSheetDialog.findViewById(R.id.edit_interface);
        EditText formfactorEditText = bottomSheetDialog.findViewById(R.id.edit_formfactor);
        EditText speedEditText = bottomSheetDialog.findViewById(R.id.edit_speed);

        modelEditText.setText(hardDriveData.getModel());
        capacityEditText.setText(String.valueOf(hardDriveData.getCapacity()));
        manufactorEditText.setText(hardDriveData.getManufactor());
        interfaceEditText.setText(hardDriveData.getInterfc());
        formfactorEditText.setText(hardDriveData.getFormFactor()+"");
        speedEditText.setText(String.valueOf(hardDriveData.getSpeed()));

        ImageButton buttonFavorite = bottomSheetDialog.findViewById(R.id.act_add_favorite);
        ImageButton buttonEdit = bottomSheetDialog.findViewById(R.id.act_edit);
        Button buttonSave = bottomSheetDialog.findViewById(R.id.act_save);
        Button buttonClear = bottomSheetDialog.findViewById(R.id.act_clear);

        buttonFavorite.setOnClickListener(v -> addToFavorites(userId, hardDriveData));

        buttonEdit.setOnClickListener(v -> {
            modelEditText.setEnabled(true);
            capacityEditText.setEnabled(true);
            manufactorEditText.setEnabled(true);
            interfaceEditText.setEnabled(true);
            formfactorEditText.setEnabled(true);
            speedEditText.setEnabled(true);

            buttonSave.setVisibility(View.VISIBLE);
            buttonClear.setVisibility(View.VISIBLE);
        });

        buttonSave.setOnClickListener(v -> {
            hardDriveData.setModel(modelEditText.getText().toString());
            hardDriveData.setCapacity(Double.parseDouble(capacityEditText.getText().toString()));
            hardDriveData.setManufactor(manufactorEditText.getText().toString());
            hardDriveData.setInterfc(interfaceEditText.getText().toString());
            hardDriveData.setFormFactor(Double.parseDouble(formfactorEditText.getText().toString()));
            hardDriveData.setSpeed(Integer.parseInt(speedEditText.getText().toString()));

            updateHardDriveData(hardDriveData);

            bottomSheetDialog.dismiss();
        });

        buttonClear.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    public void addToFavorites(String userId, HardDriveData hardDriveData) {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance("https://kr-project-69fc5-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(userId).child("favorites");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    ArrayList<HardDriveData> favorites = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        HardDriveData data = itemSnapshot.getValue(HardDriveData.class);
                        favorites.add(data);
                    }
                    favorites.add(hardDriveData);

                    userRef.setValue(favorites).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Item added to favorites.");
                            } else {
                                Log.w(TAG, "Failed to add item to favorites.", task.getException());
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "Failed to get favorites.", task.getException());
                }
            }
        });
    }

    private void updateHardDriveData(HardDriveData hardDriveData) {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "HDDDataBase.db").build();
            db.hardDriveDao().update(hardDriveData);
        }).start();
    }
}