package com.abbvisor.abbvisor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.abbvisor.abbvisor.selectcharacteristic.CharacteristicFragment;
import com.abbvisor.abbvisor.util.Constants;
import com.abbvisor.abbvisor.util.DeviceUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

public class AccountManager {

    private static final String TAG = AccountManager.class.getSimpleName();

    private static final String PROVIDER_PASSWORD = "password";
    private static final String PROVIDER_FACEBOOK = "facebook.com";
    private static final String PROVIDER_GOOGLE = "google.com";

    private static AccountManager mInstance;

    private static Context mContext;

    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String age;
    private String provider;
    private String socialProvider;

    private String prefs;

    private boolean openedFromNoti;

    private AccountManager() {
    }

    public static AccountManager getInstance(Context context) {
        mContext = context;
        if (mInstance == null)
            mInstance = new AccountManager();
        return mInstance;
    }

    public void setProfile(FirebaseUser user, String s) {
        id = user.getUid();
        provider = "firebase";

        SharedPreferences sp = mContext
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        gender = sp.getString(id + "_gender", "male");
        age = sp.getString(id + "_age", "0");
        prefs = sp.getString(id + "_prefList", "");
        name = sp.getString(id + "_name", "");
//        email = sp.getString(id + "_email", "");

        switch (s) {
            case PROVIDER_GOOGLE:
                socialProvider = PROVIDER_GOOGLE;
                name = user.getDisplayName();
                firstName = name.substring(0, name.indexOf(" "));
                lastName = name.substring(name.indexOf(" ") + 1);
                editor.putString(id+"_name", name);
                editor.apply();
                track();
                break;
            case PROVIDER_FACEBOOK:
                socialProvider = PROVIDER_FACEBOOK;
                name = user.getDisplayName();
                callGraphAPIRequest();
                break;
            default:
                socialProvider = PROVIDER_PASSWORD;
                email = user.getEmail();
                String n = user.getDisplayName();
                if (n != null) {
                    firstName = n.substring(0, n.indexOf(" "));
                    lastName = n.substring(n.indexOf(" ") + 1);
                } else {
                    name = email;
                    firstName = "N/A";
                    lastName = "N/A";
                }
                editor.putString(id + "_name", name);
                editor.apply();
                track();
                break;
        }
    }

    public void setGoogleEmail(String email) {
        this.email = email;
    }

    private void callGraphAPIRequest() {
        if (!DeviceUtils.isInternetConnectionAvailable(mContext)) {
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
//                            id = object.getString("id");
                            name = object.getString("name");
                            firstName = object.getString("first_name");
                            lastName = object.getString("last_name");
                            email = object.getString("email");
//                            gender = object.getString("gender");
//                            age = object.getJSONObject("age_range").getString("min");

                            SharedPreferences sp = mContext
                                    .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(id+"_name", name);
                            editor.putString(id+"_firstName", firstName);
                            editor.putString(id+"_lastName", lastName);
                            editor.putString(id+"_email", email);
                            editor.apply();

                            track();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,name,age_range,gender,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void track() {
        if (!DeviceUtils.isInternetConnectionAvailable(mContext))
            return;

        String json = "{\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"customerId\":{\"value\":\"" + id + "\",\"idType\":\"" + provider + "\"}," +
                "\"gender\":\"" + gender + "\"," +
                "\"age\":\"" + age + "\"}";

        LOGE(TAG, "Track: " + json);

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), json);

        Observable<ResponseBody> observable = HttpManager.getInstance().getService().trackUser(body);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        LOGE(TAG, "onNext: track successfully");
                        savePreferencesToServer(CharacteristicFragment.FLAG_NEW_ACTIVITY);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "track - onError: " + e.getLocalizedMessage());
//                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signOut(GoogleApiClient api) {
        if (socialProvider.equals(PROVIDER_GOOGLE)) {
            Auth.GoogleSignInApi.signOut(api).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess())
                        FirebaseAuth.getInstance().signOut();
                }
            });
        } else {
            FirebaseAuth.getInstance().signOut();
        }
        openedFromNoti = false;
    }

    public void savePreferences(String prefs, int flag) {
        this.prefs = prefs;
        SharedPreferences sp = mContext
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(id + "_prefList", prefs);
        editor.apply();
        savePreferencesToServer(flag);
    }

    public void savePreferencesToServer(final int flag) {
        if (!DeviceUtils.isInternetConnectionAvailable(mContext))
            return;

        String json = "{\"customerId\":\"" + id + "\","
                + "\"preferences\":" + prefs + "}";

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), json);
        Observable<ResponseBody> observable
                = HttpManager.getInstance().getService().savePreferences(body);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            LOGE(TAG, value.string());
                            if (flag == CharacteristicFragment.FLAG_UPDATE_CURRENT_ACTIVITY) {
                                Toast.makeText(mContext, "Saved preferences successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGE(TAG, "savePreferencesToServer - onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
//                        LOGE(TAG, "Saved preferences successfully");
                    }
                });

    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public boolean isOpenedFromNoti() {
        return openedFromNoti;
    }

    public void setOpenedFromNoti(boolean openedFromNoti) {
        this.openedFromNoti = openedFromNoti;
    }
}
