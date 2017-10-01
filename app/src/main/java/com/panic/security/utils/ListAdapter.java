package com.panic.security.utils;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.panic.security.R;
import com.panic.security.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maikb on 26/09/2017.
 */

public class ListAdapter extends ArrayAdapter<String>{

    private Activity context;
    private List<User> users;
    private List<Integer> imagesID;
    private List<String> itemsNames;
    private boolean withButtons;

    public ListAdapter(Activity context){
        super(context, R.layout.row_list_view);
        this.context = context;
        users = new ArrayList<>();
        imagesID = new ArrayList<>();
        itemsNames = new ArrayList<>();
        withButtons = false;
    }

    public ListAdapter(Activity context, List<User> users, List<Integer> imagesID, List<String> itemsNames, List<String> itemsDescriptions, List<List<ImageButton>> itemsButtons) {
        super(context, R.layout.row_list_view, itemsNames);

        this.context = context;
        this.users = users;
        this.imagesID = imagesID;
        this.itemsNames = itemsNames;
        withButtons = false;
    }

    public List<User> getUsers() {
        return users;
    }

    public View getView(final int position, View view, final ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_list_view, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_in_row);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.main_text_in_row);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.second_text_in_row);

        imageView.setImageResource(imagesID.get(position));
        txtTitle.setText(itemsNames.get(position));
        txtDescription.setText(users.get(position).getEmail());

        ImageButton acceptRequest = (ImageButton) rowView.findViewById(R.id.accept_request);
        ImageButton rejectRequest = (ImageButton) rowView.findViewById(R.id.reject_request);

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

        return rowView;
    }

    @Override
    public int getCount() {
        return itemsNames.size();
    }

    public void addItem(User user, String name, Integer image) {
        users.add(user);
        imagesID.add(image);
        itemsNames.add(name);
    }

    public void addItem(User user, String name, Integer image, boolean buttons) {
        users.add(user);
        imagesID.add(image);
        itemsNames.add(name);
        withButtons = buttons;
    }

}
