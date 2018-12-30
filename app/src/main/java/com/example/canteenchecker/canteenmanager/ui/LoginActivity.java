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

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = LoginActivity.class.toString();

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
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
                    Log.e(TAG, "Login failed", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String authenticationToken) {
                if (authenticationToken != null) {
                    // login successfull
                    CanteenManagerApplication.getInstance().setAuthenticationToken(authenticationToken);
                } else {
                    edtPassword.setText(null);
                    setUIEnabled(true);
                    Toast.makeText(LoginActivity.this, R.string.msg_loginFailed, Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(username, password);
    }
}

