package com.spmaurya.exposys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String WEBSITE_URL = "http://www.exposysdata.com";

    private TextInputEditText mEmail;
    private TextInputLayout mEmailLayout;

    private TextInputEditText mPassword;
    private TextInputLayout mPasswordLayout;

    private ContactDialog mContactDialog;
    private AboutDialog mAboutDialog;

    private User mUser;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        switch (getIntent().getIntExtra("comingFrom", -1)) {
            case 0: // Coming from Splash Screen
                if (sharedPreferences.getBoolean("login", false)) {
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("user", User.getFrom(sharedPreferences));
                    startActivity(intent);
                    finish();
                }
                break;
            case 1: // Coming from Registration Activity
                User.putTo(sharedPreferences, getIntent().getBundleExtra("user"));
                sharedPreferences.edit().putBoolean("login", true).apply();
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("user", User.getFrom(sharedPreferences));
                startActivity(intent);
                finish();
                break;
            case 2: // Coming from Dashboard Activity
                sharedPreferences.edit().putBoolean("login", false).apply();
                User.putToClear(sharedPreferences);
        }

        mEmail = findViewById(R.id.log_email);
        mEmailLayout = findViewById(R.id.log_email_layout);

        mPassword = findViewById(R.id.log_password);
        mPasswordLayout = findViewById(R.id.log_password_layout);

        mContactDialog = new ContactDialog(this);
        mAboutDialog = new AboutDialog(this);

        mUser = new User();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        initListeners();
    }

    private void initListeners() {

        // Goto website intent
        findViewById(R.id.log_footer_website).setOnClickListener(view ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL))));

        findViewById(R.id.log_having_trouble).setOnClickListener(view -> new ResetDialog(this,
                () -> Toast.makeText(this, "Working Fine!", Toast.LENGTH_SHORT).show()).show());

        // Login validation and goto profile intent
        findViewById(R.id.log_login).setOnClickListener(view -> {

            if (emailValid() && passwordValid() && isConnected()) {

                mUser.setEmail(Objects.requireNonNull(mEmail.getText()).toString().trim());
                mUser.setPassword(Objects.requireNonNull(mPassword.getText()).toString());

                mDialog.setMessage("Wait for a moment...");
                mDialog.show();

                mUser.fetch(new User.OnLoginListener() {

                    @Override
                    public void onIncorrect() {
                        Validator.requestFocus(mPasswordLayout, mPassword);
                        mPasswordLayout.setError("Incorrect Password!");
                        mDialog.dismiss();
                    }

                    @Override
                    public void onNotExist() {
                        Validator.requestFocus(mEmailLayout, mEmail);
                        mPasswordLayout.setError("Incorrect Email!");
                        mDialog.dismiss();
                    }

                    @Override
                    public void onSuccess() {

                        Intent intent = new Intent(
                                LoginActivity.this, DashboardActivity.class);

                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        Bundle bundle = mUser.toBundle();
                        User.putTo(sharedPreferences, bundle);
                        sharedPreferences.edit().putBoolean("login", true).apply();

                        intent.putExtra("user", bundle);
                        startActivity(intent);
                        mDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFail() {
                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Goto Registration page
        findViewById(R.id.log_register).setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        Validator.setTextWatcher(mEmail, () -> Validator.email(mEmail, mEmailLayout));
        Validator.setTextWatcher(mPassword, () ->
                Validator.validateText(mPassword, mPasswordLayout, "Enter password!"));

        findViewById(R.id.log_footer_contact).setOnClickListener(view -> mContactDialog.show());

        findViewById(R.id.log_footer_about).setOnClickListener(view -> mAboutDialog.show());
    }

    private boolean passwordValid() {
        if (!Validator.validateText(mPassword, mPasswordLayout, "Enter password!")) {
            Validator.requestFocus(mPasswordLayout, mPassword);
            return false;
        }
        return true;
    }

    private boolean emailValid() {
        if (!Validator.email(mEmail, mEmailLayout)) {
            Validator.requestFocus(mEmailLayout, mEmail);
            return false;
        }
        return true;
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        Toast.makeText(this, "No Network!", Toast.LENGTH_SHORT).show();
        return false;
    }
}