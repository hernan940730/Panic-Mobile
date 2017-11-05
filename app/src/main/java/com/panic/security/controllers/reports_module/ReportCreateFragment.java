package com.panic.security.controllers.reports_module;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

    private static Integer[] iconsDatabase = {
            R.mipmap.ic_assault,
            R.mipmap.ic_auto_theft,
            R.mipmap.ic_burglary,
            R.mipmap.ic_drugs,
            R.mipmap.ic_homicide,
            R.mipmap.ic_shoplifting,
            R.mipmap.ic_suspicious_activity,
            R.mipmap.ic_vandalism,
            R.mipmap.ic_other
    };

    private static Integer[] crimesDatabase = {
            R.string.assault_crime,
            R.string.auto_theft_crime,
            R.string.burglary_crime,
            R.string.drugs_crime,
            R.string.homicide_crime,
            R.string.shop_lifting_crime,
            R.string.suspicious_activity_crime,
            R.string.vandalism_crime,
            R.string.other_crime
    };

    @Override
    public void onCreate( Bundle savedInstaceState ) {
        super.onCreate(savedInstaceState);
        if( savedInstaceState != null ){
            latitude = savedInstaceState.getDouble( "marker_lat" );
            longitude = savedInstaceState.getDouble( "marker_lon" );
        }
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

        //Button date = ( Button )view.findViewById( R.id.crime_date );

        //date.setText( String.valueOf( day ) + "/" + String.valueOf( month ) + "/" + String.valueOf( year ) );
        return view;
    }

    private void commitCrime( String input, long timeInMillis ) {
        String crimeType = crimesSpinner.getSelectedItem().toString();
        com.panic.security.entities.Location location = new com.panic.security.entities.Location();

        location.setLatitude( latitude );
        location.setLongitude( longitude );

        Crime crime = new Crime();
        crime.setType( crimeType );
        crime.setDate( timeInMillis );

        Report report = new Report();
        report.setDescription( input );

        FirebaseDAO.getInstance().pushReport(report, crime, location);
        getActivity().getFragmentManager().popBackStack();
    }
}
