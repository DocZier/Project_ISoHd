package com.example.kr.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment
{
    private FirebaseAuth mAuth;
    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        Button returnButton = root.findViewById(R.id.return_button);
        Button logoutButton = root.findViewById(R.id.logout_button);

        TextView usernameText = root.findViewById(R.id.username);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideAccountFragment();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                ((MainActivity) getActivity()).hideAccountFragment();
            }
        });
        return root;
    }
}