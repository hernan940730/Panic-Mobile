package com.panic.security.controllers.login_sign_up_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.CouchbaseDAO;
import com.panic.security.utils.FirebaseDAO;

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

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null /*&& user.isEmailVerified()*/ ){
                    /* User signed in */
                    showHome();
                } else{
                    /* Log In User */
                    updateUI();
                }
            }
        };

    }

    private void updateUI() {
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById( R.id.facebook_login_button );
        loginButton.setReadPermissions( "email", "public_profile" );
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookLogin( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                updateUI();
            }

            @Override
            public void onError(FacebookException error) {
                updateUI();
            }
        });

        progressbar = ( ProgressBar ) findViewById( R.id.progress_bar );

        mEmailEditText = ( EditText ) findViewById( R.id.email );
        mPasswordEditText = ( EditText ) findViewById( R.id.password );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void showSignUpActivity(View view ) {
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity( intent );
    }

    public void showResetPasswordActivity( View view ){
        Intent intent = new Intent( this, ResetPasswordActivity.class );
        startActivity( intent );
    }


    private void facebookLogin( AccessToken token ) {
        progressbar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Profile profile = new Profile(
                                    null,
                                    null,
                                    0,
                                    null,
                                    null,
                                    null,
                                    null
                            );

                            FirebaseDAO firebaseDAO = FirebaseDAO.getInstance();
                            String profileID = firebaseDAO.pushProfile( user.getUid(), profile );

                            User fireBaseUser = new User (
                                    user.getUid(),
                                    user.getEmail(),
                                    true,
                                    null,
                                    profileID);

                            firebaseDAO.pushUser( user.getUid(), fireBaseUser );
                            showHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getResources().getString( R.string.authentication_failed ),
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }


                    }
                });
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
                            if( user.isEmailVerified() ){
                                showHome();
                            }else{
                                String s = getResources().getString( R.string.email_not_verified );
                                s = String.format( s, user.getEmail() );
                                Toast.makeText( LoginActivity.this, s, Toast.LENGTH_LONG ).show();
                            }
                        } else {
                            // If sign in fails, display a message to the USER_REFERENCE.
                            Toast.makeText(LoginActivity.this, getResources().getString( R.string.authentication_failed ),
                                    Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
