package com.abbvisor.abbvisor.signin;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by natavit on 2/12/2017 AD.
 */

public interface SigninContract {

    interface View {
        void showGoogleSignin();
        void showNextActivity(FirebaseUser user);
        void showProgressDialog();
        void hideProgressDialog();
        void showEmailRequired();
        void showPasswordRequired();
    }

    interface UserActionsListener {
        void signinGoogle();
        void firebaseAuthWithGoogle(GoogleSignInAccount acct);
        void signinFacebook();
        void handleFacebookAccessToken(AccessToken token);
        void createAccount(String email, String password);
        void signin(String email, String password);
        void addAuth();
        void removeAuth();
    }

}
