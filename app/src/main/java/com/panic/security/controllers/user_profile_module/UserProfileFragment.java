package com.panic.security.controllers.user_profile_module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ImageConverter;

import java.util.ArrayList;
import java.util.List;

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

        FirebaseDAO.getInstance().getProfileImageInBytes(user.getId(), new DataCallback<byte[]>() {
            @Override
            public void onDataReceive (byte[] bytes) {
                if (bytes != null) {
                    imageButtonProfilePicture.setPadding(2, 2, 2, 2);
                    imageButtonProfilePicture.setImageBitmap(ImageConverter.getRoundedCornerBitmap (
                            BitmapFactory.decodeByteArray (bytes, 0, bytes.length)
                    ));
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
            FirebaseDAO.getInstance().areFriendRequestIn(currentUserID, friendUser.getId(), new DataCallback<User.FriendRequestIn>() {
                @Override
                public void onDataReceive(User.FriendRequestIn friendIn) {
                    if(friendIn != null){
                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_accept_request);
                    }else{

                        //Change icon of friend request if friends request was sent
                        FirebaseDAO.getInstance().areFriendRequestOut(currentUserID, friendUser.getId(), new DataCallback<User.FriendRequestOut>() {
                            @Override
                            public void onDataReceive(User.FriendRequestOut friendOut) {
                                if(friendOut != null){
                                    mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                                }else{

                                    //Change icon of friend request if they are friends
                                    FirebaseDAO.getInstance().areFriends(currentUserID, friendUser.getId(), new DataCallback<User.Friend>() {
                                        @Override
                                        public void onDataReceive(User.Friend friend) {
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

    public void actionAddFriend(final User user, View view) {

        mImageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDAO.getInstance().areFriends(user.getId(), mUserShown.getId(), new DataCallback<User.Friend>() {
                    @Override
                    public void onDataReceive(User.Friend friend) {

                        if(friend != null){

                            mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends_gold);
                            Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.you_are_friends), Snackbar.LENGTH_LONG).show();

                        }else{

                            FirebaseDAO.getInstance().areFriendRequestIn(user.getId(), mUserShown.getId(), new DataCallback<User.FriendRequestIn>() {
                                @Override
                                public void onDataReceive(User.FriendRequestIn friendIn) {
                                    if(friendIn != null){

                                        User.Friend newFriend = new User.Friend(mUserShown.getId(), 0L, false);
                                        FirebaseDAO.getInstance().pushFriend(user,  newFriend);

                                        User.Friend newFriend2 = new User.Friend(user.getId(), 0L, false);
                                        FirebaseDAO.getInstance().pushFriend(mUserShown,  newFriend2);

                                        FirebaseDAO.getInstance().removeFriendRequestIn(user, new User.FriendRequestIn(mUserShown.getId(), 0L));
                                        FirebaseDAO.getInstance().removeFriendRequestOut(mUserShown, new User.FriendRequestOut(user.getId(), 0L));

                                        mImageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends_gold);
                                        Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_accepted), Snackbar.LENGTH_LONG).show();

                                    }else{

                                        FirebaseDAO.getInstance().areFriendRequestOut(user.getId(), mUserShown.getId(), new DataCallback<User.FriendRequestOut>() {
                                            @Override
                                            public void onDataReceive(User.FriendRequestOut friendOut) {
                                                if(friendOut == null){

                                                    User.FriendRequestOut friendRequestOut = new User.FriendRequestOut(mUserShown.getId(), 0L);
                                                    FirebaseDAO.getInstance().pushFriendRequestOut(user, friendRequestOut);
                                                    User.FriendRequestIn friendRequestIn = new User.FriendRequestIn(user.getId(), 0L);
                                                    FirebaseDAO.getInstance().pushFriendRequestIn(mUserShown ,friendRequestIn);

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
