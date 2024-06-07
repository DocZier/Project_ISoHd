package com.example.kr.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.kr.R;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.model.HDDViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SpecsBottomSheet extends BottomSheetDialog 
{

    boolean isEdit = false;

    public SpecsBottomSheet(@NonNull Context context,  HardDriveData hardDriveData)
    {
        super(context);
        setContentView(R.layout.bottomsheet_specs);

        EditText modelEditText = findViewById(R.id.edit_modelname);
        EditText capacityEditText = findViewById(R.id.edit_capacity);
        EditText manufactorEditText = findViewById(R.id.edit_manufactor);
        EditText interfaceEditText = findViewById(R.id.edit_interface);
        EditText formfactorEditText = findViewById(R.id.edit_formfactor);
        EditText speedEditText = findViewById(R.id.edit_speed);

        modelEditText.setText(hardDriveData.getModel());
        capacityEditText.setText(String.valueOf(hardDriveData.getCapacity()));
        manufactorEditText.setText(hardDriveData.getManufactor());
        interfaceEditText.setText(hardDriveData.getInterfc());
        formfactorEditText.setText(hardDriveData.getFormFactor()+"");
        speedEditText.setText(String.valueOf(hardDriveData.getSpeed()));

        ImageButton buttonFavorite = findViewById(R.id.act_add_favorite);
        ImageButton buttonEdit = findViewById(R.id.act_edit);
        Button buttonSave = findViewById(R.id.act_save);
        Button buttonClear = findViewById(R.id.act_clear);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            buttonFavorite.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.GONE);
        } else {
            buttonFavorite.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.VISIBLE);
        }

        buttonFavorite.setImageResource(!hardDriveData.isFavorite() ? R.drawable.ic_bookmark_add : R.drawable.ic_bookmark_remove);

        buttonFavorite.setOnClickListener(v -> {
            buttonFavorite.setImageResource(hardDriveData.isFavorite() ? R.drawable.ic_bookmark_add : R.drawable.ic_bookmark_remove);

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

        });

        buttonClear.setOnClickListener(v -> {
            modelEditText.setText(hardDriveData.getModel());
            capacityEditText.setText(String.valueOf(hardDriveData.getCapacity()));
            manufactorEditText.setText(hardDriveData.getManufactor());
            interfaceEditText.setText(hardDriveData.getInterfc());
            formfactorEditText.setText(hardDriveData.getFormFactor()+"");
            speedEditText.setText(String.valueOf(hardDriveData.getSpeed()));
        });
    }

    public void onAddToFavorites(HardDriveData hardDriveData) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hardDriveData.isFavorite())
        {
            removeFromFavorites(hardDriveData);
        } else
        {
            addToFavorites(hardDriveData);
        }
    }

    public void addToFavorites(HardDriveData hardDriveData)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference favoritesRef = db.collection("users").document(userId).collection("favorites");

            hardDriveData.setFavorite(true);
            updateHardDriveData(hardDriveData);

            favoritesRef.document(hardDriveData.uid + "").set(hardDriveData)
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

    public void removeFromFavorites(HardDriveData hardDriveData)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference favoritesRef = db.collection("users").document(userId).collection("favorites");

            hardDriveData.setFavorite(false);
            updateHardDriveData(hardDriveData);

            favoritesRef.document(hardDriveData.uid + "").delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Test", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Test", "Error deleting document", e);
                        }
                    });
        }
    }

    private void updateHardDriveData(HardDriveData hardDriveData) {
        AppDatabase.updateItem(getContext(), hardDriveData);
    }
}
