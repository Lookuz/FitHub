package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

public class EditProfileFragment extends Fragment implements WarningCallBack {

    private FitUser user;
    private ProfileFragment parent;
    private boolean isBack;

    private EditText name;
    private EditText password;
    private EditText bio;

    public static EditProfileFragment newInstance(ProfileFragment parent) {

        Bundle args = new Bundle();

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        fragment.parent = parent;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = ((MainPageActivity) getActivity()).getUser();
        this.isBack = false;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        this.initView(view);

        return view;
    }

    private void initView(View view) {
        this.name = view.findViewById(R.id.edit_profile_name);
        this.password = view.findViewById(R.id.edit_profile_password);
        this.bio = view.findViewById(R.id.edit_profile_bio);
    }

    public void applyChanges() {
        this.isBack = false;
        WarningDialog warningDialog = WarningDialog.newInstance(WarningEnum.SAVE_CHANGES, this);

        warningDialog.show(getFragmentManager(), "Save Changes");
    }

    public void onBackPressed() {
        this.isBack = true;

        WarningDialog warningDialog = WarningDialog.newInstance(WarningEnum.UNSAVED_POST, this);

        warningDialog.show(getFragmentManager(), "Unsaved Profile Changes");
    }

    @Override
    public void onCallBack(boolean exit) {
        if (exit && !this.isBack) {
            this.user.setName(this.name.getText().toString());
            this.user.setBio(this.bio.getText().toString());
            this.parent.saveChanges();
        } else if (exit && this.isBack) {
            parent.discardChanges();
        } else {
            return;
        }
    }
}
