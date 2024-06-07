package com.example.kr.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kr.R;
import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveData;
import com.example.kr.model.HDDViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class SortBottomSheet extends BottomSheetDialog
{
    HDDViewModel hddViewModel;
    private CheckBox typeA, typeB;
    private CheckBox high, low;
    private SharedPreferences sharedPrefs;
    private boolean isTypeA;
    private boolean isLow;
    private static final String PREFS_NAME = "sort_prefs";
    public SortBottomSheet(Context context, HDDViewModel hddViewModel)
    {
        super(context);
        setContentView(R.layout.bottomsheet_sort);
        this.hddViewModel = hddViewModel;

        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        typeA = findViewById(R.id.checkbox_a_type);
        typeB = findViewById(R.id.checkbox_b_type);

        high = findViewById(R.id.checkbox_higher);
        low = findViewById(R.id.checkbox_lower);

        typeA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            typeB.setChecked(!isChecked);
            typeA.setChecked(isChecked);
            isTypeA=isChecked;
        });
        typeB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            typeA.setChecked(!isChecked);
            typeB.setChecked(isChecked);
            isTypeA=!isChecked;
        });

        high.setOnCheckedChangeListener((buttonView, isChecked) -> {
            low.setChecked(!isChecked);
            high.setChecked(isChecked);
            isLow=!isChecked;
        });
        low.setOnCheckedChangeListener((buttonView, isChecked) -> {
            high.setChecked(!isChecked);
            low.setChecked(isChecked);
            isLow=isChecked;
        });

        restoreFilterState();

        Button applyButton = findViewById(R.id.button_apply_filers);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                saveFilterState();

                hddViewModel.sort(isTypeA, isLow);

                dismiss();
            }
        });
    }

    private void saveFilterState() {
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putBoolean("isTypeA",isTypeA);
        editor.putBoolean("isLow", isLow);

        editor.apply();
    }

    private void restoreFilterState() {
        if(sharedPrefs.getBoolean("isTypeA", false)) {
            typeA.setChecked(true);
            typeB.setChecked(false);
        }
        else {
            typeB.setChecked(true);
            typeA.setChecked(false);
        }

        if(sharedPrefs.getBoolean("isLow", true)) {
            low.setChecked(true);
            high.setChecked(false);
        }
        else {
            high.setChecked(true);
            low.setChecked(false);
        }

        hddViewModel.sort(isTypeA, isLow);
    }

}
