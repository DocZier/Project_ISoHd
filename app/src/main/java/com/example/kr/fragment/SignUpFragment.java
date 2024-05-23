package com.example.kr.fragment;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    EditText usernameEdit;
    EditText emailEdit;
    EditText passwordEdit;
    Button signupButton;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private boolean isLoggedIn = false;

    public SignUpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup, container, false);

        usernameEdit = root.findViewById(R.id.username_signup);
        emailEdit = root.findViewById(R.id.email_signup);
        passwordEdit = root.findViewById(R.id.password_signup);
        signupButton = root.findViewById(R.id.signupButton);

        TextView signupTextView = root.findViewById(R.id.loginText);
        TextView returnTextView = root.findViewById(R.id.returnText);

        signupButton.setEnabled(false);

        usernameEdit.addTextChangedListener(signupTextWatcher);
        emailEdit.addTextChangedListener(signupTextWatcher);
        passwordEdit.addTextChangedListener(signupTextWatcher);

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragments_container, loginFragment, "login");
                fragmentTransaction.commit();
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).hideSignupFragment();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("username", username);
                                    userMap.put("email", email);
                                    userMap.put("favorites", new ArrayList<HardDriveData>());

                                    mDatabase.child("users").child(userId).setValue(userMap);

                                    Log.d(TAG, "createUserWithEmail:success");

                                    isLoggedIn = true;

                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Вход не выполнен.", Toast.LENGTH_SHORT).show();
                                    isLoggedIn = false;
                                }
                            }
                        });

                ((MainActivity) getActivity()).hideSignupFragment();
            }
        });

        return root;
    }

    private TextWatcher signupTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = usernameEdit.getText().toString().trim();
            String emailInput = emailEdit.getText().toString().trim();
            String passwordInput = passwordEdit.getText().toString().trim();

            signupButton.setEnabled(!usernameInput.isEmpty() && !emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
