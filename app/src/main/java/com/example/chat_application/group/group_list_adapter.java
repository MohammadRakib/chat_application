package com.example.chat_application.group;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.chat_application.Data.group_data;
import com.example.chat_application.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;

public class group_list_adapter extends FirebaseRecyclerAdapter<group_data,group_list_adapter.group_list_viewHolder> {

   private group_list_interface group_list_interface;
   private Context context;



    group_list_adapter(@NonNull FirebaseRecyclerOptions<group_data> options, Context context, group_list_interface group_list_interface) {
        super(options);
        this.context = context;
        this.group_list_interface = group_list_interface;
    }

    @Override
    protected void onBindViewHolder(@NonNull final group_list_viewHolder holder, int position, @NonNull final group_data model) {

        holder.group_name.setText(model.getGroupName());
        if(model.getGroupImage() != null){

            Glide.with(context)
                    .load(model.getGroupImage())
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .dontAnimate()
                    .placeholder(R.drawable.dummyimage)
                    .into(holder.group_image);
        }

       /* if(model.getLastMessage() != null){

            String UserName = model.getLastmsgUserName();
            String message =  model.getLastMessage();
            String time = model.getLastmsgTime();
            Long tm = Long.valueOf(time);
            String concatMessage = UserName+": "+message;
            holder.last_message.setText(concatMessage);
            c.setTimeInMillis(tm*1000);
            date_time =  DateFormat.format("MMM d yyyy, h:mm a",c).toString();
            holder.time.setText(date_time);

        }*/



        holder.group_list_row_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               group_list_interface.onclick_group_list(model);

            }
        });

    }

    @NonNull
    @Override
    public group_list_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_row,parent,false);
        return new group_list_viewHolder(v);
    }

    static class group_list_viewHolder extends RecyclerView.ViewHolder{
        CircleImageView group_image;
        TextView group_name, last_message, time;
        MaterialCardView group_list_row_cardView;


        group_list_viewHolder(@NonNull View itemView) {
            super(itemView);
           /* group_image = itemView.findViewById(R.id.groupimageview);
            group_name = itemView.findViewById(R.id.groupnametextview);*/
            group_image = itemView.findViewById(R.id.owngroupimageview);
            group_name = itemView.findViewById(R.id.owngroupnametextview);
           /* group_list_row_cardView = itemView.findViewById(R.id.group_list_recycle_view_row_card_id);*/
            group_list_row_cardView = itemView.findViewById(R.id.group_vertical_parent_recycle_view_row_card_id);
            last_message = itemView.findViewById(R.id.last_group_message);
            time = itemView.findViewById(R.id.time_messege);

        }
    }

}
