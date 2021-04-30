package com.abbvisor.abbvisor.achievement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.achievement.adapter.AchievementAdapter;
import com.abbvisor.abbvisor.databinding.FragmentAchievementBinding;
import com.abbvisor.abbvisor.manager.AchievementManager;
import com.abbvisor.abbvisor.util.DeviceUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AchievementFragment extends Fragment implements AchievementContract.View {

    private static final String TAG = AchievementFragment.class.getName();

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    private AchievementContract.UserActionsListener mActionsListener;

    FragmentAchievementBinding binding;

    private AchievementManager mAchievementManager;
    private boolean mStoragePermissionGranted;
    private AchievementAdapter mAchievementAdapter;

    public AchievementFragment() {
        super();
    }

    public static AchievementFragment newInstance() {
        return new AchievementFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_achievement, container, false);
        View rootView = binding.getRoot();
        initInstances(savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mAchievementManager = new AchievementManager(getContext());
        mActionsListener = new AchievementPresenter(this, this, mAchievementManager);
    }

    private void initInstances(Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        if (savedInstanceState != null)
            mAchievementManager.onRestoreInstanceState(savedInstanceState.getBundle("achievementManager"));

        mAchievementAdapter = new AchievementAdapter(getActivity());
        mAchievementAdapter.setAchievement(mAchievementManager.getAchievement());

        binding.rvAchievement.setHasFixedSize(true);
        binding.rvAchievement.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.rvAchievement.setAdapter(mAchievementAdapter);

        binding.tvSummary.setText(String.format(getString(R.string.text_discoverd_beacon), mAchievementManager.getCount()));

        if (DeviceUtils.isInternetConnectionAvailable(getContext())) {
            mActionsListener.getAchievements(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mStoragePermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStoragePermissionGranted = true;
                }
                break;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void refreshAchievements() {
        if (mAchievementManager.getCount() > 0) {
            mAchievementAdapter.setAchievement(mAchievementManager.getAchievement());
            mAchievementAdapter.notifyDataSetChanged();
            binding.tvSummary.setText(String.format(getString(R.string.text_discoverd_beacon), mAchievementManager.getCount()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle("achievementManager",
                mAchievementManager.onSaveInstanceState());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.achievement_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.menu_item_share:
                shareImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        File image = saveScreenshot();

        if (image == null) {
            Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.fromFile(image);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setDataAndType(uri, "image/png");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My beacon achievements");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    //ref: http://stackoverflow.com/questions/27650497/how-to-take-screenshot-of-whole-activity-page-programmatically
    private Bitmap getScreenBitmap() {
        binding.content.destroyDrawingCache();
        binding.content.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        binding.content.layout(0, 0, binding.content.getMeasuredWidth(), binding.content.getMeasuredHeight());
        binding.content.buildDrawingCache(false);
        Bitmap original = binding.content.getDrawingCache();
        Bitmap.Config config = null;

        if (original != null) {
            config = original.getConfig();
        }

        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap b = original.copy(config, false);
        binding.content.destroyDrawingCache();


//        binding.content.setDrawingCacheEnabled(true);
//        binding.content.measure(LinearLayout.MeasureSpec.makeMeasureSpec(0, LinearLayout.MeasureSpec.UNSPECIFIED),
//                LinearLayout.MeasureSpec.makeMeasureSpec(0, LinearLayout.MeasureSpec.UNSPECIFIED));
//        binding.content.layout(0, 0, binding.content.getMeasuredWidth(), binding.content.getMeasuredHeight());
//        binding.content.buildDrawingCache(true);
//        Bitmap b = Bitmap.createBitmap(binding.content.getDrawingCache());
//        binding.content.destroyDrawingCache();
//        binding.content.setDrawingCacheEnabled(false);
        return b;
    }

    private File saveScreenshot() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if (mStoragePermissionGranted) {

            binding.content.setDrawingCacheEnabled(true);
            binding.content.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
            Bitmap bitmap = binding.content.getDrawingCache();
//            Bitmap bitmap = getScreenBitmap();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "aBBvisor_" + timeStamp + ".png";

            String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/App";

            File dir = new File(dirPath);

            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dirPath, fileName);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes);

            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(bytes.toByteArray());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            getActivity().sendBroadcast(mediaScanIntent);

            binding.content.setDrawingCacheEnabled(false);

            return file;
        }

        return null;
    }
}
