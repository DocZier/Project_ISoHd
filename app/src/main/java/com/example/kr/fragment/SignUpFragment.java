package com.example.kr.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.example.kr.database.HardDriveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    EditText usernameEdit;
    EditText emailEdit;
    EditText passwordConfirm;
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
        View root = inflater.inflate(R.layout.fragment_signup, container, false);

        usernameEdit = root.findViewById(R.id.username_signup);
        emailEdit = root.findViewById(R.id.email_signup);
        passwordConfirm = root.findViewById(R.id.password_signup_copy);
        passwordEdit = root.findViewById(R.id.password_signup);
        signupButton = root.findViewById(R.id.signupButton);

        TextView signupTextView = root.findViewById(R.id.loginText);
        TextView returnTextView = root.findViewById(R.id.returnText);

        usernameEdit.addTextChangedListener(signupTextWatcher);
        emailEdit.addTextChangedListener(signupTextWatcher);
        passwordConfirm.addTextChangedListener(signupTextWatcher);
        passwordEdit.addTextChangedListener(signupTextWatcher);

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                LoginFragment loginFragment = new LoginFragment(null, null);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragments_container, loginFragment, "login");
                fragmentTransaction.commit();
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).hideFragment();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                String confirm = passwordConfirm.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!confirm.equals(password))
                {
                    Toast.makeText(requireContext(), "Пароли должны совпадать", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6)
                {
                    Toast.makeText(requireContext(), "Пароль должен состоять не менее чем из 6 символов", Toast.LENGTH_SHORT).show();
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

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Log.d("Firebase", "Username saved: " + username);
                                                }
                                            });

                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(requireContext(), "Вход не выполнен.", Toast.LENGTH_SHORT).show();
                                    isLoggedIn = false;
                                }
                            }
                        });

                ((MainActivity) getActivity()).update();
                ((MainActivity) getActivity()).login(email, password);
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

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
