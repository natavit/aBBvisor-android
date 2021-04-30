package com.abbvisor.abbvisor.updateprofile;

import android.content.Context;
import android.content.SharedPreferences;

import com.abbvisor.abbvisor.util.Constants;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class UpdateProfilePresenter implements UpdateProfileContract.UserActionsListener {

    private static final String TAG = UpdateProfilePresenter.class.getName();

    private final Context mContext;

    private final UpdateProfileContract.View mView;

    private final FirebaseUser mUser;

    public UpdateProfilePresenter(Context context,
                                  UpdateProfileContract.View view,
                                  FirebaseUser user) {
        mContext = context;
        mView = view;
        mUser = user;
    }

    @Override
    public void saveProfile(String gender, String age) {
        if (!validateForm(age))
            return;

        SharedPreferences sp = mContext
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(mUser.getUid() + "_gender", gender);
        editor.putString(mUser.getUid() + "_age", age);
        editor.apply();

        boolean isFirstSignin = sp.getBoolean(mUser.getUid() + "_firstSignin", true);
        String prefs = sp.getString(mUser.getUid() + "_prefList", null);

        if (isFirstSignin && prefs == null) {
            mView.showPreferencesActivity();
        }
        else {
            mView.showMapActivity();
        }
    }

    private boolean validateForm(String age) {
        if (age.length() == 0) {
            mView.showAgeRequired();
            return false;
        } else {
            return true;
        }
    }
}
