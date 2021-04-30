package com.abbvisor.abbvisor.settings.view;

import android.content.Context;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;

/**
 * Created by natavit on 4/4/2017 AD.
 */

public class AboutPreference extends Preference {
    public AboutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ((TextView) view.findViewById(android.R.id.title))
                .setTextColor(ContextCompat.getColor(view.getContext(), R.color.theme_text_secondary_dark));
    }
}
