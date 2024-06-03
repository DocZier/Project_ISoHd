package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.decorator.DecoratorRecyclerView;
import com.example.kr.dialog.FilterBottomSheetDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment implements AdapterCallback {


    boolean isEdit = false;
    private String userId = null;
    private AdapterRecyclerView adapterRecyclerView;
    private RecyclerView recyclerView;
    private FirebaseDatabase database = null;
    private DatabaseReference userFavoritesRef = null;
    private HDDViewModel hddViewModel;

    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadHardDriveData();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
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

        hddViewModel.favoriteMode();
        hddViewModel.getSortedDrivers().observe(getViewLifecycleOwner(), sortedDrivers -> {
            adapterRecyclerView.setHardDriveDataList(sortedDrivers);
            Log.i("Adapter", sortedDrivers.toString());
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
                loadHardDriveData();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBottomSheetDialog dialog = new FilterBottomSheetDialog(requireContext(), hddViewModel, true);
                dialog.show();
            }
        });
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Сортировка", Toast.LENGTH_SHORT).show();
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Подсказка", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }



    public void loadHardDriveData()
    {
        ArrayList<HardDriveData> hardDriveDataList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference favoritesRef = db.collection("users").document(userId).collection("favorites");

        favoritesRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HardDriveData hardDriveData = document.toObject(HardDriveData.class);

                            updateHardDriveData(hardDriveData);
                        }
                    } else {
                        Log.d(TAG, "Error getting hdds: ", task.getException());
                    }
                }
            });
    }

    @Override
    public void onShowBottomSheet(HardDriveData hardDriveData)
    {
        SpecsBottomSheet dialog = new SpecsBottomSheet(requireContext(), hardDriveData);
        dialog.show();
    }

    private void updateHardDriveData(HardDriveData hardDriveData) {

        Log.i("Firebase", hardDriveData.toString());
        AppDatabase.updateItem(getContext(), hardDriveData);
    }
}