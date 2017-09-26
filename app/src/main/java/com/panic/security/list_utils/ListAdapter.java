package com.panic.security.list_utils;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    public ListAdapter(Activity context, Integer[] imagesID, String[] itemsNames, String[] itemsDescriptions) {
        super(context, R.layout.row_list_view, itemsNames);

        this.context = context;
        this.imagesID = imagesID;
        this.itemsNames = itemsNames;
        this.itemsDescriptions = itemsDescriptions;
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_list_view, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_in_row);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.main_text_in_row);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.second_text_in_row);

        imageView.setImageResource(imagesID[position]);
        txtTitle.setText(itemsNames[position]);
        txtDescription.setText(itemsDescriptions[position]);

        return rowView;
    }

}
