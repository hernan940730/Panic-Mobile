package com.panic.security.controllers.friends_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.controllers.notifications_module.NotificationsFragment;
import com.panic.security.controllers.user_profile_module.UserProfileFragment;
import com.panic.security.entities.User;
import com.panic.security.firebase_utils.DataCallback;
import com.panic.security.firebase_utils.FirebaseDAO;
import com.panic.security.list_utils.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    // Search bar
    MaterialSearchView mSearchView;
    //List to search bar
    List<String> mListSource;

    ProgressBar mProgressBar;

    private Integer[] imgId = {
            R.mipmap.ic_account,
            R.mipmap.ic_add_person,
            R.mipmap.ic_are_friends,
            R.mipmap.ic_check_circle,
            R.mipmap.ic_account,
            R.mipmap.ic_add_person,
            R.mipmap.ic_are_friends,
            R.mipmap.ic_check_circle,
            R.mipmap.ic_edit,
            R.mipmap.ic_check_circle,
            R.mipmap.ic_edit
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

                FirebaseDAO.getInstance().getFriendsToUser(user.getId(), new DataCallback<HashMap<String, User.Friend>>() {
                    @Override
                    public void onDataReceive(HashMap<String, User.Friend> friends) {

                        // TODO get list of friends

                    }
                });

                // List
                ListAdapter adapter = new ListAdapter(getActivity(), imgId, lenguaje, description, false);

                ListView listViewFriends = (ListView) view.findViewById(R.id.list_view_friends);
                listViewFriends.setAdapter(adapter);

                /*listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String selectedItem = lenguaje[position];
                        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();

                    }
                });*/

            }
        });

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

}