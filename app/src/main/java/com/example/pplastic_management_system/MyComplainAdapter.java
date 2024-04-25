package com.example.pplastic_management_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.pplastic_management_system.R;

import java.util.ArrayList;

public class MyComplainAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;
    private ArrayList<String> mObjects;

    public MyComplainAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        // Get the data item for this position
        String item = mObjects.get(position);

        // Get references to views
        TextView textViewUser = convertView.findViewById(R.id.text_view_user);

        // Set the text of textViewUser
        textViewUser.setText(item);

        // Set background color of CardView
        CardView cardView = convertView.findViewById(R.id.card_view_user);
        if (position == 0) {
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.gray));
        } else {
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.gray));
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }
}
