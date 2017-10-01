package com.panic.security.controllers.notifications_module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.panic.security.R;
import com.panic.security.utils.ListAdapter;

public class NotificationsFragment extends Fragment {

    private Integer[] imgId = {
            R.mipmap.ic_account,
            R.mipmap.ic_add_person,
            R.mipmap.ic_are_friends,
            R.mipmap.ic_edit,
            R.mipmap.ic_exit,
    };

    private String lenguaje[] = {"Maikol Bonilla","Maikol Bonilla","Maikol Bonilla","Maikol Bonilla","Maikol Bonilla"};
    private String description[] = {"Quiere ser tu amigo","Quiere ser tu amigo","Quiere ser tu amigo","Quiere ser tu amigo","Quiere ser tu amigo"};


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        ListAdapter adapter = new ListAdapter(getActivity(), imgId, lenguaje, description, true);

        ListView listViewFriends = (ListView) view.findViewById(R.id.list_view_friends);
        listViewFriends.setAdapter(adapter);

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int viewId = view.getId();
                switch (viewId) {
                    case R.id.accept_request:
                        Toast.makeText(getActivity(), "Boton accepted: " + position, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.reject_request:
                        Toast.makeText(getActivity(), "Boton reject: " + position, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        //item in list
                        String selectedItem = lenguaje[position];
                        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        return view;
    }

}
