package com.abbvisor.abbvisor.scanbeacon.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.customtabs.CustomTabActivityHelper;
import com.abbvisor.abbvisor.customtabs.config.LinkTransformationMethod;
import com.abbvisor.abbvisor.model.beacon.BeaconActivity;
import com.abbvisor.abbvisor.model.beacon.BeaconContent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by natavit on 1/13/2017 AD.
 */

public class BeaconActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BeaconActivityAdapter.class.getSimpleName();
    private static final String MONTHS[] = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    private final FragmentActivity mActivity;
    private BeaconContent mBeaconContent;
    private CustomTabActivityHelper mCustomTabActivityHelper;

    public BeaconActivityAdapter(FragmentActivity activity, CustomTabActivityHelper customTabActivityHelper) {
        mActivity = activity;
        mCustomTabActivityHelper = customTabActivityHelper;
    }

    public void setBeaconContent(BeaconContent beaconContent) {
        this.mBeaconContent = beaconContent;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_beacon_activity, parent, false);

        return new BeaconActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BeaconActivityViewHolder viewHolder = (BeaconActivityViewHolder) holder;

        setupActivity(viewHolder, position);
    }

    private void setupActivity(BeaconActivityViewHolder viewHolder, int position) {
        final BeaconActivity beaconActivity = mBeaconContent.getActivities().get(position);

        if (position > 0) {
            viewHolder.spaceTop.setVisibility(View.GONE);
        }

        if (beaconActivity.getImage() == null) {
            viewHolder.ivActivity.setVisibility(View.GONE);
        } else {
            Glide.with(mActivity)
                    .load(beaconActivity.getImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivActivity);
        }

        if (beaconActivity.getTitle() == null) {
            viewHolder.tvActivityName.setVisibility(View.GONE);
        } else {
            viewHolder.tvActivityName.setText(beaconActivity.getTitle());
        }

        if (beaconActivity.isDayloop()) {
            if (beaconActivity.getDays().isEmpty()) {
                viewHolder.tvActivityDate.setVisibility(View.GONE);
            } else {
                String day = "";
                for (String d : beaconActivity.getDays()) {
                    day += d + ", ";
                }
                day = day.substring(0, day.length() - 2);

                viewHolder.tvActivityDate.setText(String.format(mActivity.getString(R.string.text_activity_day), day));
            }
        } else {
            if (beaconActivity.getDateStart() == null || beaconActivity.getDateEnd() == null) {
                viewHolder.tvActivityDate.setVisibility(View.GONE);
            } else {
                String start = beaconActivity.getDateStart().substring(0, beaconActivity.getDateStart().indexOf("T"));
                String end = beaconActivity.getDateEnd().substring(0, beaconActivity.getDateEnd().indexOf("T"));
                viewHolder.tvActivityDate.setText(
                        String.format(mActivity.getString(R.string.text_activity_date),
                                dateToText(start), dateToText(end)
                        ));
            }
        }

        if (beaconActivity.getOpentime() == null && beaconActivity.getClosetime() == null) {
            viewHolder.tvActivityTime.setVisibility(View.GONE);
        } else {
            viewHolder.tvActivityTime.setText(
                    String.format(mActivity.getString(R.string.text_beacon_opening_time),
                            beaconActivity.getOpentime(), beaconActivity.getClosetime()
                    )
            );
        }

        if (beaconActivity.getDescription() == null) {
            viewHolder.tvActivityDescription.setVisibility(View.GONE);
        } else {
            viewHolder.tvActivityDescription.setText(beaconActivity.getDescription());
            viewHolder.tvActivityDescription.setTransformationMethod(
                    new LinkTransformationMethod(mActivity, mCustomTabActivityHelper));
        }
    }

    @Override
    public int getItemCount() {
        if (mBeaconContent == null || mBeaconContent.getActivities() == null || mBeaconContent.getActivities().isEmpty())
            return 0;

        return mBeaconContent.getActivities().size();
    }

    private BeaconActivity getItem(int position) {
        return mBeaconContent.getActivities().get(position);
    }

    private String dateToText(String str) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        int year = 0, month = 0, day = 0;
        try {
            Date date = df.parse(str);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            day = c.get(Calendar.DATE);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("%s %s %s", day, MONTHS[month], year);
    }


    private class BeaconActivityViewHolder extends RecyclerView.ViewHolder {

        private Space spaceTop;
        private Space spaceBottom;
        private ImageView ivActivity;
        private TextView tvActivityName;
        private TextView tvActivityDate;
        private TextView tvActivityTime;
        private TextView tvActivityDescription;

        private BeaconActivityViewHolder(View itemView) {
            super(itemView);

            spaceTop = (Space) itemView.findViewById(R.id.space_top);
            spaceBottom = (Space) itemView.findViewById(R.id.space_bottom);
            ivActivity = (ImageView) itemView.findViewById(R.id.iv_activity);
            tvActivityName = (TextView) itemView.findViewById(R.id.tv_activity_name);
            tvActivityDate = (TextView) itemView.findViewById(R.id.tv_activity_date);
            tvActivityTime = (TextView) itemView.findViewById(R.id.tv_activity_time);
            tvActivityDescription = (TextView) itemView.findViewById(R.id.tv_activity_description);

        }
    }


}
