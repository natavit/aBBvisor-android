package com.abbvisor.abbvisor.selectcharacteristic;

import android.content.Context;
import android.content.SharedPreferences;

import com.abbvisor.abbvisor.manager.AccountManager;
import com.abbvisor.abbvisor.util.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class CharacteristicPresenter implements CharacteristicContract.UserActionsListener {

    private static final String TAG = CharacteristicPresenter.class.getName();

    private final Context mContext;

    private final CharacteristicContract.View mPrefView;

    private final FirebaseUser mUser;

    public CharacteristicPresenter(Context context, CharacteristicContract.View prefView, FirebaseUser user) {
        mContext = context;
        mPrefView = prefView;
        mUser = user;
    }

    @Override
    public void saveSelectedPreferences(List<String> selectedPrefs) {
        SharedPreferences sp = mContext
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

//        String id = Profile.getCurrentProfile().getId();
//        editor.putString(id, id);

        String prefs = new Gson().toJson(selectedPrefs);
        editor.putString(mUser.getUid() + "_prefList", prefs);

        boolean firstLogin = sp.getBoolean(mUser.getUid() + "_firstSignin", true);

        if (firstLogin) {
            editor.putBoolean(mUser.getUid() + "_firstSignin", false);
            editor.apply();
            AccountManager.getInstance(mContext).savePreferences(prefs, CharacteristicFragment.FLAG_NEW_ACTIVITY);
            mPrefView.showMapActivity(CharacteristicFragment.FLAG_NEW_ACTIVITY);
        }
        else {
            editor.apply();
            AccountManager.getInstance(mContext).savePreferences(prefs, CharacteristicFragment.FLAG_UPDATE_CURRENT_ACTIVITY);
            mPrefView.showMapActivity(CharacteristicFragment.FLAG_UPDATE_CURRENT_ACTIVITY);
        }
    }


}
