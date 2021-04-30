package com.abbvisor.abbvisor.achievement;

import android.support.v4.app.Fragment;

import com.abbvisor.abbvisor.manager.AchievementManager;
import com.abbvisor.abbvisor.manager.HttpManager;
import com.abbvisor.abbvisor.model.achievement.Achievement;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;
import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class AchievementPresenter implements AchievementContract.UserActionsListener {

    private final Fragment mFragment;

    private final AchievementContract.View mView;

    private AchievementManager mAchievementManager;

    public AchievementPresenter(Fragment fragment,
                                AchievementContract.View view,
                                AchievementManager achievementManager) {
        mFragment = fragment;
        mView = view;
        mAchievementManager = achievementManager;
    }

    @Override
    public void getAchievements(String id) {
        Observable<Achievement> observable
                = HttpManager.getInstance().getService().getAchievements(id); //MockUpTest

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Achievement>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Achievement achievement) {
                        if (achievement != null) {
                            LOGE(TAG, "onNext: Loaded achievements successfully");
                            mAchievementManager.setAchievement(achievement);
                            mView.refreshAchievements();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
