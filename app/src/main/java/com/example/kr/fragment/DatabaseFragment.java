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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.dialog.FilterBottomSheetDialog;
import com.example.kr.model.AdapterCallback;
import com.example.kr.model.AdapterRecyclerView;
import com.example.kr.model.HDDViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseFragment extends Fragment implements AdapterCallback {

    boolean isEdit = false;
    private String userId = null;
    private HDDViewModel hddViewModel;
    private AdapterRecyclerView adapterRecyclerView;
    private RecyclerView recyclerView;
    private ImageButton accountButton;

    public DatabaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public void updateFragment()
    {
        userId = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_database, container, false);
        SearchView searchView = root.findViewById(R.id.search_bar);

        ImageButton syncButton = root.findViewById(R.id.act_sync);
        ImageButton filterButton = root.findViewById(R.id.act_filter);
        accountButton = root.findViewById(R.id.act_account);

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

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(userId != null)
                    ((MainActivity) getActivity()).showAccountFragment();
                else
                    ((MainActivity) getActivity()).showLoginFragment();
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
        if (userId == null) {
            Toast.makeText(getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hardDriveData.isFavorite()) {
            removeFromFavorites(hardDriveData);
        } else {
            addToFavorites(hardDriveData);
        }
    }

    private void startParsing()
    {
        AppDatabase.loadHardDrivesFromXml(getContext());
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

        if (userId == null) {
            buttonFavorite.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.GONE);
        } else {
            buttonFavorite.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.VISIBLE);
        }

        buttonFavorite.setOnClickListener(v -> {
            buttonFavorite.setImageResource(!hardDriveData.isFavorite() ? R.drawable.ic_bookmark_add : R.drawable.ic_bookmark_remove);

            onAddToFavorites(hardDriveData);
        });

        buttonEdit.setOnClickListener(v -> {
            isEdit = !isEdit;

            buttonEdit.setImageResource(!isEdit ? R.drawable.ic_edit_on : R.drawable.ic_edit_off);

            modelEditText.setEnabled(isEdit);
            capacityEditText.setEnabled(isEdit);
            manufactorEditText.setEnabled(isEdit);
            interfaceEditText.setEnabled(isEdit);
            formfactorEditText.setEnabled(isEdit);
            speedEditText.setEnabled(isEdit);

            buttonSave.setVisibility(isEdit ? View.VISIBLE : View.GONE);
            buttonClear.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        });

        buttonSave.setOnClickListener(v -> {
            hardDriveData.setModel(modelEditText.getText().toString());
            hardDriveData.setCapacity(Double.parseDouble(capacityEditText.getText().toString()));
            hardDriveData.setManufactor(manufactorEditText.getText().toString());
            hardDriveData.setInterfc(interfaceEditText.getText().toString());
            hardDriveData.setFormFactor(formfactorEditText.getText().toString());
            hardDriveData.setSpeed(Integer.parseInt(speedEditText.getText().toString()));

            updateHardDriveData(hardDriveData);

            bottomSheetDialog.dismiss();
        });

        buttonClear.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    public void addToFavorites(HardDriveData hardDriveData)
    {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance("https://kr-project-69fc5-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(userId).child("favorites");

        hardDriveData.setFavorite(true);
        updateHardDriveData(hardDriveData);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isDuplicate = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    HardDriveData existingHardDriveData = childSnapshot.getValue(HardDriveData.class);
                    if (existingHardDriveData.uid == hardDriveData.uid) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {

                    String key = userRef.push().getKey();
                    userRef.child(key).setValue(hardDriveData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "failed to add");
            }
        });
    }

    public void removeFromFavorites(HardDriveData hardDriveData)
    {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance("https://kr-project-69fc5-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(userId).child("favorites");

        hardDriveData.setFavorite(false);
        updateHardDriveData(hardDriveData);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    HardDriveData existingHardDriveData = childSnapshot.getValue(HardDriveData.class);
                    if (existingHardDriveData.uid == hardDriveData.uid) {
                        childSnapshot.getRef().removeValue();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "failed to delete");
            }
        });
    }


    private void updateHardDriveData(HardDriveData hardDriveData) {
        AppDatabase.updateItem(getContext(), hardDriveData);
    }
}