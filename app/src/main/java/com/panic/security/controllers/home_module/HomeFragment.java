package com.panic.security.controllers.home_module;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.panic.security.FirebaseDAO;
import com.panic.security.FirebaseReferences;
import com.panic.security.R;

public class HomeFragment extends Fragment {

    private TextView mHomeTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeTV = view.findViewById(R.id.home_message);
        showHomeMessage();

        FirebaseDAO fdao = new FirebaseDAO();
        System.out.println("user email: " + fdao.getUserByUID("-KtKjRp0KhyFZ0SoqqM4"));

        // Inflate the layout for this fragment
        return view;

    }

    public void showHomeMessage () {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseReferences.MESSAGE_REFERENCE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                mHomeTV.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

}
