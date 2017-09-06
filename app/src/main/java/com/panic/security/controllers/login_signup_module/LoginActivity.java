package com.panic.security.controllers.login_signup_module;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.panic.security.R;
import com.panic.security.controllers.home_module.HomeActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mSignUpLink;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null ){
                    /* User signed in */
                    showHome();
                }else{
                    /* Log In User */
                    setContentView(R.layout.activity_login);

                    progressbar = ( ProgressBar ) findViewById( R.id.progress_bar );

                    mEmailEditText = ( EditText ) findViewById( R.id.email );
                    mPasswordEditText = ( EditText ) findViewById( R.id.password );

                    mLoginButton = ( Button ) findViewById( R.id.login_button );

                    mSignUpLink = ( TextView ) findViewById( R.id.sign_up_link );

                }
            }
        };

    }

    private void showHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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

    private void showSignUp() {
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity( intent );
    }

    private void signIn( ) {
        if (!validateForm()) {
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            showHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
                            return;
                        }

                        // ...
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

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void login(View view) {
        signIn( );
    }
}
