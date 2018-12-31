package com.example.canteenchecker.canteenmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.toString();

    private static final String DEFAULT_LOGIN_DATA = "S1610307018";

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        // do not use this in production
        edtUsername.setText(DEFAULT_LOGIN_DATA);
        edtPassword.setText(DEFAULT_LOGIN_DATA);

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogIn();
            }
        });
    }

    private void setUIEnabled(boolean enabled) {
        btnLogin.setEnabled(enabled);
        edtPassword.setEnabled(enabled);
        edtUsername.setEnabled(enabled);
    }

    @SuppressLint("StaticFieldLeak")
    private void LogIn() {
        setUIEnabled(false);

        String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    return new ServiceProxy().Login(strings[0], strings[1]);
                } catch (IOException e) {
                    Log.e(TAG, getString(R.string.msg_loginFailed), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String authenticationToken) {
                if (authenticationToken != null) {
                    // login successful
                    CanteenManagerApplication.getInstance().setAuthenticationToken(authenticationToken);

                    // start main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    edtPassword.setText(null);
                    setUIEnabled(true);
                    Toast.makeText(LoginActivity.this, R.string.msg_loginFailed, Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(username, password);
    }
}

