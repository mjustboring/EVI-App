package com.spmaurya.exposys;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.splash_screen);

        Animation anim_a = AnimationUtils.loadAnimation(this, R.anim.splash_text1_anim);
        Animation anim_b = AnimationUtils.loadAnimation(this, R.anim.splash_text2_anim);
        findViewById(R.id.splash_text1).startAnimation(anim_a);
        findViewById(R.id.splash_text2).startAnimation(anim_b);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.putExtra("comingFrom", 0);
            startActivity(intent);
            finish();
        }, 2000);
    }
}