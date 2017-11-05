package com.panic.security.controllers.reports_module;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.panic.security.R;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Report;
import com.panic.security.utils.FirebaseDAO;

import java.util.Calendar;

public class ReportCreateFragment extends Fragment {

    private final Calendar c = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private Spinner crimesSpinner;
    private TextView crimeDate;
    private EditText mDescriptiontext;
    private double latitude, longitude;

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

    @Override
    public void onCreate( Bundle savedInstaceState ) {
        super.onCreate(savedInstaceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int year = c.get( Calendar.YEAR );
        int month = c.get( Calendar.MONTH );
        int day = c.get( Calendar.DAY_OF_MONTH );

        Bundle bundle = getArguments();
        latitude = bundle.getDouble( "marker_lat" );
        longitude = bundle.getDouble( "marker_lon" );

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_report_create, container, false);

        mDescriptiontext = view.findViewById( R.id.crime_description );

        crimesSpinner = view.findViewById( R.id.crimes_spinner );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( view.getContext(), R.array.crimes_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        crimesSpinner.setAdapter( adapter );
        crimeDate = view.findViewById( R.id.report_crime_date );

        crimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int year = c.get( Calendar.YEAR );
                int month = c.get( Calendar.MONTH );
                int day = c.get( Calendar.DAY_OF_MONTH );

                final DatePickerDialog datePickerDialog = new DatePickerDialog( view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        String newDate = String.valueOf( day ) + "/" + String.valueOf( month ) + "/" + String.valueOf( year );
                        crimeDate.setText( newDate );
                    }
                }, year, month, day);

                datePickerDialog.getDatePicker().setMaxDate( c.getTimeInMillis() );
                datePickerDialog.show();
            }
        });
        String currDate = day + "/" + month + "/" + year;
        crimeDate.setText( currDate );

        view.findViewById( R.id.commit_crime_button ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String input = mDescriptiontext.getText().toString();
                if( !TextUtils.isEmpty( input ) ) {
                    commitCrime( input, c.getTimeInMillis() );
                    Toast.makeText( view.getContext(), R.string.report_done, Toast.LENGTH_LONG ).show();
                }else{
                    mDescriptiontext.setError( getResources().getString( R.string.required ) );
                }
            }
        });

        return view;
    }

    private void commitCrime( String input, long timeInMillis ) {
        String selectedCrime = crimesSpinner.getSelectedItem().toString();
        com.panic.security.entities.Location location = new com.panic.security.entities.Location();
        String crimeType = "";

        if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 0 ) ) ){
            crimeType = CRIMES_LIST[0];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 1 ) ) ){
            crimeType = CRIMES_LIST[1];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 2 ) ) ){
            crimeType = CRIMES_LIST[2];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 3 ) ) ){
            crimeType = CRIMES_LIST[3];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 4 ) ) ){
            crimeType = CRIMES_LIST[4];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 5 ) ) ){
            crimeType = CRIMES_LIST[5];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 6 ) ) ){
            crimeType = CRIMES_LIST[6];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 7 ) ) ){
            crimeType = CRIMES_LIST[7];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 8 ) ) ){
            crimeType = CRIMES_LIST[8];
        }else if( selectedCrime.equals( crimesSpinner.getItemAtPosition( 9 ) ) ){
            crimeType = CRIMES_LIST[9];
        }

        location.setLatitude( latitude );
        location.setLongitude( longitude );

        Crime crime = new Crime();
        crime.setType( crimeType );
        crime.setDate( timeInMillis );

        Report report = new Report();
        report.setDescription( input );

        FirebaseDAO.getInstance().pushReport(report, crime, location);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
