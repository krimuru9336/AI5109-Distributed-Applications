package com.example.studentsinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList student_id, student_name;

    CustomAdapter(Context context, ArrayList student_id, ArrayList student_name) {
        this.context = context;
        this.student_id = student_id;
        this.student_name = student_name;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.student_id_txt.setText(String.valueOf(student_id.get(position)));
        holder.student_name_txt.setText(String.valueOf(student_name.get(position)));
    }

    @Override
    public int getItemCount() {
        return student_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView student_id_txt, student_name_txt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            student_id_txt = itemView.findViewById(R.id.student_id_txt);
            student_name_txt = itemView.findViewById(R.id.student_name_txt);
        }
    }
}
