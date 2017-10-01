package com.panic.security.controllers.login_sign_up_module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = ( EditText )findViewById( R.id.email );

        findViewById( R.id.password_reset_button ).setOnClickListener( this );
        findViewById( R.id.close_button ).setOnClickListener( this );
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if( i == R.id.password_reset_button ){
            sendResetPasswordEmail();
        }
        if( i == R.id.close_button ){
            finish();
        }
    }

    private void sendResetPasswordEmail() {
        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError( getResources().getString( R.string.required )  );
        } else {
            mEmailEditText.setError(null);
            mAuth.sendPasswordResetEmail( email );
            Toast.makeText( ResetPasswordActivity.this, getResources().getString( R.string.emailSent ) + email, Toast.LENGTH_SHORT ).show();
            finish();
        }

    }
}
