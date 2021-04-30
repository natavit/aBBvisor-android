package com.abbvisor.abbvisor.selectcharacteristic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.map.MapActivity;
import com.abbvisor.abbvisor.selectcharacteristic.adapter.CharacteristicAdapter;
import com.abbvisor.abbvisor.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.abbvisor.abbvisor.util.LogUtils.LOGE;

public class CharacteristicFragment extends Fragment implements CharacteristicContract.View {

    private static final String TAG = CharacteristicFragment.class.getName();

    public static final int FLAG_NEW_ACTIVITY = 1;
    public static final int FLAG_UPDATE_CURRENT_ACTIVITY = 2;

    private CharacteristicContract.UserActionsListener mActionsListener;

    private List<String> mSelectedPrefs;

    private CharacteristicAdapter mPrefsAdapter;

    private FirebaseUser mUser;
    private SharedPreferences sp;

    public CharacteristicFragment() {
        super();
    }

    public static CharacteristicFragment newInstance(ArrayList<String> prefs) {
        CharacteristicFragment fragment = new CharacteristicFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(Constants.PREFERENCE_LIST, prefs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        sp = getActivity()
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

//        String id = Profile.getCurrentProfile().getId();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Type type = new TypeToken<List<String>>() {
        }.getType();

        String json = sp.getString(mUser.getUid() + "_prefList", null);

        mSelectedPrefs = new Gson().fromJson(json, type);
        if (mSelectedPrefs == null) {
            mSelectedPrefs = new ArrayList<>();
        }
        LOGE(TAG, "Json: " + json);
        LOGE(TAG, "SelectedPrefs: " + mSelectedPrefs.size());
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        mActionsListener = new CharacteristicPresenter(getActivity(), this, mUser);

        ArrayList<String> prefs = getArguments().getStringArrayList(Constants.PREFERENCE_LIST);
        mPrefsAdapter = new CharacteristicAdapter(getContext(), prefs, mSelectedPrefs, mItemListener);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        recyclerView.setAdapter(mPrefsAdapter);

        Button btnDone = (Button) rootView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionsListener.saveSelectedPreferences(mSelectedPrefs);
            }
        });

        Button btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapActivity(FLAG_UPDATE_CURRENT_ACTIVITY);
            }
        });

        boolean isFirstSignin = sp.getBoolean(mUser.getUid() + "_firstSignin", true);
        if (!isFirstSignin) {
            btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void showMapActivity(int flag) {
        if (flag == FLAG_NEW_ACTIVITY) {
            Intent i = new Intent(getActivity(), MapActivity.class);
            startActivity(i);
            getActivity().finish();
        } else if (flag == FLAG_UPDATE_CURRENT_ACTIVITY) {
            getActivity().finish();
        }
    }

    /**
     * Listener for selecting prefs in the RecyclerView.
     */
    PrefItemListener mItemListener = new PrefItemListener() {
        @Override
        public void onPrefClick(String pref) {
            if (mSelectedPrefs.contains(pref)) {
                mSelectedPrefs.remove(pref);
//                LOGE(TAG, "Remove: " + pref);
            }
            else {
                mSelectedPrefs.add(pref);
//                LOGE(TAG, "Add: " + pref);
            }
        }
    };

    public interface PrefItemListener {
        void onPrefClick(String pref);
    }
}
