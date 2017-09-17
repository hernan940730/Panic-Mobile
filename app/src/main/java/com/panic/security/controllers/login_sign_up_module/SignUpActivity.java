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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.firebase_utils.FirebaseReferences;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mName;
    private EditText mLastName;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfEditText;

    private ProgressBar progressbar;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        mName = ( EditText ) findViewById( R.id.name );
        mLastName = ( EditText ) findViewById( R.id.last_name );
        mEmailEditText = ( EditText ) findViewById( R.id.email );
        mPasswordEditText = ( EditText ) findViewById( R.id.password );
        mPasswordConfEditText = ( EditText ) findViewById( R.id.password_confirmation );
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
                            DatabaseReference ref = database.getReference(FirebaseReferences.PROFILES_REFERENCE);
                            DatabaseReference childRef = ref.push();
                            childRef.child(FirebaseReferences.Profile.LAST_NAME_REFERENCE).setValue(
                                    mLastName.getText().toString() );
                            childRef.child(FirebaseReferences.Profile.NAME_REFERENCE).setValue(
                                    mName.getText().toString() );
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

        String name = mName.getText().toString();

        if( TextUtils.isEmpty( name ) ){
            mName.setError( getResources().getString( R.string.required ) );
            valid = false;
        }else{
            mName.setError( null );
        }

        String lastName = mLastName.getText().toString();

        if( TextUtils.isEmpty( lastName ) ){
            mLastName.setError( getResources().getString( R.string.required ) );
            valid = false;
        }else{
            mLastName.setError( null );
        }

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError( getResources().getString( R.string.required ) );
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError( getResources().getString( R.string.required ) );
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        String passwordConf = mPasswordConfEditText.getText().toString();
        if (TextUtils.isEmpty(passwordConf)) {
            mPasswordConfEditText.setError( getResources().getString( R.string.required ) );
            valid = false;
        } else {
            mPasswordConfEditText.setError(null);
        }

        if( valid ){
            if( !password.equals( passwordConf ) ){
                valid = false;
                Toast.makeText( SignUpActivity.this, getResources().getString( R.string.password_match ), Toast.LENGTH_SHORT ).show();
            }else{
                mPasswordConfEditText.setError( null );
            }

        }
        return valid;
    }
}
