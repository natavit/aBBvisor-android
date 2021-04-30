package com.abbvisor.abbvisor.selectcharacteristic.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.selectcharacteristic.CharacteristicFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 1/22/2017 AD.
 */

public class CharacteristicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CharacteristicAdapter.class.getName();

    private final String[] PICTURES =
            {"altruistic.jpeg", "artsy.jpeg", "celebratory.jpeg", "chill.jpeg", "classic.jpeg",
                "competitive.jpeg", "curious.jpeg", "daydreaming.jpeg", "extreme.jpeg",
            "offbeat.jpeg", "post.jpeg", "scholarly.jpeg"};

    private Context mContext;

    private List<String> mPrefs;
    private List<String> mSelectedPrefs;

    private CharacteristicFragment.PrefItemListener mItemListener;

    public CharacteristicAdapter(Context context,
                                 List<String> prefs,
                                 List<String> selectedPrefs,
                                 CharacteristicFragment.PrefItemListener listener) {
        mContext = context;
        mPrefs = prefs;
        mSelectedPrefs = selectedPrefs;
        mItemListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_preferences, parent, false);
        return new PreferencesViewHolder(view, mItemListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PreferencesViewHolder viewHolder = (PreferencesViewHolder) holder;

        String item = mPrefs.get(position);
        viewHolder.tvPrefName.setText(
                String.format(mContext.getString(R.string.text_personality_type), item));

        if (mSelectedPrefs != null && mSelectedPrefs.contains(item)) {
            viewHolder.selected = true;
//            viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.theme_accent_2));
//            viewHolder.overlay.setVisibility(View.INVISIBLE);
            viewHolder.overlay.setAlpha(0f);
            viewHolder.tvPrefName.setAlpha(0f);
        }

//        String[] images = item.split(" ");

        Glide.with(mContext)
//                .load(Uri.parse("file:///android_asset/images/" + PICTURES[position % 7]))
//                .load(Uri.parse("file:///android_asset/images/" + images[0].toLowerCase() + ".jpg"))
                .load(Uri.parse("file:///android_asset/images/" + item.toLowerCase() + ".jpeg"))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivPref);
    }

    @Override
    public int getItemCount() {
        return mPrefs.size();
    }

    private String getItem(int position) {
        return mPrefs.get(position);
    }

    private class PreferencesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CharacteristicFragment.PrefItemListener mItemListener;

        private CardView cardView;
        private TextView tvPrefName;
        private ImageView ivPref;
        private View overlay;

        private boolean selected;

        private PreferencesViewHolder(View itemView, CharacteristicFragment.PrefItemListener listener) {
            super(itemView);
            mItemListener = listener;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvPrefName = (TextView) itemView.findViewById(R.id.tv_pref_name);
            ivPref = (ImageView) itemView.findViewById(R.id.iv_pref);
            overlay = itemView.findViewById(R.id.overlay);

            selected = false;

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            String pref = getItem(position);
            mItemListener.onPrefClick(pref);

            if (selected) {
                selected = false;
//                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.card));
                ObjectAnimator anim = ObjectAnimator.ofFloat(overlay, View.ALPHA, 1f);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tvPrefName, View.ALPHA, 1f);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.setDuration(300);
                animatorSet.playTogether(anim, anim2);
                animatorSet.start();

            } else {
                selected = true;

                ObjectAnimator anim = ObjectAnimator.ofFloat(overlay, View.ALPHA, 0f);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tvPrefName, View.ALPHA, 0f);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.setDuration(300);
                animatorSet.playTogether(anim, anim2);
                animatorSet.start();

//                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.theme_accent_2));
            }
        }

    }
}
