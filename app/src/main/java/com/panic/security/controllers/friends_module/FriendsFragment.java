package com.panic.security.controllers.friends_module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Friend;
import com.panic.security.entities.FriendRequest;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.DataLoader;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ImageConverter;
import com.panic.security.utils.ListAdapter;

import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    MaterialSearchView mSearchView;
    // ImageToAddFriend

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDAO.getInstance().getUserFriends(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new DataCallback<Map<String, Friend>>() {
                    @Override
                    public void onDataReceive(Map<String, Friend> data) {
                        showFriends (data);
                    }
                }
        );

        FirebaseDAO.getInstance().getUserFriendRequestsIn(new DataCallback<Map<String, FriendRequest>>() {
            @Override
            public void onDataReceive(Map<String, FriendRequest> data) {
                // Show friends
                showFriendRequest(data);
            }
        });

    }

    public void showFriends(Map<String, Friend> friends){

        ImageView imageViewWithoutFriends = (ImageView) getView().findViewById(R.id.image_without_friends);
        TextView textViewWithoutFriends = (TextView) getView().findViewById(R.id.txt_without_friends);

        imageViewWithoutFriends.setVisibility(View.GONE);
        textViewWithoutFriends.setVisibility(View.GONE);

        // List
        final ListView listViewFriends = (ListView) getView().findViewById(R.id.list_view_friends);
        listViewFriends.setVisibility(View.VISIBLE);
        final ListAdapter adapter = new ListAdapter(getActivity());

        if(friends != null){

            for (Map.Entry<String, Friend> friend : friends.entrySet()){
                FirebaseDAO.getInstance().getUserByID(friend.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {

                        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                            @Override
                            public void onDataReceive(final Profile profile) {
                                adapter.addItem(user, (profile.getName() + " " + profile.getLast_name()), user.getEmail());
                                listViewFriends.setAdapter(adapter);
                            }
                        });

                    }
                });
            }

            // Event when one item is selected
            listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    FirebaseDAO.getInstance().getUserByID(adapter.getUsers().get(position).getId(), new DataCallback<User>() {
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

        }else{
            listViewFriends.setVisibility(View.GONE);
            imageViewWithoutFriends.setVisibility(View.VISIBLE);
            textViewWithoutFriends.setVisibility(View.VISIBLE);
        }

    }

    public void showFriendRequest(final Map<String, FriendRequest> friendRequestsIn){

        // List
        final TextView textViewRequest = (TextView) getView().findViewById(R.id.txt_view_request);
        final ListView listViewRequest = (ListView) getView().findViewById(R.id.list_view_request);
        final ListAdapter adapterNotifications = new ListAdapter(getActivity());

        if (friendRequestsIn != null) {

            textViewRequest.setVisibility(View.VISIBLE);
            listViewRequest.setVisibility(View.VISIBLE);

            for (Map.Entry<String, FriendRequest> friendRequestIn : friendRequestsIn.entrySet()){
                FirebaseDAO.getInstance().getUserByID(friendRequestIn.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {
                        FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                            @Override
                            public void onDataReceive(final Profile profile) {
                                adapterNotifications.addItem(user, (profile.getName() + " " + profile.getLast_name()), user.getEmail(), true);
                                listViewRequest.setAdapter(adapterNotifications);
                            }
                        });
                    }
                });
            }

            // Event when one button is selected
            listViewRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    User petitioner = adapterNotifications.getUsers().get(position);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    int viewId = view.getId();

                    switch (viewId) {
                        case R.id.accept_request:
                            FirebaseDAO.getInstance().pushFriend(petitioner.getId());
                            FirebaseDAO.getInstance().removeFriendRequest(petitioner.getId());

                            Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_accepted), Snackbar.LENGTH_LONG).show();
                            fragmentManager.beginTransaction().replace (R.id.content_main, new FriendsFragment()).commit();
                            break;
                        case R.id.reject_request:
                            FirebaseDAO.getInstance().removeFriendRequest(petitioner.getId());

                            Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), getResources().getString(R.string.friend_request_rejected), Snackbar.LENGTH_LONG).show();
                            fragmentManager.beginTransaction().replace (R.id.content_main, new FriendsFragment()).commit();
                            break;
                        default:
                            //it is not called never
                            break;
                    }
                }
            });

        } else {
            textViewRequest.setVisibility(View.GONE);
            listViewRequest.setVisibility(View.GONE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        setHasOptionsMenu(true); // Call onCreateOptionsMenu
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MaterialSearchView mSearchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        mSearchView.setMenuItem(item);
    }

}
