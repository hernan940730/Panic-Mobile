package com.panic.security.controllers.reports_module;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.panic.security.controllers.friends_module.FriendsFragment;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Profile;
import com.panic.security.entities.Report;
import com.panic.security.entities.User;
import com.panic.security.utils.CrimesConverter;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;
import com.panic.security.utils.ImageConverter;
import com.panic.security.utils.ListAdapter;

import java.util.HashMap;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private static String CRIMES_LIST[] = {
            "assault_crime",
            "auto_theft_crime",
            "burglary_crime",
            "shop_lifting_crime",
            "suspicious_activity_crime",
            "homicide_crime",
            "vandalism_crime",
            "drugs_crime",
            "other_crime"
    };

    private FirebaseAuth mAuth;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDAO.getInstance().getUserByID(mAuth.getCurrentUser().getUid(), new DataCallback<User>() {
            @Override
            public void onDataReceive(User user) {
                FirebaseDAO.getInstance().getUserReports(mAuth.getCurrentUser().getUid(),
                        new DataCallback<Map<String, String>>() {
                    @Override
                    public void onDataReceive(Map<String, String> data) {
                        showReports(data);
                    }
                });

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        return view;
    }

    public void showReports(final Map<String, String> reports){

        ImageView imageViewWithoutReports = (ImageView) getView().findViewById(R.id.image_without_reports);
        TextView textViewWithoutReports = (TextView) getView().findViewById(R.id.txt_without_reports);

        imageViewWithoutReports.setVisibility(View.GONE);
        textViewWithoutReports.setVisibility(View.GONE);

        // List
        final ListView listViewReports = (ListView) getView().findViewById(R.id.list_view_reports);
        listViewReports.setVisibility(View.VISIBLE);
        final ListAdapter adapterReports = new ListAdapter(getActivity());

        if(reports != null){

            for (Map.Entry<String, String> report : reports.entrySet()){
                FirebaseDAO.getInstance().getReportByID(report.getKey(), new DataCallback<Report>() {
                    @Override
                    public void onDataReceive(final Report report) {

                        FirebaseDAO.getInstance().getCrimeByID(report.getCrime_id(), new DataCallback<Crime>() {
                            @Override
                            public void onDataReceive(Crime crime) {
                                String type = crime.getType();
                                if(type.equals(CRIMES_LIST[0])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_assault));
                                }else if(type.equals(CRIMES_LIST[1])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_auto_theft));
                                }else if(type.equals(CRIMES_LIST[2])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_burglary));
                                }else if(type.equals(CRIMES_LIST[3])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_shoplifting));
                                }else if(type.equals(CRIMES_LIST[4])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_suspicious_activity));
                                }else if(type.equals(CRIMES_LIST[5])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_homicide));
                                }else if(type.equals(CRIMES_LIST[6])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_vandalism));
                                }else if(type.equals(CRIMES_LIST[7])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_drugs));
                                }else if(type.equals(CRIMES_LIST[8])){
                                    adapterReports.addItem(CrimesConverter.converter(getActivity(),crime.getType()),
                                            report.getDescription(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_other));
                                }

                                listViewReports.setAdapter(adapterReports);

                            }
                        });

                    }
                });

            }
            // Event when one item is selected
            /*
            listViewReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListAdapter listAdapter = (ListAdapter) listViewReports.getItemAtPosition(position);
                    adapterReports.getTxtDescription().setMaxLines(100);
                    adapterReports.getTxtDescription().setEllipsize(null);
                    listViewReports.setAdapter(adapterReports);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace (R.id.content_main, new ReportsFragment()).commit();
                }
            });
            */
        } else {
            listViewReports.setVisibility(View.GONE);
            imageViewWithoutReports.setVisibility(View.VISIBLE);
            textViewWithoutReports.setVisibility(View.VISIBLE);
        }

    }

}
