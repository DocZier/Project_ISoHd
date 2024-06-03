package com.example.kr.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kr.R;
import com.example.kr.model.HDDViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterBottomSheetDialog extends BottomSheetDialog {

    HDDViewModel hddViewModel;
    private boolean isFavorite;
    private EditText etMinCapacity, etMaxCapacity, etMinRpm, etMaxRpm;
    private CheckBox cbSeagate, cbWD, cbSamsung, cbHitachi, cbToshiba;
    private CheckBox cbFF2_5, cbFF3_5;
    private ArrayList<String> selectedManufacturers = new ArrayList<>();
    private ArrayList<Double> selectedFormFactors = new ArrayList<>();

    private ArrayList<String> Manufacturers = new ArrayList<>();
    private ArrayList<Double> FormFactors = new ArrayList<>();

    private TextView tvClearManufacturer, tvClearCapacity, tvClearFormFactor, tvClearRotationSpeed;

    private SharedPreferences sharedPrefs;
    private static final String PREFS_NAME = "filter_prefs";
    public FilterBottomSheetDialog(Context context, HDDViewModel hddViewModel, boolean isFavorite)
    {
        super(context);
        setContentView(R.layout.bottomsheet_filter);
        this.hddViewModel = hddViewModel;
        this.isFavorite = isFavorite;

        etMinCapacity = findViewById(R.id.et_min_capacity);
        etMaxCapacity = findViewById(R.id.et_max_capacity);
        etMinRpm = findViewById(R.id.et_min_rpm);
        etMaxRpm = findViewById(R.id.et_max_rpm);

        Manufacturers.addAll(Arrays.asList("Seagate", "Western digital", "Samsung", "Hitachi", "Toshiba"));
        FormFactors.addAll(Arrays.asList(3.5, 2.5));

        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        tvClearManufacturer = findViewById(R.id.textview_clear_manufactor);
        tvClearCapacity = findViewById(R.id.textview_clear_capacity);
        tvClearFormFactor = findViewById(R.id.textview_clear_form_factor);
        tvClearRotationSpeed = findViewById(R.id.textview_clear_rotation_speed);

        tvClearManufacturer.setOnClickListener(v -> clearFilters("manufacturers"));
        tvClearCapacity.setOnClickListener(v -> clearFilters("capacity"));
        tvClearFormFactor.setOnClickListener(v -> clearFilters("formfactors"));
        tvClearRotationSpeed.setOnClickListener(v -> clearFilters("rotationspeed"));

         cbSeagate = findViewById(R.id.checkbox_seagate);
         cbWD = findViewById(R.id.checkbox_wdigital);
         cbSamsung = findViewById(R.id.checkbox_samsung);
         cbHitachi = findViewById(R.id.checkbox_hitachi);
         cbToshiba = findViewById(R.id.checkbox_toshiba);

        cbSeagate.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Seagate", isChecked));
        cbWD.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Western digital", isChecked));
        cbSamsung.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Samsung", isChecked));
        cbHitachi.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Hitachi", isChecked));
        cbToshiba.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Toshiba", isChecked));

        cbFF2_5 = findViewById(R.id.checkbox_ff_2_5);
        cbFF3_5 = findViewById(R.id.checkbox_ff_3_5);

        cbFF2_5.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedFormFactors, "3.5", isChecked, 0));
        cbFF3_5.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedFormFactors, "2.5", isChecked, 0));

        restoreFilterState();

        Button applyButton = findViewById(R.id.button_apply_filers);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                double minCapacity = getValidValue(etMinCapacity, 0);
                double maxCapacity = getValidValue(etMaxCapacity, Integer.MAX_VALUE);

                int minRpm = getValidValue(etMinRpm, 0);
                int maxRpm = getValidValue(etMaxRpm, Integer.MAX_VALUE);

                if(selectedManufacturers.isEmpty())
                    selectedManufacturers = Manufacturers;

                if(selectedFormFactors.isEmpty())
                    selectedFormFactors = FormFactors;

                saveFilterState();

                hddViewModel.filterDrivers(selectedManufacturers, minCapacity, maxCapacity,
                        selectedFormFactors, minRpm, maxRpm, isFavorite);

                dismiss();
            }
        });

        Button clearButton = findViewById(R.id.button_clear_filers);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ArrayList<String> titles = new ArrayList<>();
                titles.addAll(Arrays.asList("manufacturers", "capacity", "formfactors", "rotationspeed"));

                for(String title: titles)
                    clearFilters(title);

                hddViewModel.clearFilteredDrivers();

                clearFilterState();

                dismiss();
            }
        });


        etMinCapacity.addTextChangedListener(new MinValueValidator(etMinCapacity, 1));
        etMaxCapacity.addTextChangedListener(new MaxValueValidator(etMaxCapacity, etMinCapacity, 0));
        etMinRpm.addTextChangedListener(new MinValueValidator(etMinRpm, 0));
        etMaxRpm.addTextChangedListener(new MaxValueValidator(etMaxRpm, etMinRpm, 0));
    }

    private void saveFilterState() {
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putBoolean("checkbox_seagate", cbSeagate.isChecked());
        editor.putBoolean("checkbox_wd", cbWD.isChecked());
        editor.putBoolean("checkbox_samsung", cbSamsung.isChecked());
        editor.putBoolean("checkbox_hitachi", cbHitachi.isChecked());
        editor.putBoolean("checkbox_toshiba", cbToshiba.isChecked());

        editor.putBoolean("checkbox_ff25", cbFF2_5.isChecked());
        editor.putBoolean("checkbox_ff35", cbFF3_5.isChecked());

        editor.putString("et_min_capacity", etMinCapacity.getText().toString());
        editor.putString("et_max_capacity", etMaxCapacity.getText().toString());
        editor.putString("et_min_rpm", etMinRpm.getText().toString());
        editor.putString("et_max_rpm", etMaxRpm.getText().toString());

        editor.apply();
    }

    private void clearFilterState() {
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.clear();

        editor.apply();
    }

    private void restoreFilterState() {
        cbSeagate.setChecked(sharedPrefs.getBoolean("checkbox_seagate", false));
        cbWD.setChecked(sharedPrefs.getBoolean("checkbox_wd", false));
        cbSamsung.setChecked(sharedPrefs.getBoolean("checkbox_samsung", false));
        cbHitachi.setChecked(sharedPrefs.getBoolean("checkbox_hitachi", false));
        cbToshiba.setChecked(sharedPrefs.getBoolean("checkbox_toshiba", false));

        cbFF2_5.setChecked(sharedPrefs.getBoolean("checkbox_ff25", false));
        cbFF3_5.setChecked(sharedPrefs.getBoolean("checkbox_ff35", false));

        etMinCapacity.setText(sharedPrefs.getString("et_min_capacity", ""));
        etMaxCapacity.setText(sharedPrefs.getString("et_max_capacity", ""));
        etMinRpm.setText(sharedPrefs.getString("et_min_rpm", ""));
        etMaxRpm.setText(sharedPrefs.getString("et_max_rpm", ""));
    }


    private int getValidValue(EditText editText, int defaultValue)
    {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                editText.setError("Некорректный ввод");
                return defaultValue;
            }
        }
    }

    private class MinValueValidator implements TextWatcher
    {
        private EditText editText;
        private int minValue;

        public MinValueValidator(EditText editText, int minValue) {
            this.editText = editText;
            this.minValue = minValue;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            try {
                int value = Integer.parseInt(s.toString());
                if (value < minValue) {
                    editText.setError("Значение должно быть хотя бы равным " + minValue);
                } else {
                    editText.setError(null);
                }
            } catch (NumberFormatException e) {
                editText.setError(null);
            }
        }
    }


    private class MaxValueValidator implements TextWatcher
    {
        private EditText editText;
        private EditText minValueEditText;
        private int minDifference;

        public MaxValueValidator(EditText editText, EditText minValueEditText, int minDifference) {
            this.editText = editText;
            this.minValueEditText = minValueEditText;
            this.minDifference = minDifference;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            try {
                int value = Integer.parseInt(s.toString());
                int minValue = Integer.parseInt(minValueEditText.getText().toString());
                if (value < minValue + minDifference) {
                    editText.setError("Значение должно превышать или быть равным " + (minValue + minDifference));
                } else {
                    editText.setError(null);
                }
            } catch (NumberFormatException e) {
                editText.setError(null);
            }
        }
    }

    private void updateFilterList(ArrayList<String> list, String value, boolean isChecked)
    {
        if (isChecked)
        {
            list.add(value);
        } else
        {
            list.remove(value);
        }
        updateClearButtonVisibility();
    }

    private void updateFilterList(ArrayList<Double> list, String value, boolean isChecked, int i)
    {
        if (isChecked)
        {
            list.add(Double.parseDouble(value));
        } else
        {
            list.remove(Double.parseDouble(value));
        }
        updateClearButtonVisibility();
    }

    private void clearFilters(String filterType)
    {
        switch (filterType)
        {
            case "manufacturers":
                selectedManufacturers.clear();
                ConstraintLayout manufacturersLayout = findViewById(R.id.manufactor_layout);
                for (int i = 0; i < manufacturersLayout.getChildCount(); i++)
                {
                    View child = manufacturersLayout.getChildAt(i);
                    if (child instanceof CheckBox)
                    {
                        ((CheckBox) child).setChecked(false);
                    }
                }
                break;
            case "capacity":
                etMinCapacity.setText("");
                etMaxCapacity.setText("");
                break;
            case "formfactors":
                selectedFormFactors.clear();
                LinearLayout formFactorsLinearLayout = findViewById(R.id.form_factor_linear_layout);
                for (int i = 0; i < formFactorsLinearLayout.getChildCount(); i++) {
                    View child = formFactorsLinearLayout.getChildAt(i);
                    if (child instanceof CheckBox) {
                        ((CheckBox) child).setChecked(false);
                    }
                }
                break;
            case "rotationspeed":
                etMinRpm.setText("");
                etMaxRpm.setText("");
                break;
            default:
                break;
        }
        updateClearButtonVisibility();

    }

    private void updateClearButtonVisibility() {

        findViewById(R.id.textview_clear_manufactor).setVisibility(
                !selectedManufacturers.isEmpty() ?
                View.VISIBLE : View.GONE);
        findViewById(R.id.textview_clear_capacity).setVisibility(
                !etMinCapacity.getText().toString().isEmpty() || !etMaxCapacity.getText().toString().isEmpty() ?
                        View.VISIBLE : View.GONE);
        findViewById(R.id.textview_clear_form_factor).setVisibility(
                !selectedFormFactors.isEmpty() ?
                        View.VISIBLE : View.GONE);
        findViewById(R.id.textview_clear_rotation_speed).setVisibility(
                !etMinRpm.getText().toString().isEmpty() || !etMaxRpm.getText().toString().isEmpty() ?
                        View.VISIBLE : View.GONE);
    }
}
