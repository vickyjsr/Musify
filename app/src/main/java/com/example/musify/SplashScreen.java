package com.example.musify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private TextView slogan;
    private View vtop1,vtop2,vtop3;
    private View vbot1,vbot2,vbot3;
    private ImageView logo,iv1,iv2,iv3,iv4,iv5,iv6,iv7,iv8;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        slogan = findViewById(R.id.slogan);
        logo = findViewById(R.id.logo);

        iv1 = findViewById(R.id.imageView1);
        iv2 = findViewById(R.id.imageView2);
        iv3 = findViewById(R.id.imageView3);
        iv4 = findViewById(R.id.imageView4);
        iv5 = findViewById(R.id.imageView5);
        iv6 = findViewById(R.id.imageView6);
        iv7 = findViewById(R.id.imageView7);
        iv8 = findViewById(R.id.imageView8);


        vtop1 = findViewById(R.id.v1);
        vtop2 = findViewById(R.id.v2);
        vtop3 = findViewById(R.id.v3);

        vbot1 = findViewById(R.id.v4);
        vbot2 = findViewById(R.id.v5);
        vbot3 = findViewById(R.id.v6);

        Animation logoanim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);

        Animation animiv1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);

        Animation animiv4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv5 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv6 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv7 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);
        Animation animiv8 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_animation);


        Animation vt1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.topviewanim);
        Animation vt2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.topviewanim);
        Animation vt3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.topviewanim);

        Animation vb1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottomviewanim);
        Animation vb2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottomviewanim);
        Animation vb3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottomviewanim);

        vtop1.startAnimation(vt1);
        vbot1.startAnimation(vb1);

        vt1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                vtop1.setVisibility(View.VISIBLE);
                vbot1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vtop2.setVisibility(View.VISIBLE);
                vbot2.setVisibility(View.VISIBLE);

                vtop2.startAnimation(vt2);
                vbot2.startAnimation(vb2);

                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.VISIBLE);
                iv5.setVisibility(View.VISIBLE);
                iv6.setVisibility(View.VISIBLE);
                iv7.setVisibility(View.VISIBLE);
                iv8.setVisibility(View.VISIBLE);

                iv1.startAnimation(animiv1);
                iv2.startAnimation(animiv2);
                iv3.startAnimation(animiv3);
                iv4.startAnimation(animiv4);
                iv5.startAnimation(animiv5);
                iv6.startAnimation(animiv6);
                iv7.startAnimation(animiv7);
                iv8.startAnimation(animiv8);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        vt2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vtop3.setVisibility(View.VISIBLE);
                vbot3.setVisibility(View.VISIBLE);

                vtop3.startAnimation(vt3);
                vbot3.startAnimation(vb3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        vt3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.VISIBLE);
                logo.setAnimation(logoanim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        logoanim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                slogan.setVisibility(View.VISIBLE);
                final String sl = slogan.getText().toString();
                slogan.setText("");
                count = 0;

                new CountDownTimer(sl.length() * 100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        slogan.setText(String.format("%s%s", slogan.getText().toString(), sl.charAt(count)));
                        count++;
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(mainIntent);
            customType(SplashScreen.this,"fadein-to-fadeout");
            finish();
        }, SPLASH_DISPLAY_LENGTH);

    }
}