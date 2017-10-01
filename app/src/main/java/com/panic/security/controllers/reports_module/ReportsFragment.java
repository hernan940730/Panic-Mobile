package com.panic.security.controllers.reports_module;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.User;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ImageConverter;
import com.panic.security.utils.ListAdapter;

import java.util.Map;

public class ReportsFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {

                showReports(user);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        return view;
    }

    public void showReports(User currentUser){

        ImageView imageViewWithoutReports = (ImageView) getView().findViewById(R.id.image_without_reports);
        TextView textViewWithoutReports = (TextView) getView().findViewById(R.id.txt_without_reports);

        imageViewWithoutReports.setVisibility(View.GONE);
        textViewWithoutReports.setVisibility(View.GONE);

        // List
        final ListView listViewReports = (ListView) getView().findViewById(R.id.list_view_reports);
        listViewReports.setVisibility(View.VISIBLE);
        final ListAdapter adapter = new ListAdapter(getActivity());

        if(currentUser.getReports() != null){

            for (Map.Entry<String, String> report : currentUser.getReports().entrySet()){
                /*FirebaseDAO.getInstance().getUserByID(report.getKey(), new DataCallback<User>() {
                    @Override
                    public void onDataReceive(final User user) {



                    }
                });*/
            }

            /*
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
            });*/

        }else{
            listViewReports.setVisibility(View.GONE);
            imageViewWithoutReports.setVisibility(View.VISIBLE);
            textViewWithoutReports.setVisibility(View.VISIBLE);
        }

    }

}
