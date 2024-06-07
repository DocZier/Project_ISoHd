package com.example.kr.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kr.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CalculatorBottomSheet extends BottomSheetDialog {

    private EditText IDiskNormalCapacity;
    private TextView diskComputerCapacity;
    public CalculatorBottomSheet(Context context)
    {
        super(context);
        setContentView(R.layout.bottomsheet_calculator);

        IDiskNormalCapacity = findViewById(R.id.input_normal_cap);
        diskComputerCapacity = findViewById(R.id.computer_capacity);


        Button clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                IDiskNormalCapacity.setText("");

                diskComputerCapacity.setText("0.0");

            }
        });

        IDiskNormalCapacity.addTextChangedListener(new Converter(IDiskNormalCapacity, diskComputerCapacity));
    }


    private class Converter implements TextWatcher
    {
        private EditText inputDiskCapacity;
        private TextView resultDiskCapacity;
        public Converter(EditText inputDiskCapacity, TextView resultDiskCapacity) {
            this.inputDiskCapacity=inputDiskCapacity;
            this.resultDiskCapacity = resultDiskCapacity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length()>0) {
                double capacityDisk = (Double.parseDouble(s.toString()) * 1000*1000*1000)/(1024*1024*1024);
                resultDiskCapacity.setText(capacityDisk + "");
            }
        }
    }
}
