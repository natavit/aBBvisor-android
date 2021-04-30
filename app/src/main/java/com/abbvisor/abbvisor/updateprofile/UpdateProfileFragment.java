package com.abbvisor.abbvisor.updateprofile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abbvisor.abbvisor.R;
import com.abbvisor.abbvisor.databinding.FragmentUpdateProfileBinding;
import com.abbvisor.abbvisor.map.MapActivity;
import com.abbvisor.abbvisor.selectcharacteristic.CharacteristicActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UpdateProfileFragment extends Fragment implements
        UpdateProfileContract.View {

    private static final String TAG = UpdateProfileFragment.class.getName();

    private UpdateProfileContract.UserActionsListener mActionsListener;

    FragmentUpdateProfileBinding binding;

    public UpdateProfileFragment() {
        super();
    }

    public static UpdateProfileFragment newInstance() {
        return new UpdateProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false);
        View rootView = binding.getRoot();
        initInstances(savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
    }

    private void initInstances(Bundle savedInstanceState) {
        mActionsListener = new UpdateProfilePresenter(getContext(), this, FirebaseAuth.getInstance().getCurrentUser());

        binding.btnNext.setCustomTextFont("Aller-Regular.ttf");
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender = binding.stickySwitch.getText().toLowerCase();
                String age = binding.etAge.getText().toString();
                mActionsListener.saveProfile(gender, age);
            }
        });
    }

    @Override
    public void showMapActivity() {
        Intent i = new Intent(getActivity(), MapActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void showPreferencesActivity() {
        Intent i = new Intent(getActivity(), CharacteristicActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void showAgeRequired() {
        binding.etAge.setError("Age is required.");
    }
}
