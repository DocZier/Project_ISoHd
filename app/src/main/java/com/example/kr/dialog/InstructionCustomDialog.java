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

public class InstructionCustomDialog extends DialogFragment  {
    public InstructionCustomDialog() {
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
        View root = inflater.inflate(R.layout.dialog_instructions, container, false);

        Button hddsButton = root.findViewById(R.id.about_hdds);
        Button profileButton = root.findViewById(R.id.about_profile);
        Button mapButton = root.findViewById(R.id.about_map);


        hddsButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_hdds");
        });
        profileButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_profile");
        });
        mapButton.setOnClickListener(v -> {

            ((MainActivity) getActivity()).showCustomDialog("a_map");
        });
        return root;
    }
}
