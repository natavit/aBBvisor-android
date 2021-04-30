package com.abbvisor.abbvisor.scanbeacon.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.customtabs.CustomTabActivityHelper;
import com.abbvisor.abbvisor.customtabs.config.LinkTransformationMethod;
import com.abbvisor.abbvisor.model.beacon.BeaconContent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by natavit on 1/13/2017 AD.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = HorizontalAdapter.class.getSimpleName();
    private final FragmentActivity mActivity;
    private final CustomTabActivityHelper mCustomTabActivityHelper;

    private BeaconContent mBeaconContent;

    public HorizontalAdapter(FragmentActivity activity, CustomTabActivityHelper customTabActivityHelper) {
        mActivity = activity;
        mCustomTabActivityHelper = customTabActivityHelper;
    }

    public void setContents(BeaconContent beaconContent) {
        mBeaconContent = beaconContent;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_template_1, parent, false);

        return new Template1ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Template1ViewHolder viewHolder = (Template1ViewHolder) holder;

        String image = mBeaconContent.getImages().get(position);
        String title = mBeaconContent.getTopics().get(position);
        String content = mBeaconContent.getContents().get(position);

        viewHolder.tvTitle.setText(title);
        viewHolder.tvContent.setText(content);

        Glide.with(mActivity)
                .load(image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.pvContent);
    }

    @Override
    public int getItemCount() {
        if (mBeaconContent == null)
            return 0;

        return Math.max(mBeaconContent.getImages().size(),
                Math.max(mBeaconContent.getContents().size(), mBeaconContent.getTopics().size()));
    }

    private class Template1ViewHolder extends RecyclerView.ViewHolder {

        private PhotoView pvContent;
        private TextView tvTitle;
        private TextView tvContent;

        private Template1ViewHolder(View itemView) {
            super(itemView);

            pvContent = (PhotoView) itemView.findViewById(R.id.pv_content);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_description);

            tvTitle.setTransformationMethod(new LinkTransformationMethod(mActivity, mCustomTabActivityHelper));
            tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }


}
