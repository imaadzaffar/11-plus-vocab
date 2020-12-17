package com.zafaris.elevenplusvocab.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.zafaris.elevenplusvocab.R;
import com.zafaris.elevenplusvocab.ui.main.MainActivity;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundResource(R.mipmap.ic_launcher_background)
                //.withHeaderText("Header")
                //.withFooterText("Zafaris Developers")
                //.withBeforeLogoText("Before Logo Text")
                //.withAfterLogoText("After Logo Text")
                .withLogo(R.mipmap.ic_launcher_foreground);

        config.getLogo().getLayoutParams().height = 1000;
        config.getLogo().getLayoutParams().width = 1000;
        //config.getHeaderTextView().setTextColor(Color.WHITE);
        //config.getFooterTextView().setTextColor(Color.WHITE);
        //config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        //config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
