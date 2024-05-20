package com.example.kr.fragment;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpFragment extends Fragment {
    EditText usernameEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;
    FirebaseAuth mAuth;
    public SignUpFragment()
    {

    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();

        usernameEdit = root.findViewById(R.id.username_signup);
        passwordEdit = root.findViewById(R.id.password_signup);
        passwordConfirmEdit = root.findViewById(R.id.password_signup_copy);

        Button loginButton = root.findViewById(R.id.signupButton);

        TextView loginTextView = root.findViewById(R.id.loginText);
        TextView returnTextView = root.findViewById(R.id.returnText);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragments_container, loginFragment, "login");
                fragmentTransaction.commit();
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                //TODO remove fragment(if it still exist)
                getActivity().findViewById(R.id.fragments_container).setVisibility(GONE);
                getActivity().findViewById(R.id.main_layout).setVisibility(VISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String password_confirm = passwordConfirmEdit.getText().toString();

                if (username.isEmpty() || password.isEmpty() || password_confirm.isEmpty()) {
                    if (username.isEmpty()) {
                        return;
                    }
                    if (password.isEmpty()) {
                        return;
                    }
                    return;
                }
                if(!password.equals(password_confirm))
                {
                    return;
                }
                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return root;
    }

    //TODO add textwatcher
}