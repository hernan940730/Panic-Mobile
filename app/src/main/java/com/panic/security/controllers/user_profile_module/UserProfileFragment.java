package com.panic.security.controllers.user_profile_module;

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
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.entities.Friend;
import com.panic.security.entities.FriendRequest;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.FirebaseDAO;

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

    ProgressBar mProgressBar;

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageViewUserProfileAddFriend = (ImageView) getView().findViewById(R.id.user_profile_add_friend);

        mAuth = FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar) view.findViewById(R.id.user_profile_progress_bar);

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
                actionAddFriend(user);

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

                mProgressBar.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                FirebaseDAO.getInstance().getUserByEmail(query, new DataCallback<User>() {
                    @Override
                    public void onDataReceive(User user) {

                        mUserShown = user;
                        showUserData(user);

                        mProgressBar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        setHasOptionsMenu(true); //To show search bar, call onCreateOptionsMenu
    }

    public void fillListSourceToSearch() {
        mListSource = DataLoader.getInstance().getEmails();
        String[] sourceArr = new String[mListSource.size()];
        mSearchView.setSuggestions(mListSource.toArray(sourceArr));
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

        final TextView textUserProfileShortDesc = (TextView) getView().findViewById(R.id.user_profile_short_desc);
        final TextView textUserProfileEmail = (TextView) getView().findViewById(R.id.user_profile_email);
        final TextView textUserProfilePhoneNumber = (TextView) getView().findViewById(R.id.user_profile_phone_number);
        final TextView textUserProfileNumberReports = (TextView) getView().findViewById(R.id.user_profile_number_reports);
        final ImageButton imageButtonProfilePicture = (ImageButton) getView().findViewById(R.id.default_user_profile);

        //TODO Allow change short description to the user
        textUserProfileShortDesc.setText(getResources().getString(R.string.user_profile_short_desc));
        if(user.getEmail() != null){
            textUserProfileEmail.setText(user.getEmail());
        }
        if(user.getPhone_number() != null){
            textUserProfilePhoneNumber.setText(user.getPhone_number());
        }

        FirebaseDAO.getInstance().getUserReports(user.getId(), new DataCallback<Map<String, String>>() {
            @Override
            public void onDataReceive(Map<String, String> data) {
                if (data != null){
                    textUserProfileNumberReports.setText(String.valueOf(data.size()));
                }
            }
        });

        imageButtonProfilePicture.setPadding(2, 2, 2, 2);
        FirebaseDAO.getInstance().putRoundProfileImageInView(user.getId(), getActivity(), imageButtonProfilePicture);

        /*
        if (data != null) {
            imageButtonProfilePicture.setPadding(2, 2, 2, 2);
            imageButtonProfilePicture.setImageBitmap(
                    ImageConverter.getRoundedCornerBitmap (data)
            );
        }
        else {
            imageButtonProfilePicture.setImageResource(R.mipmap.ic_default_user_profile);
        }
        */

        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
            @Override
            public void onDataReceive(Profile profile) {
                if (profile == null) {
                    return;
                }
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

    public void setAddFriendIcon(final String currentUserID, final User friendUser){

        if( currentUserID.equals(friendUser.getId()) ){
            mImageViewUserProfileAddFriend.setVisibility(View.GONE);
        }else{

            //Change icon of friend request if friend sent request
            FirebaseDAO.getInstance().areFriendRequestIn(currentUserID, friendUser.getId(), new DataCallback<FriendRequest>() {
                @Override
                public void onDataReceive(FriendRequest friendIn) {
                    if(friendIn != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_accept_request);
                    }else{

                        //Change icon of friend request if friends request was sent
                        FirebaseDAO.getInstance().areFriendRequestOut(currentUserID, friendUser.getId(), new DataCallback<FriendRequest>() {
                            @Override
                            public void onDataReceive(FriendRequest friendOut) {
                                if(friendOut != null){
                                    mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                                }else{

                                    //Change icon of friend request if they are friends
                                    FirebaseDAO.getInstance().areFriends(currentUserID, friendUser.getId(), new DataCallback<Friend>() {
                                        @Override
                                        public void onDataReceive(Friend friend) {
                                            if(friend != null){
                                                mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends_gold);
                                            }else{
                                                mImageViewUserProfileAddFriend.setVisibility(View.VISIBLE);
                                                mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_add_person);
                                            }

                                        }
                                    });

                                }
                            }
                        });

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

    public void actionAddFriend(final User user) {

        mImageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDAO.getInstance().areFriends(user.getId(), mUserShown.getId(), new DataCallback<Friend>() {
                    @Override
                    public void onDataReceive(Friend friend) {

                        if(friend != null){

                            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends_gold);
                            Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.you_are_friends), Snackbar.LENGTH_LONG).show();

                        }else{

                            FirebaseDAO.getInstance().areFriendRequestIn(user.getId(), mUserShown.getId(), new DataCallback<FriendRequest>() {
                                @Override
                                public void onDataReceive(FriendRequest friendIn) {
                                    if(friendIn != null){
                                        FirebaseDAO.getInstance().pushFriend(mUserShown.getId());
                                        FirebaseDAO.getInstance().removeFriendRequest(mUserShown.getId());

                                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends_gold);
                                        Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_accepted), Snackbar.LENGTH_LONG).show();

                                    }else{

                                        FirebaseDAO.getInstance().areFriendRequestOut(user.getId(), mUserShown.getId(), new DataCallback<FriendRequest>() {
                                            @Override
                                            public void onDataReceive(FriendRequest friendOut) {
                                                if(friendOut == null){
                                                    FirebaseDAO.getInstance().pushFriendRequest(mUserShown.getId());
                                                    mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                                                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_sent), Snackbar.LENGTH_LONG).show();

                                                }else{
                                                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.request_pending_acceptance), Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                        }

                    }
                });

            }
        });

    }

}
