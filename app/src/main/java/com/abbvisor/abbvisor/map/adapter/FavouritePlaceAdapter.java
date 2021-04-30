package com.abbvisor.abbvisor.map.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.manager.PlaceListManager;
import com.abbvisor.abbvisor.map.MapActivity;
import com.abbvisor.abbvisor.model.place.Result;
import com.google.android.gms.location.places.Place;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

/**
 * Created by natavit on 1/13/2017 AD.
 */

public class FavouritePlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FavouritePlaceAdapter.class.getSimpleName();
    private final Context mContext;

    private MapActivity.PlaceItemListener mItemListener;

    private PlaceListManager mPlaceListManager;

    private List<Result> mPlaces;

    public FavouritePlaceAdapter(Context context,
                                 MapActivity.PlaceItemListener listener,
                                 PlaceListManager placeListManager) {
        mContext = context;
        mItemListener = listener;
        mPlaceListManager = placeListManager;
    }

    public void setFavouritePlaces(List<Result> places) {
        mPlaces = places;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_fav_place, parent, false);

        return new FavouritePlaceViewHolder(view, mItemListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FavouritePlaceViewHolder viewHolder = (FavouritePlaceViewHolder) holder;

        final Result place = mPlaces.get(position);

        viewHolder.tvPlaceName.setText(place.getName());

        if (place.getPhoneNumber() != null && !place.getPhoneNumber().equals("")) {
            viewHolder.tvPlaceContact.setText(String.format(mContext.getString(R.string.text_place_contact),
                    place.getPhoneNumber()));
        } else {
            viewHolder.tvPlaceContact.setText(String.format(mContext.getString(R.string.text_place_contact),
                    "-"));
        }

        viewHolder.starButton.setLiked(true);

        viewHolder.starButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                viewHolder.starButton.setLiked(true);
                mItemListener.onLikeClick(place);
                LOGE(TAG, "Like");
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                viewHolder.starButton.setLiked(false);
                mItemListener.onUnlikeClick(place);
                LOGE(TAG, "Unlike");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPlaces == null || mPlaces.isEmpty())
            return 0;

        return mPlaces.size();
    }

    private Result getItem(int position) {
        return mPlaces.get(position);
    }

    private class FavouritePlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MapActivity.PlaceItemListener mItemListener;

        private RelativeLayout placeInfo;
        private TextView tvPlaceName;
        private TextView tvPlaceContact;
        private LikeButton starButton;

        private FavouritePlaceViewHolder(View itemView, MapActivity.PlaceItemListener listener) {
            super(itemView);

            mItemListener = listener;

            placeInfo = (RelativeLayout) itemView.findViewById(R.id.place_info);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tv_place_name);
            tvPlaceContact = (TextView) itemView.findViewById(R.id.tv_place_contact);
            starButton = (LikeButton) itemView.findViewById(R.id.star_button);

            placeInfo.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Place place = getItem(position);
            mItemListener.onPlaceClick(place);
        }
    }


}
