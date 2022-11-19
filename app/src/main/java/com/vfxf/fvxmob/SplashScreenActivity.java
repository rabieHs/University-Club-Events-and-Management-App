package com.vfxf.fvxmob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 3500;
    Animation TopAnim, BottomAnim;
    ImageView Logo;
    TextView Welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        TopAnim = AnimationUtils.loadAnimation(this,R.anim.animation);
        BottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        Logo = findViewById(R.id.logo);
        Welcome = findViewById(R.id.welcome);
        Logo.setAnimation(TopAnim);
        Welcome.setAnimation(BottomAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(MainIntent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}