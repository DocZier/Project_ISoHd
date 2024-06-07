package com.example.kr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;

public class AboutProfileCustomDialog extends DialogFragment  {
    public AboutProfileCustomDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View root =  inflater.inflate(R.layout.dialog_about_profile, container, false);

        Button historyButton = root.findViewById(R.id.about_history);
        Button favoritesButton = root.findViewById(R.id.about_favorites);
        Button calculatorButton = root.findViewById(R.id.about_calculator);


        historyButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showCustomDialog("a_history");
        });
        favoritesButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_favorites");
        });
        calculatorButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_calculator");
        });

        return root;
    }
}
