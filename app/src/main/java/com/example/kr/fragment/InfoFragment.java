package com.example.kr.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class InfoFragment extends Fragment {
    private ScrollView scrollView;
    private FloatingActionButton fabScrollToTop;

    public InfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        scrollView = root.findViewById(R.id.scrollView);
        fabScrollToTop = root.findViewById(R.id.fabScrollToTop);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > 200) {
                fabScrollToTop.show();
            } else {
                fabScrollToTop.hide();
            }
        });

        fabScrollToTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));

        return root;
    }

}