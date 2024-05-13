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

import com.example.kr.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterBottomSheetDialog extends BottomSheetDialog {

    private Button applyButton;
    private EditText etMinCapacity, etMaxCapacity, etMinRpm, etMaxRpm;
    private CheckBox cbSeagate, cbWD, cbSamsung, cbHitachi, cbToshiba;
    private CheckBox cbSata1, cbSata2, cbSata3;
    private CheckBox cbFF2_5, cbFF3_5;
    private ArrayList<String> selectedManufacturers = new ArrayList<>();
    private ArrayList<String> selectedInterfaces = new ArrayList<>();
    private ArrayList<String> selectedFormFactors = new ArrayList<>();

    private ArrayList<String> Manufacturers = new ArrayList<>();
    private ArrayList<String> Interfaces = new ArrayList<>();
    private ArrayList<String> FormFactors = new ArrayList<>();

    private TextView tvClearManufacturer, tvClearCapacity, tvClearInterface, tvClearFormFactor, tvClearRotationSpeed;

    private SharedPreferences sharedPrefs;
    private static final String PREFS_NAME = "filter_prefs";
    public FilterBottomSheetDialog(Context context) {
        super(context);
        setContentView(R.layout.bottomsheet_filter);


        etMinCapacity = findViewById(R.id.et_min_capacity);
        etMaxCapacity = findViewById(R.id.et_max_capacity);
        etMinRpm = findViewById(R.id.et_min_rpm);
        etMaxRpm = findViewById(R.id.et_max_rpm);

        // Инициализация списков с дефолтными значениями
        Manufacturers.addAll(Arrays.asList("Seagate", "Western Digital", "Samsung", "Hitachi", "Toshiba"));
        Interfaces.addAll(Arrays.asList("SATA 1", "SATA 2", "SATA 3"));
        FormFactors.addAll(Arrays.asList("3.5''", "2.5''"));

        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Находим TextView для кнопок "Сбросить"
        tvClearManufacturer = findViewById(R.id.textview_clear_manufactor);
        tvClearCapacity = findViewById(R.id.textview_clear_capacity);
        tvClearInterface = findViewById(R.id.textview_clear_interface);
        tvClearFormFactor = findViewById(R.id.textview_clear_form_factor);
        tvClearRotationSpeed = findViewById(R.id.textview_clear_rotation_speed);

        // Устанавливаем слушателей для кнопок "Сбросить"
        tvClearManufacturer.setOnClickListener(v -> clearFilters("manufacturers"));
        tvClearCapacity.setOnClickListener(v -> clearFilters("capacity"));
        tvClearInterface.setOnClickListener(v -> clearFilters("interfaces"));
        tvClearFormFactor.setOnClickListener(v -> clearFilters("formfactors"));
        tvClearRotationSpeed.setOnClickListener(v -> clearFilters("rotationspeed"));

        // Обработчики CheckBox для производителей
         cbSeagate = findViewById(R.id.checkbox_seagate);
         cbWD = findViewById(R.id.checkbox_wdigital);
         cbSamsung = findViewById(R.id.checkbox_samsung);
         cbHitachi = findViewById(R.id.checkbox_hitachi);
         cbToshiba = findViewById(R.id.checkbox_toshiba);

        cbSeagate.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Seagate", isChecked));
        cbWD.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Western Digital", isChecked));
        cbSamsung.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Samsung", isChecked));
        cbHitachi.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Hitachi", isChecked));
        cbToshiba.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedManufacturers, "Toshiba", isChecked));

        // Обработчики CheckBox для интерфейсов
         cbSata1 = findViewById(R.id.checkbox_sata1);
         cbSata2 = findViewById(R.id.checkbox_sata2);
         cbSata3 = findViewById(R.id.checkbox_sata3);

        cbSata1.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedInterfaces, "SATA 1", isChecked));
        cbSata2.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedInterfaces, "SATA 2", isChecked));
        cbSata3.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedInterfaces, "SATA 3", isChecked));

        // Обработчики CheckBox для форм-факторов
         cbFF2_5 = findViewById(R.id.checkbox_ff_2_5);
         cbFF3_5 = findViewById(R.id.checkbox_ff_3_5);

        cbFF2_5.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedFormFactors, "3.5''", isChecked));
        cbFF3_5.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilterList(selectedFormFactors, "2.5''", isChecked));

        restoreFilterState();

        applyButton = findViewById(R.id.button_apply_filers);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                int minCapacity = getValidValue(etMinCapacity, 0);
                int maxCapacity = getValidValue(etMaxCapacity, Integer.MAX_VALUE);

                int minRpm = getValidValue(etMinRpm, 0);
                int maxRpm = getValidValue(etMaxRpm, Integer.MAX_VALUE);

                if(selectedManufacturers.isEmpty())
                    selectedManufacturers = Manufacturers;
                if(selectedInterfaces.isEmpty())
                    selectedInterfaces = Interfaces;
                if(selectedFormFactors.isEmpty())
                    selectedFormFactors = FormFactors;

                Log.i("Info",minCapacity + "-" + maxCapacity + "\n" +
                        minRpm + "-" + maxRpm + "\n" +
                        selectedManufacturers + "\n" +
                        selectedInterfaces + "\n" +
                        selectedFormFactors);

                saveFilterState();

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

        // Сохранение состояния CheckBox
        editor.putBoolean("checkbox_seagate", cbSeagate.isChecked());
        editor.putBoolean("checkbox_wd", cbWD.isChecked());
        editor.putBoolean("checkbox_samsung", cbSamsung.isChecked());
        editor.putBoolean("checkbox_hitachi", cbHitachi.isChecked());
        editor.putBoolean("checkbox_toshiba", cbToshiba.isChecked());

        editor.putBoolean("checkbox_sata1", cbSata1.isChecked());
        editor.putBoolean("checkbox_sata2", cbSata2.isChecked());
        editor.putBoolean("checkbox_sata3", cbSata3.isChecked());

        editor.putBoolean("checkbox_ff25", cbFF2_5.isChecked());
        editor.putBoolean("checkbox_ff35", cbFF3_5.isChecked());

        // Сохранение значений EditText
        editor.putString("et_min_capacity", etMinCapacity.getText().toString());
        editor.putString("et_max_capacity", etMaxCapacity.getText().toString());
        editor.putString("et_min_rpm", etMinRpm.getText().toString());
        editor.putString("et_max_rpm", etMaxRpm.getText().toString());

        editor.apply();
    }

    private void restoreFilterState() {
        // Восстановление состояния CheckBox
        cbSeagate.setChecked(sharedPrefs.getBoolean("checkbox_seagate", false));
        cbWD.setChecked(sharedPrefs.getBoolean("checkbox_wd", false));
        cbSamsung.setChecked(sharedPrefs.getBoolean("checkbox_samsung", false));
        cbHitachi.setChecked(sharedPrefs.getBoolean("checkbox_hitachi", false));
        cbToshiba.setChecked(sharedPrefs.getBoolean("checkbox_toshiba", false));

        cbSata1.setChecked(sharedPrefs.getBoolean("checkbox_sata1", false));
        cbSata2.setChecked(sharedPrefs.getBoolean("checkbox_sata2", false));
        cbSata3.setChecked(sharedPrefs.getBoolean("checkbox_sata3", false));

        cbFF2_5.setChecked(sharedPrefs.getBoolean("checkbox_ff25", false));
        cbFF3_5.setChecked(sharedPrefs.getBoolean("checkbox_ff35", false));

        // Восстановление значений EditText
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
        if (isChecked) {
            list.add(value);
        } else {
            list.remove(value);
        }
        updateClearButtonVisibility();
    }

    private void clearFilters(String filterType) {
        switch (filterType) {
            case "manufacturers":
                selectedManufacturers.clear();


                // Сброс CheckBox для производителей
                GridLayout manufacturersGridLayout = findViewById(R.id.manufactor_grid_layout);
                for (int i = 0; i < manufacturersGridLayout.getChildCount(); i++) {
                    View child = manufacturersGridLayout.getChildAt(i);
                    if (child instanceof CheckBox) {
                        ((CheckBox) child).setChecked(false);
                    }
                }
                break;
            case "capacity":
                etMinCapacity.setText("");
                etMaxCapacity.setText("");
                break;
            case "interfaces":
                selectedInterfaces.clear();

                // Сброс CheckBox для интерфейсов
                LinearLayout interfacesLinearLayout = findViewById(R.id.interface_linear_layout);
                for (int i = 0; i < interfacesLinearLayout.getChildCount(); i++) {
                    View child = interfacesLinearLayout.getChildAt(i);
                    if (child instanceof CheckBox) {
                        ((CheckBox) child).setChecked(false);
                    }
                }
                break;
            case "formfactors":
                selectedFormFactors.clear();

                // Сброс CheckBox для форм-факторов
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
        findViewById(R.id.textview_clear_interface).setVisibility(
                !selectedInterfaces.isEmpty() ?
                View.VISIBLE : View.GONE);
        findViewById(R.id.textview_clear_form_factor).setVisibility(
                !selectedFormFactors.isEmpty() ?
                        View.VISIBLE : View.GONE);
        findViewById(R.id.textview_clear_rotation_speed).setVisibility(
                !etMinRpm.getText().toString().isEmpty() || !etMaxRpm.getText().toString().isEmpty() ?
                        View.VISIBLE : View.GONE);
    }
}
