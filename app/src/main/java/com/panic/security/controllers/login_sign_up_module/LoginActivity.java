package com.panic.security.controllers.login_sign_up_module;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.panic.security.DBRegistersGenerator;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.firebase_utils.CouchbaseDAO;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with USER_REFERENCE interaction.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar progressbar;

    private int loginAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCouchbase();
        loginAttempts = 0;
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
                    updateUI();
                }
            }
        };

    }

    private void updateUI() {
        setContentView(R.layout.activity_login);

        progressbar = ( ProgressBar ) findViewById( R.id.progress_bar );

        mEmailEditText = ( EditText ) findViewById( R.id.email );
        mPasswordEditText = ( EditText ) findViewById( R.id.password );

        if( loginAttempts < 4 ) {
            findViewById(R.id.password_reset).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.password_reset).setVisibility(View.VISIBLE);
        }
    }

    private void showHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

    public void showSignUpActivity(View view ) {
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity( intent );
    }

    public void showResetPasswordActivity( View view ){
        Intent intent = new Intent( this, ResetPasswordActivity.class );
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
                            // Sign in success, update UI with the signed-in USER_REFERENCE's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            showHome();
                        } else {
                            // If sign in fails, display a message to the USER_REFERENCE.
                            Toast.makeText(LoginActivity.this, getResources().getString( R.string.authentication_failed ),
                                    Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
                            loginAttempts += 1;
                            updateUI();
                        }

                        // ...
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError( getResources().getString( R.string.required )  );
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError( getResources().getString( R.string.required )   );
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    public void login(View view) {
        signIn( );
    }

    private void initCouchbase () {
        CouchbaseDAO.getInstance (this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
