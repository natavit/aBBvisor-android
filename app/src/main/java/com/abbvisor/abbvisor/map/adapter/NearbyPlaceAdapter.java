package com.abbvisor.abbvisor.map.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.manager.PlaceListManager;
import com.abbvisor.abbvisor.map.MapActivity;
import com.abbvisor.abbvisor.model.place.Result;
import com.abbvisor.abbvisor.model.place.distancematrix.Element;
import com.abbvisor.abbvisor.util.PlaceFilters2;
import com.google.android.gms.location.places.Place;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natavit on 1/13/2017 AD.
 */

public class NearbyPlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = NearbyPlaceAdapter.class.getSimpleName();
    private final Context mContext;

    private MapActivity.PlaceItemListener mItemListener;

    private PlaceListManager mPlaceListManager;

    private List<Result> mPlaces;
    private List<Element> mElement;

    private FavouritePlaceAdapter mFavouritePlaceAdapter;

    public NearbyPlaceAdapter(Context context,
                              MapActivity.PlaceItemListener listener,
                              PlaceListManager placeListManager,
                              FavouritePlaceAdapter favouritePlaceAdapter) {
        mContext = context;
        mItemListener = listener;
        mPlaceListManager = placeListManager;
        mFavouritePlaceAdapter = favouritePlaceAdapter;
    }

    public void appendNearbyPlacesAtBottomPosition(Result place, Element element) {
        if (filter(place)) {
            if (mPlaces == null) {
                mPlaces = new ArrayList<>();
            }

            if (mElement == null) {
                mElement = new ArrayList<>();
            }

            int pos = getItemCount();
            if (pos > 0) {

                boolean isAdded = false;

                for (int i=0; i<mElement.size(); i++) {
                    if (element.getDuration().getValue() < mElement.get(i).getDuration().getValue()) {
                        isAdded = true;
                        mPlaces.add(i, place);
                        mElement.add(i, element);
                        break;
                    }
                }
                if (!isAdded) {
                    mPlaces.add(place);
                    mElement.add(element);
                }

            }
            else {
                mPlaces.add(place);
                mElement.add(element);
            }

            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_nearby_place, parent, false);

        return new NearbyPlaceViewHolder(view, mItemListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NearbyPlaceViewHolder viewHolder = (NearbyPlaceViewHolder) holder;

        final Result place = mPlaces.get(position);

        viewHolder.tvPlaceName.setText(place.getName());
        viewHolder.tvPlaceAddress.setText(place.getAddress());

        if (place.getPhoneNumber() != null && !place.getPhoneNumber().equals("")) {
            viewHolder.tvPlaceContact.setText(String.format(mContext.getString(R.string.text_place_contact),
                    place.getPhoneNumber()));
        } else {
            viewHolder.tvPlaceContact.setText(String.format(mContext.getString(R.string.text_place_contact),
                    "-"));
        }

        viewHolder.ratingBar.setRating(place.getRating());

        viewHolder.tvPlaceDuration.setText(mElement.get(position).getDuration().getText());
        viewHolder.tvPlaceDistance.setText(mElement.get(position).getDistance().getText());

        if (mPlaceListManager.isFavouritePlace(place)) {
            viewHolder.starButton.setLiked(true);
        } else {
            viewHolder.starButton.setLiked(false);
        }

        viewHolder.starButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                viewHolder.starButton.setLiked(true);
                mItemListener.onLikeClick(place);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                viewHolder.starButton.setLiked(false);
                mItemListener.onUnlikeClick(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPlaces == null || mPlaces.isEmpty())
            return 0;

        return mPlaces.size();
    }

    private Place getItem(int position) {
        return mPlaces.get(position);
    }

    private boolean filter(Result place) {

        for (int type : place.getPlaceTypes()) {
            if (PlaceFilters2.getValues().contains(type))
                return true;
        }

        return false;
    }

//    private List<Place> filter(List<Place> places) {
////        LOGE(TAG, "Size: " + placeList.getResults().size());
//
//        List<Place> results = new ArrayList<>();
//
//        for (Place place : places) {
//            for (Integer type : place.getPlaceTypes()) {
//                if (mFilterList.contains(type)) {
//                    results.add(place);
//                    break;
//                }
//            }
//        }
//
//        return results;
//    }

    private class NearbyPlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MapActivity.PlaceItemListener mItemListener;

        private RelativeLayout placeInfo;
        private TextView tvPlaceName;
        private TextView tvPlaceDistance;
        private TextView tvPlaceAddress;
        private TextView tvPlaceContact;
        private TextView tvPlaceDuration;
        private RatingBar ratingBar;
        private LikeButton starButton;

        private NearbyPlaceViewHolder(View itemView, MapActivity.PlaceItemListener listener) {
            super(itemView);

            mItemListener = listener;

            placeInfo = (RelativeLayout) itemView.findViewById(R.id.place_info);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tv_place_name);
            tvPlaceDistance = (TextView) itemView.findViewById(R.id.tv_place_distance);
            tvPlaceAddress = (TextView) itemView.findViewById(R.id.tv_place_address);
            tvPlaceContact = (TextView) itemView.findViewById(R.id.tv_place_contact);
            tvPlaceDuration = (TextView) itemView.findViewById(R.id.tv_place_duration);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
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
