package com.abbvisor.abbvisor.scanbeacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.databinding.ActivityBeaconScannerBinding;
import com.abbvisor.abbvisor.manager.AccountManager;
import com.abbvisor.abbvisor.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BeaconScannerActivity extends AppCompatActivity {

    private static final String TAG = BeaconScannerActivity.class.getSimpleName();

    ActivityBeaconScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_beacon_scanner);

        checkUserStatus();

        boolean isOpenedFromNoti = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        AccountManager.getInstance(this).setOpenedFromNoti(isOpenedFromNoti);

        initInstances();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, BeaconScannerFragment.newInstance(), "BeaconScannerFragment")
                    .commit();
        }
    }

    private void checkUserStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }

    private void initInstances() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences sp = this
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("running_scanner", true);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = this
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("running_scanner", false);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
