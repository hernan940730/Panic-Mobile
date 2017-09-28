package com.panic.security.controllers.user_profile_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageButton;
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
    // ImageToAddFriend
    ImageView mImageViewUserProfileAddFriend;
    // user_id of User that is displayed
    User mUserShown;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageViewUserProfileAddFriend = (ImageView) getView().findViewById(R.id.user_profile_add_friend);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {

                Bundle bundle = getArguments();
                if(bundle != null){
                    User userFound = (User)getArguments().getSerializable("userFound");
                    mUserShown = userFound;
                    addSearchBar();
                }else{
                    mUserShown = user;
                }
                showUserData(mUserShown);

                //actionEdit(view);
                actionAddFriend(user, view);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
    }

    public void showUserData(User user){

        // Icon of friendship
        setAddFriendIcon(mAuth.getCurrentUser().getUid(), user);

        TextView textUserProfileShortDesc = (TextView) getView().findViewById(R.id.user_profile_short_desc);
        TextView textUserProfileEmail = (TextView) getView().findViewById(R.id.user_profile_email);
        TextView textUserProfilePhoneNumber = (TextView) getView().findViewById(R.id.user_profile_phone_number);
        TextView textUserProfileNumberReports = (TextView) getView().findViewById(R.id.user_profile_number_reports);
        final ImageButton imageButtonProfilePicture = (ImageButton) getView().findViewById(R.id.default_user_profile);

        //TODO Allow change short description to the user
        textUserProfileShortDesc.setText(getResources().getString(R.string.user_profile_short_desc));
        if(user.getEmail() != null){
            textUserProfileEmail.setText(user.getEmail());
        }
        if(user.getPhone_number() != null){
            textUserProfilePhoneNumber.setText(user.getPhone_number());
        }
        if(user.getReports() != null){
            textUserProfileNumberReports.setText(String.valueOf(user.getReports().size()));
        }

        FirebaseDAO.getInstance().getProfileImageInBytes(user.getKey(), new DataCallback<byte []>() {
            @Override
            public void onDataReceive (byte []data) {
                imageButtonProfilePicture.setImageBitmap(BitmapFactory.decodeByteArray (data, 0, data.length));
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
                    textUserProfileLocation.setText(profile.getCountry());
                }
                if(profile.getGender() != null){
                    textUserProfileGender.setText(profile.getGender());
                }
                //TODO parser long to date and not string
                if(profile.getBirthday() != 0){
                    textUserProfileBirthday.setText(String.valueOf(profile.getBirthday()));
                }

            }
        });

    }

    public void setAddFriendIcon(String currentUserID, User friendUser){

        if( friendUser.getKey().equals(mAuth.getCurrentUser().getUid()) ){
            mImageViewUserProfileAddFriend.setVisibility(View.GONE);

        }else{
            mImageViewUserProfileAddFriend.setVisibility(View.VISIBLE);
            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_add_person);

            //Change icon of friend request if friends request was sent
            FirebaseDAO.getInstance().areFriendRequestOut(currentUserID, friendUser.getKey(), new DataCallback<User.FriendRequestOut>() {
                @Override
                public void onDataReceive(User.FriendRequestOut friend) {
                    if(friend != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                    }
                }
            });

            //Change icon of friend request if they are friends
            FirebaseDAO.getInstance().areFriends(currentUserID, friendUser.getKey(), new DataCallback<User.Friend>() {
                @Override
                public void onDataReceive(User.Friend friend) {
                    if(friend != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends);
                    }
                }
            });
        }
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

    public void actionAddFriend(final User user, View view) {

        mImageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDAO.getInstance().areFriendRequestOut(user.getKey(), mUserShown.getKey(), new DataCallback<User.FriendRequestOut>() {
                    @Override
                    public void onDataReceive(User.FriendRequestOut friend) {
                        if(friend == null){
                            
                            // TODO mirar si son friendRequestIn agregarlo de una

                            FirebaseDAO.getInstance().pushFriendRequest(user , new User.FriendRequestOut(mUserShown.getKey(), 0L ));
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
