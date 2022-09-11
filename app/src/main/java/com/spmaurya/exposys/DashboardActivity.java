package com.spmaurya.exposys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();

    private User mUser;
    private ImageView mAvatar;
    private ProgressBar mProgressBar;
    private View mAvatarDimmer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_dashboard);

        mUser = new User();
        mUser.updateFrom(getIntent().getBundleExtra("user"));

        findViewById(R.id.dash_logout).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.putExtra("comingFrom", 2);
            startActivity(intent);
            finish();
        });

        init();
    }

    void init() {
        mAvatar = findViewById(R.id.dash_avatar);
        TextView mName = findViewById(R.id.dash_name);
        TextView mGender = findViewById(R.id.dash_gender);
        TextView mBranch = findViewById(R.id.dash_study_branch);
        TextView mCollege = findViewById(R.id.dash_study_college);
        TextView mEmail = findViewById(R.id.dash_email);
        TextView mPhone = findViewById(R.id.dash_phone);
        TextView mPercentage10 = findViewById(R.id.dash_10);
        TextView mPercentage12 = findViewById(R.id.dash_12);
        TextView mPercentageUG = findViewById(R.id.dash_ug);
        TextView mPercentagePG = findViewById(R.id.dash_pg);
        TextView mInternship = findViewById(R.id.dash_internship_opt);
        TextView mInternshipLocation = findViewById(R.id.dash_internship_location);
        TextView mInternshipDuration = findViewById(R.id.dash_internship_duration);
        mProgressBar = findViewById(R.id.dash_avatar_progress);
        mAvatarDimmer = findViewById(R.id.dash_avatar_dimmer);

        switch (mUser.getGender()) {
            case "Male": mAvatar.setImageResource(R.drawable.ic_avatar_male); break;
            case "Female": mAvatar.setImageResource(R.drawable.ic_avatar_female); break;
            case "Other": mAvatar.setImageResource(R.drawable.ic_avatar_other); break;
            default: mAvatar.setImageResource(R.drawable.ic_avatar);
        }

        if (mUser.getAvatar() != null && !mUser.getAvatar().isEmpty() && isConnected()) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(() -> {
                runOnUiThread(() -> showAvatarProgress(true));
                try {
                    URL url = new URL(mUser.getAvatar());
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    runOnUiThread(() -> {
                        mAvatar.setImageBitmap(bitmap);
                        showAvatarProgress(false);
                    });
                } catch (IOException e) {
                    Log.e(TAG, "init -> service: " + e.getMessage(), e);
                }
            });
            service.shutdown();
        }

        mName.setText(mUser.getFullName());
        mGender.setText(mUser.getGender());
        mBranch.setText(mUser.getBranch());
        mCollege.setText(mUser.getCollege());
        mEmail.setText(mUser.getEmail());
        mPhone.setText(mUser.getPhone());
        mPercentage10.setText(String.format("%s%%", mUser.getPercentage10()));
        mPercentage12.setText(String.format("%s%%", mUser.getPercentage12()));
        mPercentageUG.setText(String.format("%s%%", mUser.getPercentageUG()));
        mPercentagePG.setText(String.format("%s%%", mUser.getPercentagePG()));
        mInternship.setText(mUser.getInternship());
        mInternshipLocation.setText(mUser.getLocation());
        mInternshipDuration.setText(mUser.getDuration());
    }

    private void showAvatarProgress(boolean flag) {
        if (flag) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAvatarDimmer.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mAvatarDimmer.setVisibility(View.GONE);
        }
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