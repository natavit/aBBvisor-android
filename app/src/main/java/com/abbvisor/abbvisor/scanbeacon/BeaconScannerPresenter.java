package com.abbvisor.abbvisor.scanbeacon;

import android.content.Context;
import android.widget.Toast;

import com.abbvisor.abbvisor.manager.HttpManager;
import com.abbvisor.abbvisor.model.beacon.BeaconContent;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class BeaconScannerPresenter implements BeaconScannerContract.UserActionsListener {

    private static final String TAG = BeaconScannerPresenter.class.getSimpleName();

    private final Context mContext;

    private BeaconScannerContract.View mView;

    public BeaconScannerPresenter(Context context, BeaconScannerContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getBeaconContent(String major, String minor, String id) {
        LOGE(TAG, "major: " + major + " minor: " + minor);
        Observable<BeaconContent> observable
                = HttpManager.getInstance().getService().getBeaconContent(major, minor, id);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BeaconContent>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        LOGE(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(BeaconContent beaconContent) {
//                        LOGE(TAG, "onNext");
                        if (beaconContent != null) {
                            mView.setBeaconTemplate(beaconContent);
                        } else {
                            mView.setBeaconTemplate(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "Error: " + e.getMessage());
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
//                        LOGE(TAG, "onComplete");
                    }
                });

    }
}
