package com.abbvisor.abbvisor.scanbeacon;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.customtabs.CustomTabActivityHelper;
import com.abbvisor.abbvisor.customtabs.config.LinkTransformationMethod;
import com.abbvisor.abbvisor.databinding.FragmentBeaconScannerBinding;
import com.abbvisor.abbvisor.model.beacon.BeaconContent;
import com.abbvisor.abbvisor.scanbeacon.adapter.BeaconActivityAdapter;
import com.abbvisor.abbvisor.scanbeacon.adapter.HorizontalAdapter;
import com.abbvisor.abbvisor.scanbeacon.data.BeaconID;
import com.abbvisor.abbvisor.scanbeacon.manager.ProximityContentManager;
import com.abbvisor.abbvisor.util.animation.ResizeAnimation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.estimote.sdk.SystemRequirementsChecker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static com.abbvisor.abbvisor.util.DeviceUtils.dpToPx;
import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

public class BeaconScannerFragment extends Fragment implements
        BeaconScannerContract.View,
        android.view.View.OnClickListener{

    private static final String TAG = BeaconScannerFragment.class.getSimpleName();

    private BeaconScannerContract.UserActionsListener mActionsListener;

    private ProximityContentManager mProximityContentManager;

    private HorizontalAdapter mHorizontalAdapter;

    FragmentBeaconScannerBinding binding;

    BottomSheetBehavior bottomSheetBehavior;
    private BeaconActivityAdapter mBeaconActivityAdapter;
    private CustomTabActivityHelper mCustomTabActivityHelper;

    public BeaconScannerFragment() {
        super();
    }

    public static BeaconScannerFragment newInstance() {
        BeaconScannerFragment fragment = new BeaconScannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

//        if (savedInstanceState != null)
//            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beacon_scanner, container, false);
        View rootView = binding.getRoot();
        initInstances(savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mActionsListener = new BeaconScannerPresenter(getActivity(), this);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
    }

    private void initInstances(Bundle savedInstanceState) {
        mProximityContentManager = new ProximityContentManager(getContext());
        mProximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(BeaconID beaconID) {
                binding.viewFlipper.setInAnimation(getContext(), R.anim.fade_in);
                binding.viewFlipper.setOutAnimation(getContext(), R.anim.fade_out);

                if (beaconID != null) {
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String major = String.valueOf(beaconID.getMajor());
                    String minor = String.valueOf(beaconID.getMinor());
                    mActionsListener.getBeaconContent(major, minor, customerId);
                }
            }
        });

        mHorizontalAdapter = new HorizontalAdapter(getActivity(), mCustomTabActivityHelper);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.beaconTemplate1.rvTemplate1);

        configTextView();

        initBottomSheet();
    }

    private void configTextView() {
        binding.beaconTemplate2.tvTitle.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate2.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());

        binding.beaconTemplate3.tv01Description.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate3.tv01Description.setMovementMethod(LinkMovementMethod.getInstance());

        binding.beaconTemplate3.tv02Description.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate3.tv02Description.setMovementMethod(LinkMovementMethod.getInstance());

        binding.beaconTemplate3.tv03Description.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate3.tv03Description.setMovementMethod(LinkMovementMethod.getInstance());

        binding.beaconTemplate3.tv04Description.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate3.tv04Description.setMovementMethod(LinkMovementMethod.getInstance());

        binding.beaconTemplate4.tvDescription.setTransformationMethod(
                new LinkTransformationMethod(getActivity(), mCustomTabActivityHelper));
        binding.beaconTemplate4.tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initBottomSheet() {
        binding.cvActivity.setOnClickListener(this);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull android.view.View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.tvActivity.setText(getString(R.string.action_view_activities));
                    binding.ivActivity.setImageDrawable(
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.tvActivity.setText(getString(R.string.action_hide_activities));
                    binding.ivActivity.setImageDrawable(
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
                }
            }

            @Override
            public void onSlide(@NonNull android.view.View bottomSheet, float slideOffset) {
                int m1 = dpToPx(getContext(), (int) (24 - (slideOffset * 24)));
                ResizeAnimation anim = new ResizeAnimation(
                        binding.bottomSheet,
                        bottomSheetBehavior,
                        m1
                );
                anim.setDuration(0);
                binding.bottomSheet.startAnimation(anim);
            }
        });

        mBeaconActivityAdapter = new BeaconActivityAdapter(getActivity(), mCustomTabActivityHelper);
        binding.rvBeaconActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBeaconActivity.setAdapter(mBeaconActivityAdapter);
    }

    @Override
    public void setBeaconTemplate(BeaconContent beaconContent) {
        if (beaconContent == null) {
            binding.viewFlipper.setDisplayedChild(0);
        } else {
            String template = beaconContent.getTemplate();

            switch (template) {
                case "1":
                    setViewTemplate1(beaconContent);
                    break;
                case "2":
                    setViewTemplate2(beaconContent);
                    break;
                case "3":
                    setViewTemplate3(beaconContent);
                    break;
                case "4":
                    setViewTemplate4(beaconContent);
                    break;
            }

            binding.cvActivity.setVisibility(View.GONE);

            if (beaconContent.getActivities() != null && !beaconContent.getActivities().isEmpty())
                setBeaconActivity(beaconContent);
        }
    }

    @Override
    public void setViewTemplate1(BeaconContent beaconContent) {
        binding.beaconTemplate1.circleIndicatorView.setPageIndicators(3);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.beaconTemplate1.rvTemplate1.setHasFixedSize(true);
        binding.beaconTemplate1.rvTemplate1.setLayoutManager(linearLayoutManager);
        binding.beaconTemplate1.rvTemplate1.setAdapter(mHorizontalAdapter);
        binding.beaconTemplate1.rvTemplate1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int pos = ((LinearLayoutManager) binding.beaconTemplate1.rvTemplate1.getLayoutManager()).findLastVisibleItemPosition();
                binding.beaconTemplate1.circleIndicatorView.setCurrentPage(pos);
            }
        });

        binding.viewFlipper.setDisplayedChild(1);

        mHorizontalAdapter.setContents(beaconContent);
    }

    @Override
    public void setViewTemplate2(BeaconContent beaconContent) {
        binding.viewFlipper.setDisplayedChild(2);
        String image = beaconContent.getImages().get(0);
        String topic = beaconContent.getTopics().get(0);
        String open = beaconContent.getOpentime();
        String close = beaconContent.getClosetime();
        String tel = beaconContent.getTelephone();
        String des = beaconContent.getContents().get(0);

        Glide.with(getContext())
                .load(image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.beaconTemplate2.pvContent);

        binding.beaconTemplate2.tvTitle.setText(topic);

        binding.beaconTemplate2.tvOpeningTime.setText(
                String.format(getString(R.string.text_beacon_opening_time), open, close));

        binding.beaconTemplate2.tvTel.setText(String.format(getString(R.string.text_beacon_tel), tel));

        binding.beaconTemplate2.tvDescription.setText(des);
    }

    @Override
    public void setViewTemplate3(BeaconContent beaconContent) {
        binding.viewFlipper.setDisplayedChild(3);
        List<String> images = beaconContent.getImages();
        List<String> topics = beaconContent.getTopics();
        List<String> prices = beaconContent.getPrices();
        List<String> contents = beaconContent.getContents();

        /**
         * 1st item
         */
        if (topics.get(0).isEmpty() && images.get(0).isEmpty()
                && prices.get(0).isEmpty() && contents.get(0).isEmpty()) {
            binding.beaconTemplate3.cv01.setVisibility(View.GONE);
        } else {

            if (images.get(0).isEmpty()) {
                binding.beaconTemplate3.pv01.setVisibility(View.GONE);
            } else {
                Glide.with(getContext())
                        .load(images.get(0))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.beaconTemplate3.pv01);

            }

            if (topics.get(0).isEmpty()) {
                binding.beaconTemplate3.tv01Title.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv01Title.setText(topics.get(0));
            }

            if (prices.get(0).isEmpty()) {
                binding.beaconTemplate3.tv01Price.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv01Price.setText(
                        String.format(getString(R.string.text_beacon_price), prices.get(0)));
            }

            if (contents.get(0).isEmpty()) {
                binding.beaconTemplate3.tv01Description.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv01Description.setText(contents.get(0));
            }
        }

        /**
         * 2st item
         */
        if (topics.get(1).isEmpty() && images.get(1).isEmpty()
                && prices.get(1).isEmpty() && contents.get(1).isEmpty()) {
            binding.beaconTemplate3.cv02.setVisibility(View.GONE);
        } else {

            if (images.get(1).isEmpty()) {
                binding.beaconTemplate3.pv02.setVisibility(View.GONE);
            } else {
                Glide.with(getContext())
                        .load(images.get(1))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.beaconTemplate3.pv02);
            }

            if (topics.get(1).isEmpty()) {
                binding.beaconTemplate3.tv02Title.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv02Title.setText(topics.get(1));
            }

            if (prices.get(1).isEmpty()) {
                binding.beaconTemplate3.tv02Price.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv02Price.setText(
                        String.format(getString(R.string.text_beacon_price), prices.get(1)));
            }

            if (contents.get(1).isEmpty()) {
                binding.beaconTemplate3.tv02Description.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv02Description.setText(contents.get(1));
            }
        }

        /**
         * 3st item
         */
        if (topics.get(2).isEmpty() && images.get(2).isEmpty()
                && prices.get(2).isEmpty() && contents.get(2).isEmpty()) {
            binding.beaconTemplate3.cv03.setVisibility(View.GONE);
        } else {

            if (images.get(2).isEmpty()) {
                binding.beaconTemplate3.pv03.setVisibility(View.GONE);
            } else {
                Glide.with(getContext())
                        .load(images.get(2))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.beaconTemplate3.pv03);
            }

            if (topics.get(2).isEmpty()) {
                binding.beaconTemplate3.tv03Title.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv03Title.setText(topics.get(2));
            }

            if (prices.get(2).isEmpty()) {
                binding.beaconTemplate3.tv03Price.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv03Price.setText(
                        String.format(getString(R.string.text_beacon_price), prices.get(2)));
            }

            if (contents.get(2).isEmpty()) {
                binding.beaconTemplate3.tv03Description.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv03Description.setText(contents.get(2));
            }
        }

        /**
         * 4st item
         */
        if (topics.get(3).isEmpty() && images.get(3).isEmpty()
                && prices.get(3).isEmpty() && contents.get(3).isEmpty()) {
            binding.beaconTemplate3.cv04.setVisibility(View.GONE);
        } else {

            if (images.get(3).isEmpty()) {
                binding.beaconTemplate3.pv04.setVisibility(View.GONE);
            } else {
                Glide.with(getContext())
                        .load(images.get(3))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.beaconTemplate3.pv04);
            }

            if (topics.get(3).isEmpty()) {
                binding.beaconTemplate3.tv04Title.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv04Title.setText(topics.get(3));
            }

            if (prices.get(3).isEmpty()) {
                binding.beaconTemplate3.tv04Price.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv04Price.setText(
                        String.format(getString(R.string.text_beacon_price), prices.get(3)));
            }

            if (contents.get(3).isEmpty()) {
                binding.beaconTemplate3.tv04Description.setVisibility(View.GONE);
            } else {
                binding.beaconTemplate3.tv04Description.setText(contents.get(3));
            }
        }
    }

    @Override
    public void setViewTemplate4(BeaconContent beaconContent) {
        binding.viewFlipper.setDisplayedChild(4);
        String image = beaconContent.getImages().get(0);
        String topic = beaconContent.getTopics().get(0);
        String price = beaconContent.getPrices().get(0);
        String content = beaconContent.getContents().get(0);

        Glide.with(getContext())
                .load(image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.beaconTemplate4.pvContent);

        binding.beaconTemplate4.tvTitle.setText(topic);
        binding.beaconTemplate4.tvPrice.setText(
                String.format(getString(R.string.text_beacon_price), price));
        binding.beaconTemplate4.tvDescription.setText(content);
    }

    @Override
    public void setBeaconActivity(BeaconContent beaconContent) {
        binding.progressBar.setVisibility(View.GONE);
        binding.cvActivity.setVisibility(View.VISIBLE);
        mBeaconActivityAdapter.setBeaconContent(beaconContent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mCustomTabActivityHelper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SystemRequirementsChecker.checkWithDefaultDialogs(getActivity())) {
            LOGE(TAG, "Can't scan for beacons, some pre-conditions were not met");
//            LOGE(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
//            LOGE(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            LOGE(TAG, "Starting ProximityContentManager content updates");
            mProximityContentManager.startContentUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProximityContentManager != null)
            mProximityContentManager.stopContentUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCustomTabActivityHelper.unbindCustomTabsService(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProximityContentManager != null)
            mProximityContentManager.destroy();
    }

    //TODO: Show full image and zoomable

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.cv_activity:
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    private void onRestoreInstanceState(Bundle savedInstanceState) {
//    }

}
