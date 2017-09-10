package com.panic.security.controllers.login_sign_up_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if( user != null ){
                    showHome();
                }else{
                    updateUI();
                }
            }
        };
    }

    private void updateUI() {
        setContentView( R.layout.activity_sign_up );
        progressbar = ( ProgressBar ) findViewById( R.id.progress_bar );
        mEmailEditText = ( EditText ) findViewById( R.id.email );
        mPasswordEditText = ( EditText ) findViewById( R.id.password );
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showHome() {
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        finishAffinity();
    }

    public void showLogIn( View view ){
        Intent intent = new Intent( this, LoginActivity.class );
        startActivity( intent );
        finish();
    }

    public void signUp(View view) {
        if (!validateForm()) {
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString() )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressbar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            showHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Required.");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Required.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }
}
