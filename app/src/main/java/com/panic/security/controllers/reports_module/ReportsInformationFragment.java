package com.panic.security.controllers.reports_module;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.controllers.main_module.MainActivity;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Location;
import com.panic.security.entities.Report;
import com.panic.security.utils.CrimesConverter;
import com.panic.security.utils.DataCallback;
import com.panic.security.utils.FirebaseDAO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsInformationFragment extends Fragment {

    public static final String CRIME_BUNDLE = "CRIME_ID";
    public static final String REPORT_BUNDLE = "REPORT_ID";

    private Crime mCurrentCrime;
    private Report mCurrentReport;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().containsKey(CRIME_BUNDLE) && getArguments().containsKey(REPORT_BUNDLE)) {
            mCurrentCrime = (Crime)getArguments().getSerializable(CRIME_BUNDLE);
            mCurrentReport = (Report)getArguments().getSerializable(REPORT_BUNDLE);
            showReportInformation();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports_information, container, false);
    }

    public void showReportInformation(){

        TextView reportInfoDate = getView().findViewById(R.id.report_info_date);
        TextView reportInfoType = getView().findViewById(R.id.report_info_type);
        TextView reportInfoDescription = getView().findViewById(R.id.report_info_description);
        final TextView reportInfoLatitude = getView().findViewById(R.id.report_info_latitude);
        final TextView reportInfoLongitude = getView().findViewById(R.id.report_info_longitude);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        reportInfoDate.setText(simpleDateFormat.format(new Date(mCurrentReport.getDate())));
        reportInfoType.setText(CrimesConverter.converter(getActivity(), mCurrentCrime.getType()));
        reportInfoDescription.setText(mCurrentReport.getDescription());

        FirebaseDAO.getInstance().getLocationByID(mCurrentCrime.getLocation_id(), new DataCallback<Location>() {
            @Override
            public void onDataReceive(final Location location) {
                reportInfoLatitude.setText(String.valueOf(location.getLatitude()));
                reportInfoLongitude.setText(String.valueOf(location.getLongitude()));

                Button goToLocationButton = getView().findViewById(R.id.go_to_location_button);

                goToLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (location != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder ().
                                    target (new LatLng(location.getLatitude (), location.getLongitude ())).
                                    zoom (17).
                                    build ();
                            MainActivity activity = (MainActivity) getActivity();
                            activity.moveCamera(cameraPosition, false);
                            FragmentManager fragmentManager;
                            fragmentManager = activity.getSupportFragmentManager();
                            //fragmentManager.popBackStack(); TODO
                            fragmentManager.beginTransaction().replace (R.id.content_main, new Fragment()).commit();
                        }
                    }
                });
            }
        });

    }
}
