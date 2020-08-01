package com.example.chat_application.group;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.chat_application.Data.group_data;
import com.example.chat_application.Data.yourGroupData;
import com.example.chat_application.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class myProfile_adapter extends RecyclerView.Adapter<myProfile_adapter.myProfile_viewHolder> {

    private Context context;
    private owngroupInterface owngroupInterface;
    private List<yourGroupData> groupDataList;
    private final Calendar c = Calendar.getInstance(Locale.getDefault());
    private String date_time;

    myProfile_adapter(Context context, owngroupInterface owngroupInterface, List<yourGroupData> groupDataList) {
        this.context = context;
        this.owngroupInterface = owngroupInterface;
        this.groupDataList = groupDataList;
    }

    @NonNull
    @Override
    public myProfile_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_row,parent,false);
        return new myProfile_viewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull myProfile_viewHolder holder, final int position) {

       final yourGroupData groupData = groupDataList.get(position);

        holder.group_name.setText(groupData.getGroupName());

        if(groupData.getLastMessage() != null){

            String UserName = groupData.getLastmsgUserName();
            String message =  groupData.getLastMessage();
            String time = groupData.getLastmsgTime();

            long tm = Long.parseLong(time);

            String concatMessage = UserName+": "+message;
            holder.last_message.setText(concatMessage);

            c.setTimeInMillis(tm*1000);
            date_time =  DateFormat.format("MMM d yyyy, h:mm a",c).toString();
            holder.time.setText(date_time);


        }else {
            holder.last_message.setText("");
            holder.time.setText("");
        }

        //loading message count

        if( groupData.getMsgCount() != null && groupData.getMsgCountUser() != null){

            String messageCount = groupData.getMsgCount();
            String messageCountUser = groupData.getMsgCountUser();


            long tempMessageCount = Long.parseLong(messageCount);
            long tempMessageCountUser = Long.parseLong(messageCountUser);
            long tempNewMessageNumber = tempMessageCount - tempMessageCountUser;
            String newMessageNumber = String.valueOf(tempNewMessageNumber);


            if(tempNewMessageNumber != 0){

                holder.last_message.setTypeface(null, Typeface.BOLD);
                holder.time.setTypeface(null, Typeface.BOLD);

                holder.messageCounter.setVisibility(View.VISIBLE);
                if( tempNewMessageNumber < 100 ){
                    holder.messageCounter.setText(newMessageNumber);
                }else {
                    holder.messageCounter.setText(R.string.above99);
                }

            }else {
                holder.messageCounter.setVisibility(View.INVISIBLE);
                holder.last_message.setTypeface(null, Typeface.NORMAL);
                holder.time.setTypeface(null, Typeface.NORMAL);
            }

        }

           /* holder.messageCounter.setVisibility(View.VISIBLE);
            holder.messageCounter.setText(messageCount);
*/

         ///loading image

        if(groupData.getGroupImage() != null){

            Glide.with(context)
                    .load(groupData.getGroupImage())
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .dontAnimate()
                    .placeholder(R.drawable.dummyimage)
                    .into(holder.group_image);
        }
        holder.group_list_row_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                owngroupInterface.onclick_own_group(groupData,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupDataList.size();
    }


    static class myProfile_viewHolder extends RecyclerView.ViewHolder {

        CircleImageView group_image;
        TextView group_name, last_message, time, messageCounter;
        MaterialCardView group_list_row_cardView;

        myProfile_viewHolder(@NonNull View itemView) {
            super(itemView);
            group_image = itemView.findViewById(R.id.owngroupimageview);
            group_name = itemView.findViewById(R.id.owngroupnametextview);
            last_message = itemView.findViewById(R.id.last_group_message);
            messageCounter = itemView.findViewById(R.id.messageCounter);
            group_list_row_cardView = itemView.findViewById(R.id.group_vertical_parent_recycle_view_row_card_id);
            time = itemView.findViewById(R.id.time_messege);
        }
    }
}
