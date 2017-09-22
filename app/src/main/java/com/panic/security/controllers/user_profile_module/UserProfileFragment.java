package com.panic.security.controllers.user_profile_module;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    // Search bar
    MaterialSearchView mSearchView;
    //List to search bar
    List<String> mListSource;

    ImageView mImageViewUserProfileAddFriend;
    // user_id of User that is displayed
    String mUserShownID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUserShownID = mAuth.getCurrentUser().getUid();

        // Image to add friend
        mImageViewUserProfileAddFriend = (ImageView) view.findViewById(R.id.user_profile_add_friend);
        mImageViewUserProfileAddFriend.setVisibility(View.GONE);

        addSearchBar();
        showUserData(mUserShownID);
        //actionEdit(view);
        actionAddFriend(view);

        // Inflate the layout for this fragment
        return view;
    }

    public void addSearchBar(){

        mSearchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor_search);

        fillListSourceToSearch();

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                FirebaseDAO.getInstance().getProfileIDByFullName(query, new DataCallback<String>() {
                    @Override
                    public void onDataReceive(String profileID) {

                        FirebaseDAO.getInstance().getUserIDByProfileID(profileID, new DataCallback<String>() {
                            @Override
                            public void onDataReceive(String userID) {

                                mUserShownID = userID;
                                showUserData(userID);
                                setAddFriendIcon(mAuth.getCurrentUser().getUid(), userID);

                            }
                        });

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        setHasOptionsMenu(true); //To show search bar, call onCreateOptionsMenu
    }

    public void fillListSourceToSearch(){
        mListSource = new ArrayList<String>();

        FirebaseDAO.getInstance().getAllFullNamesForAllProfiles(new DataCallback<List<String> >() {
            @Override
            public void onDataReceive(List<String> usersNames) {
                mListSource = usersNames;
                String[] sourceArr = new String[mListSource.size()];
                mSearchView.setSuggestions(mListSource.toArray(sourceArr));
            }
        });
    }

    public void setAddFriendIcon(String currentUserID, String friendUserID){

        if( mUserShownID.equals(mAuth.getCurrentUser().getUid()) ){
            mImageViewUserProfileAddFriend.setVisibility(View.GONE);

        }else{
            mImageViewUserProfileAddFriend.setVisibility(View.VISIBLE);
            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_add_person);

            //Change icon of friend request if friends request was sent
            FirebaseDAO.getInstance().areFriendRequestOut(currentUserID, friendUserID, new DataCallback<User.Friend>() {
                @Override
                public void onDataReceive(User.Friend friend) {
                    if(friend != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                    }
                }
            });

            //Change icon of friend request if they are friends
            FirebaseDAO.getInstance().areFriends(currentUserID, friendUserID, new DataCallback<User.Friend>() {
                @Override
                public void onDataReceive(User.Friend friend) {
                    if(friend != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends);
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
    }

    public void showUserData(String ID){

        FirebaseDAO.getInstance().getUserByID(ID, new DataCallback<User>() {
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

    /*public void actionEdit(View view){

        ImageView imageViewUserProfileEditInfo = (ImageView) view.findViewById(R.id.user_profile_edit_info);

        imageViewUserProfileEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "Edit Profile", Toast.LENGTH_SHORT).show();

            }
        });
    }*/

    public void actionAddFriend(View view) {

        mImageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDAO.getInstance().areFriendRequestOut(mAuth.getCurrentUser().getUid(), mUserShownID, new DataCallback<User.Friend>() {
                    @Override
                    public void onDataReceive(User.Friend friend) {
                        if(friend == null){
                            FirebaseDAO.getInstance().pushFriendRequestOutToUser(mAuth.getCurrentUser().getUid(), new User.FriendRequestOut(mUserShownID, (long)0 ));
                            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                            Toast.makeText(getActivity(), getResources().getString(R.string.friend_request_sent), Toast.LENGTH_SHORT).show();
                            //Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_sent), Snackbar.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), getResources().getString(R.string.request_pending_acceptance), Toast.LENGTH_SHORT).show();
                            //Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.request_pending_acceptance), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

}
