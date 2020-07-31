package com.example.chat_application.group;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.chat_application.Data.yourGroupData;
import com.example.chat_application.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;


public class group_host_activity extends AppCompatActivity {

    public Toolbar toolbar;
    NavController navController;
    TextView state,group_name;
    CircleImageView group_image;
    private yourGroupData current_group_data;
    public static long message_serial = 0L;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_navhost);
        navController = Navigation.findNavController(this,R.id.group_nav_host_id);
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);

        //layout component
        state = findViewById(R.id.group_state);
        group_image = findViewById(R.id.group_image);
        group_name = findViewById(R.id.group_title);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            current_group_data = (yourGroupData) extras.getSerializable("groupData");
            loadData(current_group_data);


        }

    }

    private void loadData(yourGroupData group_data) {
        if(group_data != null && group_data.getGroupImage() == null){

            String groupName = group_data.getGroupName();
            group_name.setText(groupName);

        }else if (group_data != null && group_data.getGroupImage() != null){

            String groupName = group_data.getGroupName();
            String groupImageUri = group_data.getGroupImage();

            group_name.setText(groupName);

            Glide.with(group_host_activity.this)
                    .load(groupImageUri)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .dontAnimate()
                    .placeholder(R.drawable.dummyimage)
                    .into(group_image);

        }
    }

    public yourGroupData getCurrent_group_data() {
        return current_group_data;
    }
}
