package com.abbvisor.abbvisor.achievement.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.achievement.adapter.viewholder.AchievementDetailViewHolder;
import com.abbvisor.abbvisor.model.achievement.Achievement;
import com.abbvisor.abbvisor.model.achievement.Result;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * Created by natavit on 4/7/2017 AD.
 */

public class AchievementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = AchievementAdapter.class.getName();

    public static final int[] COLORS = {
            rgb("#FF9F1C"), rgb("#F0F0F0")
    };

    private FragmentActivity mActivity;

    private Achievement mAchievement;

    public AchievementAdapter(FragmentActivity activity) {
        mActivity = activity;
    }

    public void setAchievement(Achievement achievement) {
        mAchievement = achievement;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_achievement, parent, false);
        return new AchievementDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AchievementDetailViewHolder viewHolder = (AchievementDetailViewHolder) holder;
        setupDetail(viewHolder, position);
    }

    private void setupDetail(AchievementDetailViewHolder viewHolder, int position) {
        final Result item = mAchievement.getResults().get(position);
        viewHolder.tvAchievement.setText(item.getName());

        ArrayList<PieEntry> entries = new ArrayList<>();
        int count = item.getCount() >= item.getMax() ? item.getMax() : item.getCount();
        int max = count >= item.getMax() ? 0 : item.getMax() - count;

        entries.add(new PieEntry(count));

        if (max > 0)
            entries.add(new PieEntry(max));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(COLORS);
        dataSet.setValueTextSize(13f);
        dataSet.setValueFormatter(new IntegerValueFormatter());

        PieData data = new PieData(dataSet);

        Legend legend = viewHolder.pieChart.getLegend();
        legend.setEnabled(false);

        viewHolder.pieChart.setData(data);
        Description description = new Description();
        description.setText("");
        viewHolder.pieChart.setDescription(description);
        viewHolder.pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
        viewHolder.pieChart.setCenterText(count + "/" + item.getMax());
        viewHolder.pieChart.setCenterTextSize(12f);
        viewHolder.pieChart.animateXY(800, 500);
        viewHolder.pieChart.setTouchEnabled(false);
        viewHolder.pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDialog(item);
            }
        });
        viewHolder.pieChart.invalidate();

        viewHolder.cvAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDialog(item);
            }
        });
    }

    private void setupDialog(Result item) {
        String detail = item.getAchievementDetail() == null || item.getAchievementDetail().isEmpty()
            ? "No detail available yet." : item.getAchievementDetail();
        final Dialog dialog = new Dialog(mActivity);
        dialog.setTitle("Detail");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.alert_dialog_achievement);
        dialog.setCancelable(true);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        tvTitle.setText(item.getName());

        TextView tvDetail = (TextView) dialog.findViewById(R.id.tv_detail);
        tvDetail.setText(detail);


        TextView tvOK = (TextView) dialog.findViewById(R.id.tv_ok);
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        if (mAchievement == null || mAchievement.getResults() == null)
            return 0;

        return mAchievement.getResults().size();
    }

    private class IntegerValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        IntegerValueFormatter() {
            mFormat = new DecimalFormat("###,###,###"); // use no decimals
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}