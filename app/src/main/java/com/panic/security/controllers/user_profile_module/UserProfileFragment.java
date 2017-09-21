package com.panic.security.controllers.user_profile_module;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    //List to search bar
    List<String> mListSource;

    private FirebaseAuth mAuth;



    // Search bar
    MaterialSearchView mSearchView;
    ListView mListViewSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        addSearchBar();
        mAuth = FirebaseAuth.getInstance();


        showUserData(mAuth.getCurrentUser().getUid());
        //actionEdit(view);
        actionAddFriend(view);

        // Inflate the layout for this fragment
        return view;
    }

    public void fillListToSearch(){

        mListSource = new ArrayList<String>();

        FirebaseDAO.getInstance().getAllFullNamesForAllUsers(new DataCallback<List<String>>() {
            @Override
            public void onDataReceive(List<String> usersNames) {
                mListSource = usersNames;
            }
        });
    }

    public void addSearchBar(){

        mSearchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        mListViewSearch =  (ListView) getActivity().findViewById(R.id.lst_view_search);

        fillListToSearch();

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mListViewSearch.setVisibility(View.VISIBLE);
                List<String> listFriends = new ArrayList<String>();
                listFriends.add("Friend1");
                listFriends.add("Friend2");
                listFriends.add("Friend3");
                listFriends.add("HernanEsUnaFufa");
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listFriends);
                mListViewSearch.setAdapter(adapter);
            }

            @Override
            public void onSearchViewClosed() {
                // If close search view, search_view will return default
                List<String> listEmpty = new ArrayList<String>();
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listEmpty);
                mListViewSearch.setAdapter(adapter);
                mListViewSearch.setVisibility(View.GONE);
            }
        });

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                mListViewSearch.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    List<String> listFound = new ArrayList<String>();
                    for(String item : mListSource){
                        if(item.toLowerCase().contains(newText.toLowerCase())){
                            listFound.add(item);
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listFound);
                    mListViewSearch.setAdapter(adapter);
                }else{
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mListSource);
                    mListViewSearch.setAdapter(adapter);
                }
                return true;
            }
        });

        setHasOptionsMenu(true); //To show search bar, call onCreateOptionsMenu
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

        final ImageView imageViewUserProfileAddFriend = (ImageView) view.findViewById(R.id.user_profile_add_friend);

        imageViewUserProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO If not they are not friends
                    Toast.makeText(getActivity(), "Friend request sent", Toast.LENGTH_SHORT).show();
                    imageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                // TODO if request friends was sent
                    imageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_check_circle);
                // TODO if they are friends
                    imageViewUserProfileAddFriend.setImageResource(R.mipmap.ic_are_friends);

            }
        });

    }

}
