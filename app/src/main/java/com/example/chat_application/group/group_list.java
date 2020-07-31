package com.example.chat_application.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chat_application.Data.group_data;
import com.example.chat_application.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;

public class group_list extends AppCompatActivity implements group_list_interface{

    RecyclerView recyclerView;
    group_list_adapter group_list_adapter;
    String currentUser;
    Query queryLoad;
    ChildEventListener templistener;
    RecyclerView.AdapterDataObserver tempOb;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        recyclerView = findViewById(R.id.group_list_recyler_view);
        currentUser = Objects.requireNonNull(getINSTANCE().getMAuth().getCurrentUser()).getUid();

    }

    private void newMessageTracker() {

       /* queryLoad = getINSTANCE().getRootRef().child("GROUP");
        templistener = queryLoad.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(group_list.this, "yes", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < group_list_adapter.getItemCount(); i++){
                    if(Objects.equals(dataSnapshot.getKey(), group_list_adapter.getItem(i).getGroupId())){

                        group_data group_data = group_list_adapter.getSnapshots().remove(i);
                        group_list_adapter.getSnapshots().add(0,group_data);
                        group_list_adapter.notifyItemMoved(i,0);

                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

          tempOb =  new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Toast.makeText(group_list.this, "yes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        };
        group_list_adapter.registerAdapterDataObserver(tempOb);


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadGroup();
        newMessageTracker();
    }

    private void loadGroup() {
        Query query = getINSTANCE().getRootRef().child("GROUP");
        FirebaseRecyclerOptions<group_data> options = new FirebaseRecyclerOptions.Builder<group_data>()
                .setQuery(query, group_data.class)
                .build();
         group_list_adapter = new group_list_adapter(options, group_list.this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(group_list_adapter);
        group_list_adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        group_list_adapter.stopListening();
       // queryLoad.removeEventListener(templistener);
        group_list_adapter.unregisterAdapterDataObserver(tempOb);
    }

    @Override
    public void onclick_group_list(final group_data group_data) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("join the group");
        alert.setMessage("Do you want join this group?");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               join_group(group_data);
            }
        });
        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();

    }

    private void join_group(group_data group_data) {

        String groupId = group_data.getGroupId();
        Map<String, Object> update = new HashMap<>();

        update.put("GROUP/"+groupId+"/msgCount","0");
        update.put("GROUP/"+groupId+"/members/"+currentUser,true);
        update.put("Users/"+currentUser+"/joinedGroups/"+groupId,true);

        getINSTANCE().getRootRef().updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(group_list.this, "joined the group", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(group_list.this, "Error"+e.toString(), Toast.LENGTH_LONG).show();
            }

        });

    }


}
