package com.abbvisor.abbvisor.updateprofile;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface UpdateProfileContract {

    interface View {
        void showMapActivity();
        void showPreferencesActivity();
        void showAgeRequired();
    }

    interface UserActionsListener {
        void saveProfile(String gender, String age);
    }

}
