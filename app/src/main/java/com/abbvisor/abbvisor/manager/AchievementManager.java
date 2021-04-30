package com.abbvisor.abbvisor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.abbvisor.abbvisor.model.achievement.Achievement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

/**
 * Created by natavit on 4/4/2017 AD.
 */

public class AchievementManager {
    private static final String TAG = AchievementManager.class.getSimpleName();

    private Context mContext;

    private Achievement mAchievement;

    public AchievementManager(Context context) {
        mContext = context;
        loadCache();
    }

    public void setAchievement(Achievement achievement) {
        mAchievement = achievement;
        if (mAchievement != null) {
            // Save to Persistent Storage
            saveCache();
        }
    }

    public Achievement getAchievement() {
        return mAchievement;
    }

    public int getCount() {
        if (mAchievement == null || mAchievement.getResults() == null) return 0;
        return mAchievement.getResults().size();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable("achievement_dao", mAchievement);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mAchievement = savedInstanceState.getParcelable("achievement_dao");
    }

    private void saveCache() {
        Achievement cacheDao = new Achievement();

        if (mAchievement != null && mAchievement.getResults() != null)
            cacheDao.setResults(mAchievement.getResults());

        // Convert object to json
        String json = new Gson().toJson(cacheDao);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences prefs = mContext.getSharedPreferences(uid+"achievements",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("json", json);
        editor.apply();
    }

    private void loadCache() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences prefs = mContext.getSharedPreferences(uid+"achievements",
                Context.MODE_PRIVATE);
        String json = prefs.getString("json", null);
        if (json == null)
            return;

        mAchievement = new Gson().fromJson(json, Achievement.class);
    }
}
