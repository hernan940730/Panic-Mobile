package com.panic.security.list_utils;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.panic.security.R;

/**
 * Created by maikb on 26/09/2017.
 */

public class ListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final Integer[] imagesID;
    private final String[] itemsNames;
    private final String[] itemsDescriptions;
    private final boolean withButtons;

    public ListAdapter(Activity context, Integer[] imagesID, String[] itemsNames, String[] itemsDescriptions, boolean withButtons) {
        super(context, R.layout.row_list_view, itemsNames);

        this.context = context;
        this.imagesID = imagesID;
        this.itemsNames = itemsNames;
        this.itemsDescriptions = itemsDescriptions;
        this.withButtons = withButtons;

    }

    public View getView(final int position, View view, final ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_list_view, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_in_row);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.main_text_in_row);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.second_text_in_row);

        imageView.setImageResource(imagesID[position]);
        txtTitle.setText(itemsNames[position]);
        txtDescription.setText(itemsDescriptions[position]);

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

}
