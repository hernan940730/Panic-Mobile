package com.panic.security.controllers.user_profile_module;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfilePictureFragment extends Fragment {

    public static final String USER_BUNDLE = "USER_ID";

    public static final int PICK_IMAGE_TAG = 0;

    private ImageView profileImageView;
    private String userId;

    public EditProfilePictureFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = getView().findViewById(R.id.profile_picture_image);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            }
        });

        if (getArguments().containsKey(USER_BUNDLE)) {
            userId = getArguments().getString(USER_BUNDLE);
            if (userId.equals(FirebaseAuth.getInstance().getUid())) {
                setHasOptionsMenu(true);
            }

            showUserImage(userId);
        }
    }

    private void showUserImage(String userId) {
        FirebaseDAO.getInstance().putFullProfileImageInView(userId, getActivity(), profileImageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.picture_menu, menu);

        MenuItem item = menu.findItem(R.id.edit_picture);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_TAG);
                return true;
            }
        });
        item.setVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.toolbar).setBackgroundResource(R.color.black);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.toolbar).setBackgroundResource(R.color.colorPrimary);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getActivity().RESULT_OK == resultCode) {
            switch (requestCode) {
                case PICK_IMAGE_TAG:
                    Uri selectedImg = data.getData();
                    GlideApp.with(getActivity())
                            .load(selectedImg)
                            .into(profileImageView);

                    FirebaseDAO.getInstance().pushProfileImage(userId, selectedImg);
                    break;
            }
        }

    }
}
