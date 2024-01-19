package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Models.AllMethods;
import Models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    Context context;
    ArrayList<Message> messages;
    DatabaseReference reference;

    public  MessageAdapter(Context c, ArrayList<Message> msgs,DatabaseReference db){
        context = c;
        messages = msgs;
        reference = db;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_view,parent,false);
        return  new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message m = messages.get(position);
        if(m.getName().equals(AllMethods.name)){
            holder.textView.setText("You:"+m.getText());
            holder.textView_time.setText(m.getTime());
            holder.li.setGravity(Gravity.END);
            holder.li.setBackgroundColor(Color.parseColor("#7B5088"));
        }else {
            holder.textView.setText(m.getName() + ":"+m.getText());
            holder.textView_time.setText(m.getTime());
            holder.li.setGravity(Gravity.START);
            holder.delButton.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textView_time;
        ImageButton delButton;
        LinearLayout li;

        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvTitle);
            textView_time = itemView.findViewById(R.id.tvTime);
            delButton = itemView.findViewById(R.id.imDelBtn);
            li = itemView.findViewById(R.id.l1Message);
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child(messages.get(getBindingAdapterPosition()).getKey()).removeValue();
                }
            });
        }
    }
}
