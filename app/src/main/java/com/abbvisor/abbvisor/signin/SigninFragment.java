package com.abbvisor.abbvisor.signin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.databinding.FragmentSigninBinding;
import com.abbvisor.abbvisor.manager.AccountManager;
import com.abbvisor.abbvisor.manager.HttpManager;
import com.abbvisor.abbvisor.map.MapActivity;
import com.abbvisor.abbvisor.model.user.UserProfile;
import com.abbvisor.abbvisor.selectcharacteristic.CharacteristicActivity;
import com.abbvisor.abbvisor.updateprofile.UpdateProfileActivity;
import com.abbvisor.abbvisor.util.Constants;
import com.abbvisor.abbvisor.util.DeviceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

public class SigninFragment extends Fragment implements
        SigninContract.View,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SigninFragment.class.getSimpleName();

    private static final int GOOGLE_SIGN_IN = 9001;

    private SigninContract.UserActionsListener mActionsListener;

    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private AccessTokenTracker mAccessTokenTracker;

    private GoogleApiClient mGoogleApiClient;

    FragmentSigninBinding binding;

    public SigninFragment() {
        super();
    }

    public static SigninFragment newInstance() {
        return new SigninFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signin, container, false);
        View rootView = binding.getRoot();
        initInstances(savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
    }

    private void initInstances(Bundle savedInstanceState) {

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                Profile.setCurrentProfile(currentProfile);
                                mProfileTracker.stopTracking();
                            }
                        };

                        mAccessTokenTracker = new AccessTokenTracker() {
                            @Override
                            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                                AccessToken.setCurrentAccessToken(currentAccessToken);
                                mAccessTokenTracker.stopTracking();
                            }
                        };

                        mActionsListener.handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        binding.welcomeImage.setPageCount(WELCOME_IMAGES_NUMBER);
//        binding.welcomeImage.setImageListener(new ImageListener() {
//            @Override
//            public void setImageForPosition(int position, ImageView imageView) {
//                Glide.with(getActivity())
//                        .load(WELCOME_IMAGES[position])
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(imageView);
//            }
//        });

        Glide.with(getActivity())
                .load(Uri.parse("file:///android_asset/images/welcome_image.jpg"))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.welcomeImage);

        binding.btnSignin.setCustomTextFont("Aller-Regular.ttf");
        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetConnectionAvailable())
                    return;
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                mActionsListener.signin(email, password);
            }
        });

        binding.btnRegister.setCustomTextFont("Aller-Regular.ttf");
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetConnectionAvailable())
                    return;
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                mActionsListener.createAccount(email, password);
            }
        });

        binding.btnFacebook.setCustomTextFont("Aller-Regular.ttf");
        binding.btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetConnectionAvailable())
                    return;
                mActionsListener.signinFacebook();
            }
        });

        binding.btnGoogle.setCustomTextFont("Aller-Regular.ttf");
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetConnectionAvailable())
                    return;
                mActionsListener.signinGoogle();
            }
        });

        mActionsListener = new SigninPresenter(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mActionsListener.addAuth();
    }

    @Override
    public void onResume() {
        super.onResume();
//        showNextActivity(AccessToken.getCurrentAccessToken());
    }

    @Override
    public void onStop() {
        super.onStop();
        mActionsListener.removeAuth();
        hideProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                LOGE(TAG, "Google Sign In Passed");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                LOGE(TAG, "Email: " + account.getEmail());
                AccountManager.getInstance(getContext()).setGoogleEmail(account.getEmail());
                mActionsListener.firebaseAuthWithGoogle(account);
            } else {
                LOGE(TAG, "Google Sign In Failed: " + result.getStatus().getStatusMessage());
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showGoogleSignin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void showNextActivity(final FirebaseUser firebaseUser) {
        final SharedPreferences sp = getActivity()
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        boolean firstLogin = sp.getBoolean(firebaseUser.getUid() + "_firstSignin", true);
        if (firstLogin) {
            Observable<UserProfile> observable
                    = HttpManager.getInstance().getService().getUserProfile(firebaseUser.getUid());

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UserProfile>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(UserProfile userProfile) {
                            LOGE(TAG, "pass");
                            if (userProfile != null && userProfile.getId() != null && !userProfile.getId().isEmpty()) {
                                SharedPreferences.Editor editor = sp.edit();
                                String gender = userProfile.getGender();
                                String age = userProfile.getAge();
                                String prefs = new Gson().toJson(userProfile.getPreferences());
                                LOGE(TAG, "Prefs: " + prefs);
                                String name;
                                if (userProfile.getFirstName() != null && !userProfile.getFirstName().isEmpty()) {
                                    name = userProfile.getFirstName() + " " + userProfile.getLastName();
                                } else {
                                    name = userProfile.getEmail();
                                }
                                String email = userProfile.getEmail();

                                String id = userProfile.getUserID().getId();
                                editor.putString(id + "_gender", gender);
                                editor.putString(id + "_age", age);

                                editor.putString(id + "_prefList", prefs);
                                editor.putString(id + "_name", name);
                                editor.putString(id + "_email", email);
                                editor.putString(id + "_loginType", firebaseUser.getProviders().get(0));
                                editor.putBoolean(id + "_firstSignin", false);
                                editor.apply();

                                Intent i = new Intent(getActivity(), MapActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            } else {
                                LOGE(TAG, "no data");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LOGE(TAG, "error: " + e.getMessage());
                            String id = firebaseUser.getUid();
                            String gender = sp.getString(id + "_gender", null);
                            String prefs = sp.getString(id + "_prefList", null);

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(id + "_loginType", firebaseUser.getProviders().get(0));
//                        editor.putBoolean(firebaseUser.getUid() + "_firstSignin", true);
                            editor.apply();
                            if (gender != null && prefs == null) {
                                Intent i = new Intent(getActivity(), CharacteristicActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            } else {
                                Intent i = new Intent(getActivity(), UpdateProfileActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Intent i = new Intent(getActivity(), MapActivity.class);
            startActivity(i);
            getActivity().finish();
        }

    }

    private void loadUserProfile() {
    }

    private synchronized boolean isUserNotNull() {
        final boolean[] userNotNull = new boolean[1];
        userNotNull[0] = false;


        return userNotNull[0];
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.text_loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showEmailRequired() {
        binding.etEmail.setError("Email is required.");
    }

    @Override
    public void showPasswordRequired() {
        binding.etPassword.setError("Password is required.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private boolean isInternetConnectionAvailable() {
        if (!DeviceUtils.isInternetConnectionAvailable(getContext())) {
            Toast.makeText(getContext(), "No Internet Connection...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
