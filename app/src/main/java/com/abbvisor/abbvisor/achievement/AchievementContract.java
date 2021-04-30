package com.abbvisor.abbvisor.achievement;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface AchievementContract {

    interface View {
        void refreshAchievements();
    }

    interface UserActionsListener {
        void getAchievements(String id);
    }
}
