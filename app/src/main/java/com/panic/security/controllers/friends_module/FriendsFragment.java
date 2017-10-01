package com.panic.security.controllers.friends_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    // Search bar
    MaterialSearchView mSearchView;
    //List to search bar
    List<String> mListSource;

    ProgressBar mProgressBar;

    private Integer[] imgId = {
            R.mipmap.ic_account,
    };
    private String lenguaje[] = {"Java","PHP","Python","JavaScript","Ruby","C","Go","Perl","Pascal","Maikol","Ada"};
    private String description[] = {"DssaJava","PasdasdHP","Pytasdhasodn","JaasdvaadSascdrasdipt","Rasduasdbasy","Casd","Gadasdsao","Peadasdasdrl","Pasdaasscal","Maikoasdasdl","Adasdasda"};


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar) view.findViewById(R.id.friends_progress_bar);

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {

                // Bar search
                addSearchBar();
                // Show friends
                showFriends(user);
                showNotifications(user);

            }
        });

    }

    public void showFriends(User user){

        // List
        final ListView listViewFriends = (ListView) getView().findViewById(R.id.list_view_request);
        final ListAdapter adapter = new ListAdapter(getActivity());

        if(user.getFriends() != null){

            for (Map.Entry<String, User.Friend> friend : user.getFriends().entrySet()){
                FirebaseDAO.getInstance().getUserByID(friend.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {

                        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                            @Override
                            public void onDataReceive(Profile profile) {

                                Integer image = new Integer(R.mipmap.ic_account);
                                adapter.addItem(user.getId(), (profile.getName() + " " + profile.getLast_name()), user.getEmail(), image);
                                listViewFriends.setAdapter(adapter);

                                // Event when one item is selected
                                listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        FirebaseDAO.getInstance().getUserByID(adapter.getUserIDs().get(position), new DataCallback<User>() {
                                            @Override
                                            public void onDataReceive(User userSelected) {
                                                //To send user selected from fragment to activity
                                                Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                                                intent.putExtra("type", "list_friends");
                                                intent.putExtra("user_selected", userSelected);
                                                getActivity().startActivity(intent);
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                });
            }

        }else{
            ImageView imageViewWithoutFriends = (ImageView) getView().findViewById(R.id.image_without_friends);
            TextView textViewWithoutFriends = (TextView) getView().findViewById(R.id.txt_without_friends);

            imageViewWithoutFriends.setVisibility(View.VISIBLE);
            textViewWithoutFriends.setVisibility(View.VISIBLE);
        }

    }

    public void showNotifications(User user){

        // List
        final ListView listViewFriends = (ListView) getView().findViewById(R.id.list_view_friends);
        final ListAdapter adapterNotifications = new ListAdapter(getActivity());

        adapterNotifications.addItem(user.getId(), "Test" , "Description", 0);
        listViewFriends.setAdapter(adapterNotifications);

        /*if(user.getFriends() != null){

            for (Map.Entry<String, User.Friend> friend : user.getFriends().entrySet()){
                FirebaseDAO.getInstance().getUserByID(friend.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {

                        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                            @Override
                            public void onDataReceive(Profile profile) {

                                Integer image = new Integer(R.mipmap.ic_account);
                                adapterNotifications.addItem(user.getId(), (profile.getName() + " " + profile.getLast_name()), user.getEmail(), image);
                                listViewFriends.setAdapter(adapterNotifications);

                                // Event when one item is selected
                                listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        FirebaseDAO.getInstance().getUserByID(adapterNotifications.getUserIDs().get(position), new DataCallback<User>() {
                                            @Override
                                            public void onDataReceive(User userSelected) {
                                                //To send user selected from fragment to activity
                                                Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                                                intent.putExtra("type", "list_friends");
                                                intent.putExtra("user_selected", userSelected);
                                                getActivity().startActivity(intent);
                                            }
                                        });

                                    }
                                });

                            }
                        });

                    }
                });
            }

        }else{
            ImageView imageViewWithoutFriends = (ImageView) getView().findViewById(R.id.image_without_friends);
            TextView textViewWithoutFriends = (TextView) getView().findViewById(R.id.txt_without_friends);

            imageViewWithoutFriends.setVisibility(View.VISIBLE);
            textViewWithoutFriends.setVisibility(View.VISIBLE);
        }*/

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

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

                        //To send user selected from fragment to activity
                        Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                        intent.putExtra("type", "query");
                        intent.putExtra("user_in_search", user);
                        getActivity().startActivity(intent);
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

}
