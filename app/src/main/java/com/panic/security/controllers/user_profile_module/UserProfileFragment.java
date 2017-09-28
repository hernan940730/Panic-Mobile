package com.panic.security.controllers.user_profile_module;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    // Search bar
    MaterialSearchView mSearchView;
    //List to search bar
    List<String> mListSource;

    ImageView mImageViewUserProfileAddFriend;
    // user_id of User that is displayed
    User mUserShown;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageViewUserProfileAddFriend = (ImageView) getView().findViewById(R.id.user_profile_add_friend);
        mImageViewUserProfileAddFriend.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {
                mUserShown = user;

                showUserData(mUserShown);
                addSearchBar();

                //actionEdit(view);
                actionAddFriend();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

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

                FirebaseDAO.getInstance().getUserByEmail(query, new DataCallback<User>() {
                    @Override
                    public void onDataReceive(User user) {

                        mUserShown = user;
                        showUserData(user);
                        setAddFriendIcon(mAuth.getCurrentUser().getUid(), user);
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

        FirebaseDAO.getInstance().getAllEmailsForAllUsers(new DataCallback<List<String> >() {
            @Override
            public void onDataReceive(List<String> usersEmails) {
                mListSource = usersEmails;
                String[] sourceArr = new String[mListSource.size()];
                mSearchView.setSuggestions(mListSource.toArray(sourceArr));
            }
        });
    }

    public void setAddFriendIcon(String currentUserID, User friendUser){

        if( friendUser.getId().equals(mAuth.getCurrentUser().getUid()) ){
            mImageViewUserProfileAddFriend.setVisibility(View.GONE);

        }else{
            mImageViewUserProfileAddFriend.setVisibility(View.VISIBLE);
            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_add_person);

            //Change icon of friend request if friends request was sent
            FirebaseDAO.getInstance().areFriendRequestOut(currentUserID, friendUser.getId(), new DataCallback<User.FriendRequestOut>() {
                @Override
                public void onDataReceive(User.FriendRequestOut friend) {
                    if(friend != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                    }
                }
            });

            //Change icon of friend request if they are friends
            FirebaseDAO.getInstance().areFriends(currentUserID, friendUser.getId(), new DataCallback<User.Friend>() {
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

    public void showUserData(User user){

        TextView textUserProfileShortDesc = (TextView) getView().findViewById(R.id.user_profile_short_desc);
        TextView textUserProfileEmail = (TextView) getView().findViewById(R.id.user_profile_email);
        TextView textUserProfilePhoneNumber = (TextView) getView().findViewById(R.id.user_profile_phone_number);
        TextView textUserProfileNumberReports = (TextView) getView().findViewById(R.id.user_profile_number_reports);
        final ImageButton imageButtonProfilePicture = (ImageButton) getView().findViewById(R.id.default_user_profile);

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

        FirebaseDAO.getInstance().getProfileImageInBytes(user.getId(), new DataCallback<byte []>() {
            @Override
            public void onDataReceive (byte []data) {
                if (data != null) {
                    imageButtonProfilePicture.setImageBitmap(BitmapFactory.decodeByteArray (data, 0, data.length));
                }
                else {
                    imageButtonProfilePicture.setImageResource(R.mipmap.ic_default_user_profile);
                }

            }
        });

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

    /*public void actionEdit(View view){

        ImageView imageViewUserProfileEditInfo = (ImageView) view.findViewById(R.id.user_profile_edit_info);

        imageViewUserProfileEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Edit Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void actionAddFriend () {

        mImageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDAO.getInstance().areFriendRequestOut(mAuth.getCurrentUser().getUid(), mUserShown.getId(), new DataCallback<User.FriendRequestOut>() {
                    @Override
                    public void onDataReceive(User.FriendRequestOut friend) {
                        if(friend == null){
                            FirebaseDAO.getInstance().pushFriendRequestOutToUser(mAuth.getCurrentUser().getUid(), new User.FriendRequestOut(mUserShown.getId(), 0L ));
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
