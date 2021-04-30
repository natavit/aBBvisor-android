package com.abbvisor.abbvisor.customtabs.config;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.TransformationMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.customtabs.CustomTabActivityHelper;
import com.abbvisor.abbvisor.customtabs.WebviewFallback;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 4/15/2017 AD.
 */

//ref: https://medium.com/@nullthemall/make-textview-open-links-in-customtabs-12fdcf4bb684
public class LinkTransformationMethod implements TransformationMethod {

    private static final String TAG = LinkTransformationMethod.class.getSimpleName();

    private final CustomTabActivityHelper mCustomTabActivityHelper;
    private final FragmentActivity mActivity;

    public LinkTransformationMethod(FragmentActivity activity, CustomTabActivityHelper customTabActivityHelper) {
        mActivity = activity;
        mCustomTabActivityHelper = customTabActivityHelper;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            Linkify.addLinks(textView, Linkify.WEB_URLS);
            if (textView.getText() == null || !(textView.getText() instanceof Spannable)) {
                return source;
            }
            Spannable text = (Spannable) textView.getText();
            URLSpan[] spans = text.getSpans(0, textView.length(), URLSpan.class);
            for (int i = spans.length - 1; i >= 0; i--) {
                URLSpan oldSpan = spans[i];
                int start = text.getSpanStart(oldSpan);
                int end = text.getSpanEnd(oldSpan);
                String url = oldSpan.getURL();
                text.removeSpan(oldSpan);
                text.setSpan(new CustomTabsURLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return text;
        }
        return source;
    }

    @Override
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

    }

    private class CustomTabsURLSpan extends URLSpan {

        CustomTabsURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            String url = getURL();

            widget.invalidate();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            boolean openExternally = sp.getBoolean(mActivity.getString(R.string.pref_key_general_links_open_externally), false);

            LOGE(TAG, "Open web");

            if (!openExternally) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(mActivity, R.color.theme_primary));
                builder.setShowTitle(true);
                builder.addDefaultShareMenuItem();
                builder.setStartAnimations(mActivity, R.anim.slide_in_right, R.anim.slide_out_left);
                builder.setExitAnimations(mActivity, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                CustomTabActivityHelper.openCustomTab(mActivity, builder.build(), Uri.parse(url), new WebviewFallback());
            } else {
                super.onClick(widget);
            }
        }
    }
}
