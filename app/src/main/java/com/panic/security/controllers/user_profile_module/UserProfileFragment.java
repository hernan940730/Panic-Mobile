package com.panic.security.controllers.user_profile_module;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        showUserData();

        // Inflate the layout for this fragment
        return view;
    }

    public void showUserData(){

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {

                TextView textUserProfileShortDesc = (TextView) getView().findViewById(R.id.user_profile_short_desc);
                TextView textUserProfileEmail = (TextView) getView().findViewById(R.id.user_profile_email);
                TextView textUserProfilePhoneNumber = (TextView) getView().findViewById(R.id.user_profile_phone_number);
                TextView textUserProfileNumberReports = (TextView) getView().findViewById(R.id.user_profile_number_reports);

                textUserProfileShortDesc.setText(getResources().getString(R.string.user_profile_short_desc));
                if(user.getEmail() != null){
                    textUserProfileEmail.setText(getResources().getString(R.string.user_profile_email) + " " +user.getEmail());
                }else{
                    textUserProfileEmail.setText(getResources().getString(R.string.user_profile_email));
                }
                if(user.getPhone_number() != null){
                    textUserProfilePhoneNumber.setText(getResources().getString(R.string.user_profile_phone_number) + " " +user.getPhone_number());
                }else{
                    textUserProfilePhoneNumber.setText(getResources().getString(R.string.user_profile_phone_number));
                }
                if(user.getReports() != null){
                    textUserProfileNumberReports.setText(getResources().getString(R.string.user_profile_number_reports) + " " + user.getReports().size());
                }else{
                    textUserProfileNumberReports.setText(getResources().getString(R.string.user_profile_number_reports) + " 0");
                }

                FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                    @Override
                    public void onDataReceive(Profile profile) {

                        TextView textUserProfileName = (TextView) getView().findViewById(R.id.user_profile_name);
                        TextView textUserProfileLastName = (TextView) getView().findViewById(R.id.user_profile_last_name);
                        TextView textUserProfileLocation = (TextView) getView().findViewById(R.id.user_profile_location);
                        TextView textUserProfileGender = (TextView) getView().findViewById(R.id.user_profile_gender);
                        TextView textUserProfileBirthday = (TextView) getView().findViewById(R.id.user_profile_birthday);

                        textUserProfileName.setText(profile.getName());
                        textUserProfileLastName.setText(profile.getLast_name());
                        if(profile.getCountry() != null){
                            textUserProfileLocation.setText(getResources().getString(R.string.user_profile_location) + " " + profile.getCountry());
                        }else{
                            textUserProfileLocation.setText(getResources().getString(R.string.user_profile_location));
                        }
                        if(profile.getGender() != null){
                            textUserProfileGender.setText(getResources().getString(R.string.user_profile_gender) + " " +profile.getGender());
                        }else{
                            textUserProfileGender.setText(getResources().getString(R.string.user_profile_gender));
                        }
                        if(profile.getBirthday() != 0){
                            textUserProfileBirthday.setText(getResources().getString(R.string.user_profile_birthday) + " " +profile.getBirthday());
                        }else{
                            textUserProfileBirthday.setText(getResources().getString(R.string.user_profile_birthday));
                        }

                    }
                });

            }
        });

    }

}
