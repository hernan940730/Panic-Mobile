package com.panic.security.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.panic.security.R;
import com.panic.security.entities.Crime;
import com.panic.security.entities.Report;
import com.panic.security.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maikb on 26/09/2017.
 */

public class ListAdapter extends ArrayAdapter<String>{

    private TextView txtDescription;
    private TextView txtTitle;
    private ImageView imageView;

    private ImageButton acceptRequest;
    private ImageButton rejectRequest;

    private AppCompatCheckBox checkBox;

    private Activity context;
    private List<User> users;
    private List<Crime> crimes;
    private List<Report> reports;
    private List<Bitmap> images;
    private List<String> itemsNames;
    private List<String> itemsDescription;
    private boolean withButtons;
    private boolean withCheckBox;

    public ListAdapter(Activity context){
        super(context, R.layout.row_list_view);
        this.context = context;
        crimes = new ArrayList<>();
        reports = new ArrayList<>();
        users = new ArrayList<>();
        images = new ArrayList<>();
        itemsNames = new ArrayList<>();
        itemsDescription = new ArrayList<>();
        withButtons = false;
        withCheckBox = false;
    }

    public User getUserByPosition(int i) {
        return users.get(i);
    }

    public Crime getCrimeByPosition(int i) {
        return crimes.get(i);
    }

    public Report geReportsByPosition(int i) {
        return reports.get(i);
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_list_view, null, true);

        imageView = rowView.findViewById(R.id.image_in_row);
        txtTitle = rowView.findViewById(R.id.main_text_in_row);
        txtDescription = rowView.findViewById(R.id.second_text_in_row);

        Bitmap bitmap = images.get(position);

        if (bitmap == null) {
            FirebaseDAO.getInstance().putProfileImageInView(users.get(position).getId(), context, imageView);
        }
        else {
            imageView.setImageBitmap(bitmap);
        }

        txtTitle.setText(itemsNames.get(position));
        txtDescription.setText(itemsDescription.get(position));

        acceptRequest = rowView.findViewById(R.id.accept_request);
        rejectRequest = rowView.findViewById(R.id.reject_request);

        if(!withButtons){
            acceptRequest.setVisibility(View.GONE);
            rejectRequest.setVisibility(View.GONE);
        }else{

            acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view, position, 0);
                }
            });

            rejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view, position, 0);
                }
            });
        }

        checkBox = rowView.findViewById(R.id.share_check_box);
        if(!withCheckBox){
            checkBox.setVisibility(View.GONE);
        }else{
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view, position, 0);
                }
            });
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return itemsNames.size();
    }

    public void addItem(boolean checkBoxes, User user, String email, String description) {
        add(email);
        users.add(user);
        images.add(null);
        itemsDescription.add(description);
        itemsNames.add(email);
        withCheckBox = checkBoxes;
    }

    public void addItem(User user, String email, String description) {
        users.add(user);
        images.add(null);
        itemsDescription.add(description);
        itemsNames.add(email);
    }

    public void addItem(User user, String name, String description, boolean buttons) {
        users.add(user);
        images.add(null);
        itemsNames.add(name);
        itemsDescription.add(description);
        withButtons = buttons;
    }

    public void addItem(Crime crime, String name, Report report, Bitmap image) {
        crimes.add(crime);
        reports.add(report);
        images.add(image);
        itemsNames.add(name);
        itemsDescription.add(report.getDescription());
    }

}