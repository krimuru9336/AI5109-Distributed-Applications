package com.example.distributedapplicationsproject.android;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.distributedapplicationsproject.models.User;

import java.util.List;

public class UserArrayAdapter extends ArrayAdapter<User> {

    public UserArrayAdapter(@NonNull Context context, int resource, @NonNull List<User> users) {
        super(context, resource, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        view.setText(getItem(position).getName());
        return view;
    }

}
