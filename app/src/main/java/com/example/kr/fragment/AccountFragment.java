package com.example.kr.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.dialog.CalculatorBottomSheet;
import com.example.kr.dialog.SortBottomSheet;
import com.example.kr.model.AdapterRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment
{
    private String userId = null;
    private ViewSwitcher currentViewSwitcher = null;
    TextView usernameText;
    String username="";
    private View firstView;
    private View secondView;
    public AccountFragment() {
        userId = null;

        if(currentViewSwitcher!=null)
            changeView();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
    }

    public void updateFragment()
    {
        if(currentViewSwitcher!=null)
            changeView();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if(usernameText!=null)
            {
                usernameText.setText(username);
            }
        }
        else
            userId = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).setCurrentPage(0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        currentViewSwitcher = root.findViewById(R.id.viewSwitcher);

        firstView = root.findViewById(R.id.login_register_view);
        secondView = root.findViewById(R.id.account_view);

        Button logoutButton = root.findViewById(R.id.logout_button);

        usernameText = root.findViewById(R.id.username);

        changeView();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if(usernameText!=null)
            {
                usernameText.setText(username);
            }
        }


        Button loginButton = root.findViewById(R.id.login_button);
        Button signupButton = root.findViewById(R.id.signup_button);
        Button historyButton = root.findViewById(R.id.history_button);
        Button statisticButton = root.findViewById(R.id.stat_button);
        Button favoritesButton = root.findViewById(R.id.favorite_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showFragment("login");
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showFragment("signup");
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showFragment("favorite");
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showFragment("history");
            }
        });

        statisticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorBottomSheet dialog = new CalculatorBottomSheet(requireContext());
                dialog.show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                changeView();
            }
        });
        return root;
    }

    private void changeView()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            if (currentViewSwitcher.getCurrentView() == firstView)
                currentViewSwitcher.showNext();
        }
        else
        {
            if (currentViewSwitcher.getCurrentView() == secondView)
                currentViewSwitcher.showPrevious();
        }
    }
}