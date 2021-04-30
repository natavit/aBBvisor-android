package com.abbvisor.abbvisor.signin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.abbvisor.abbvisor.util.Constants;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public class SigninPresenter implements SigninContract.UserActionsListener {

    private static final String TAG = SigninPresenter.class.getSimpleName();

    private final Fragment mFragment;

    private final SigninContract.View mView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    public SigninPresenter(Fragment fragment, SigninContract.View view) {
        SharedPreferences sp = fragment.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        mFragment = fragment;
        mView = view;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mFragment.getContext());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    LOGE(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    String provider = user.getProviders().get(0);
                    if (provider.equals("password") && !user.isEmailVerified()) {
                        Toast.makeText(mFragment.getContext(), "Verification email was sent, please verify your email",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mView.showNextActivity(user);
                        trackSignin(user);
                    }

                    mFirebaseAnalytics.setUserId(user.getUid());


                    editor.putBoolean("signed_in", true);

                } else {
                    // User is signed out
                    LOGE(TAG, "onAuthStateChanged:signed_out");

                    editor.putBoolean("signed_in", false);
                }

                editor.apply();
            }
        };
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(mFragment.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LOGE(TAG, "Email sent.");
                        }
                    }
                });
    }

    @Override
    public void signinGoogle() {
        mView.showGoogleSignin();
    }

    @Override
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mView.showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mFragment.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleSigninTask(task);
                    }
                });
    }

    @Override
    public void signinFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                mFragment,
                Arrays.asList("public_profile", "email")
        );
    }

    @Override
    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mFragment.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleSigninTask(task);
                    }
                });
    }

    @Override
    public void createAccount(String email, String password) {
        if (!validateForm(email, password))
            return;

        mView.showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mFragment.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleSigninTask(task);

                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            sendEmailVerification(user);
                            trackSignup(user);
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    @Override
    public void signin(String email, String password) {
        if (!validateForm(email, password))
            return;

        mView.showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mFragment.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleSigninTask(task);
                    }
                });
    }

    @Override
    public void addAuth() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void removeAuth() {
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }


    private void handleSigninTask(Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            LOGE(TAG, "signInWithCredential", task.getException());
            Toast.makeText(mFragment.getContext(), task.getException().getLocalizedMessage(), //R.string.auth_failed,
                    Toast.LENGTH_SHORT).show();
        }
        mView.hideProgressDialog();
    }

    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            mView.showEmailRequired();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            mView.showPasswordRequired();
            return false;
        } else {
            return true;
        }
    }

    private void trackSignin(FirebaseUser user) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, user.getUid());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    private void trackSignup(FirebaseUser user) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, user.getProviders().get(0));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }
}
