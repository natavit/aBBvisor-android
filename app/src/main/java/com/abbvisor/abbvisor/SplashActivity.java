package com.abbvisor.abbvisor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abbvisor.abbvisor.signin.SigninActivity;

/**
 * Created by natavit on 4/8/2017 AD.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
        finish();
    }
}
