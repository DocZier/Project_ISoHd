package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.database.HardDriveData;
import com.example.kr.database.HistoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment {
    EditText usernameEdit;
    EditText passwordEdit;
    FirebaseAuth mAuth;
    Button loginButton;

    private String email;
    private String password;

    public LoginFragment(String email, String password) {
        this.email=email;
        this.password=password;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).hideFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEdit = root.findViewById(R.id.username_login);
        passwordEdit = root.findViewById(R.id.password_login);
        loginButton = root.findViewById(R.id.loginButton);

        loginButton.setEnabled(false);

        usernameEdit.addTextChangedListener(loginTextWatcher);
        passwordEdit.addTextChangedListener(loginTextWatcher);

        if(email!=null && password!=null)
        {
            usernameEdit.setText(email);
            passwordEdit.setText(password);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    syncHistory();
                                    ((MainActivity) getActivity()).hideFragment();
                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Ошибка авторизации.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        syncHistory();
                                        ((MainActivity) getActivity()).hideFragment();
                                    }
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Ошибка авторизации.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        TextView signupTextView = root.findViewById(R.id.signupText);
        TextView returnTextView = root.findViewById(R.id.returnText);

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragments_container, signUpFragment, "signup");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                ((MainActivity) getActivity()).update();
                ((MainActivity) getActivity()).hideFragment();
            }
        });

        return root;
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = usernameEdit.getText().toString().trim();
            String passwordInput = passwordEdit.getText().toString().trim();

            loginButton.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };



    public void syncHistory() {
        ArrayList<HistoryData> offlineHistory = new ArrayList<>();
        ArrayList<HistoryData> onlineHistory = new ArrayList<>();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("history_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference historyRef = db.collection("users").document(userId).collection("history");

            historyRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<HardDriveData> helper = (List<HardDriveData>) document.get("hardDriveDataList");
                                    HistoryData historyData = new HistoryData(document.get("date").toString(), new ArrayList<>(helper));

                                    Log.i("History", helper.toString());
                                    offlineHistory.add(historyData);
                                }
                            } else {
                                Log.d(TAG, "Error getting hdds: ", task.getException());
                            }
                        }
                    });
        }


        for (int i = 0; i < onlineHistory.size(); i++) {
            HistoryData historyData = getHistoryDataForDate(sharedPreferences, onlineHistory.get(i).getDate());
            ArrayList<HardDriveData> hardDriveData = onlineHistory.get(i).getHardDriveDataList();
            if (historyData != null) {
                if (!historyData.getHardDriveDataList().equals(hardDriveData)) {
                    ArrayList<HardDriveData> combinedList = new ArrayList<>(historyData.getHardDriveDataList());
                    combinedList.addAll(hardDriveData);

                    historyData.setHardDriveDataList(combinedList);

                    String jsonHistory = new Gson().toJson(historyData);
                    editor.putString(onlineHistory.get(i).getDate(), jsonHistory);
                    editor.apply();
                }

            } else {
                historyData = new HistoryData(onlineHistory.get(i).getDate(), new ArrayList<>());
                historyData.getHardDriveDataList().addAll(hardDriveData);

                String jsonHistory = new Gson().toJson(historyData);
                editor.putString(onlineHistory.get(i).getDate(), jsonHistory);
                editor.apply();
            }
        }
    }

    private HistoryData getHistoryDataForDate(SharedPreferences sharedPreferences, String date) {
        String jsonHistory = sharedPreferences.getString(date, null);
        if (jsonHistory != null) {
            Type historyDataType = new TypeToken<HistoryData>() {}.getType();
            return new Gson().fromJson(jsonHistory, historyDataType);
        }
        return null;
    }
}
