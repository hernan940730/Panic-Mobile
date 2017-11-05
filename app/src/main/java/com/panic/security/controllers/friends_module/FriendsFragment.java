package com.panic.security.controllers.friends_module;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Friend;
import com.panic.security.entities.FriendRequest;
import com.panic.security.entities.Profile;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ListAdapter;

import java.util.Map;

public class FriendsFragment extends Fragment {

    private ListView listViewFriends;
    private ListView listViewRequest;

    private ImageView imageViewWithoutFriends;
    private TextView textViewWithoutFriends;

    private TextView textViewRequest;
    private TextView textViewFriend;

    private ListAdapter friendsAdapter;
    private ListAdapter requestsAdapter;

    private ChildEventListener friendsChildListener;
    private ChildEventListener requestsChildListener;

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewFriends = getView().findViewById(R.id.list_view_friends);
        listViewRequest = getView().findViewById(R.id.list_view_request);

        imageViewWithoutFriends = getView().findViewById(R.id.image_without_friends);
        textViewWithoutFriends = getView().findViewById(R.id.txt_without_friends);

        textViewRequest = getView().findViewById(R.id.txt_view_request);
        textViewFriend = getView().findViewById(R.id.txt_friends);

        friendsAdapter = new ListAdapter(getActivity());
        requestsAdapter = new ListAdapter(getActivity());

        listViewFriends.setAdapter(friendsAdapter);
        listViewRequest.setAdapter(requestsAdapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseDAO.getInstance().getUserByID(friendsAdapter.getUserByPosition(position).getId(),
                    new DataCallback<User>() {
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

        listViewRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User petitioner = requestsAdapter.getUserByPosition(position);
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

        friendsChildListener = FirebaseDAO.getInstance().addUserFriendsChildListener(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new DataCallback<Friend>() {
                    @Override
                    public void onDataReceive(Friend friend) {
                        addItemToAdapter(listViewFriends, friendsAdapter, textViewFriend,
                                friend.getUser_id(), View.GONE, false);
                    }
                }
        );

        requestsChildListener = FirebaseDAO.getInstance().addUserFriendRequestsInChildListener(
                new DataCallback<FriendRequest>() {
                    @Override
                    public void onDataReceive(FriendRequest friendRequest) {
                        addItemToAdapter(listViewRequest, requestsAdapter, textViewRequest,
                                friendRequest.getUser_id(), View.GONE, true);
                    }
                }
        );
    }

    private void addItemToAdapter(ListView listView, final ListAdapter adapter, TextView title,
                                  String userId, int noFriendsVisibility, final boolean options) {
        listView.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);

        imageViewWithoutFriends.setVisibility(noFriendsVisibility);
        textViewWithoutFriends.setVisibility(noFriendsVisibility);

        FirebaseDAO.getInstance().getUserByID(userId, new DataCallback<User>() {
            @Override
            public void onDataReceive(final User user) {
                FirebaseDAO.getInstance().getProfileByID(user.getProfile_id(), new DataCallback<Profile>() {
                    @Override
                    public void onDataReceive(final Profile profile) {
                        if (profile != null) {
                            adapter.addItem(user, (profile.getName() + " " + profile.getLast_name()), user.getEmail(), options);
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        setHasOptionsMenu(true); // Call onCreateOptionsMenu
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MaterialSearchView mSearchView = getActivity().findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        mSearchView.setMenuItem(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (friendsChildListener != null) {
            FirebaseDAO.getInstance()
                    .revokeUserFriendsChildListener(
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            friendsChildListener
                    );
        }
        if (requestsChildListener != null) {
            FirebaseDAO.getInstance()
                    .revokeUserFriendRequestsInChildListener(
                            requestsChildListener
                    );
        }
    }
}
