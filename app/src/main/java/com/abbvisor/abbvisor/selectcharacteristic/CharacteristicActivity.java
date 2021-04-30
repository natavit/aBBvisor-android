package com.abbvisor.abbvisor.selectcharacteristic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.abbvisor.abbvisor.R;

import java.util.ArrayList;
import java.util.Collections;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CharacteristicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        
        initInstances();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, CharacteristicFragment.newInstance(createFirstPreferences()), "CharacteristicFragment")
                    .commit();
        }
    }

    private void initInstances() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_preferences));
    }

    private ArrayList<String> createFirstPreferences(){
        String[] prefs = getResources().getStringArray(R.array.personalities);

        ArrayList<String> prefList = new ArrayList<>();

        Collections.addAll(prefList, prefs);

        return prefList;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
