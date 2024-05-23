package com.example.kr.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kr.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class InfoFragment extends Fragment {
    private ScrollView scrollView;
    private FloatingActionButton fabScrollToTop;
    private TextView capacityTitle, capacityDescription, interfaceTitle, interfaceDescription, formFactorTitle, formFactorDescription, spindleSpeedTitle, spindleSpeedDescription;


    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        scrollView = root.findViewById(R.id.scrollView);
        fabScrollToTop = root.findViewById(R.id.fabScrollToTop);

        capacityTitle = root.findViewById(R.id.capacityTitle);
        capacityDescription = root.findViewById(R.id.capacityDescription);
        interfaceTitle = root.findViewById(R.id.interfaceTitle);
        interfaceDescription = root.findViewById(R.id.interfaceDescription);
        formFactorTitle = root.findViewById(R.id.formFactorTitle);
        formFactorDescription = root.findViewById(R.id.formFactorDescription);
        spindleSpeedTitle = root.findViewById(R.id.spindleSpeedTitle);
        spindleSpeedDescription = root.findViewById(R.id.spindleSpeedDescription);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > 200) {
                fabScrollToTop.show();
            } else {
                fabScrollToTop.hide();
            }
        });

        fabScrollToTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));

        capacityTitle.setOnClickListener(v -> toggleDescriptionVisibility(capacityDescription));
        interfaceTitle.setOnClickListener(v -> toggleDescriptionVisibility(interfaceDescription));
        formFactorTitle.setOnClickListener(v -> toggleDescriptionVisibility(formFactorDescription));
        spindleSpeedTitle.setOnClickListener(v -> toggleDescriptionVisibility(spindleSpeedDescription));

        return root;
    }

    private void toggleDescriptionVisibility(TextView description) {
        if (description.getVisibility() == View.GONE) {
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }
    }
}