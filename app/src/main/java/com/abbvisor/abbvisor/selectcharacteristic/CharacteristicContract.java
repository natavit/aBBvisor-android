package com.abbvisor.abbvisor.selectcharacteristic;

import java.util.List;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface CharacteristicContract {

    interface View {
        void showMapActivity(int flag);
    }

    interface UserActionsListener {
        void saveSelectedPreferences(List<String> selectedPrefs);
    }
}
