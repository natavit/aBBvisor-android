package com.abbvisor.abbvisor.scanbeacon;

import com.abbvisor.abbvisor.model.beacon.BeaconContent;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface BeaconScannerContract {

    interface View {
        void setBeaconTemplate(BeaconContent beaconContent);
        void setViewTemplate1(BeaconContent beaconContent);
        void setViewTemplate2(BeaconContent beaconContent);
        void setViewTemplate3(BeaconContent beaconContent);
        void setViewTemplate4(BeaconContent beaconContent);
        void setBeaconActivity(BeaconContent beaconContent);
    }

    interface UserActionsListener {
        void getBeaconContent(String uuid, String major, String minor);
    }
}
