package com.abbvisor.abbvisor.achievement.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.github.mikephil.charting.charts.PieChart;

/**
 * Created by natavit on 4/13/2017 AD.
 */

public class AchievementDetailViewHolder extends RecyclerView.ViewHolder {

    public CardView cvAchievement;
    public TextView tvAchievement;
    public PieChart pieChart;

    public AchievementDetailViewHolder(View itemView) {
        super(itemView);
        cvAchievement = (CardView) itemView.findViewById(R.id.cv_achievement);
        tvAchievement = (TextView) itemView.findViewById(R.id.tv_achievement);
        pieChart = (PieChart) itemView.findViewById(R.id.pie_chart);
    }
}