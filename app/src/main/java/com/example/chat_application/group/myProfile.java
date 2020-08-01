package com.example.chat_application.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.chat_application.Data.yourGroupData;
import com.example.chat_application.Data.group_data;
import com.example.chat_application.MainActivity;
import com.example.chat_application.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;
import static com.example.chat_application.singleton.yourGroupSingleton.getYourGroupListInstance;


public class myProfile extends AppCompatActivity implements owngroupInterface{
    private ProgressDialog loadingBar;
    Uri profile_image_Uri;
    private String currentUserID;
    private StorageReference UserProfileImagesRef;
    ImageView profileImage;
    AppCompatImageButton upload_profile_image;
    TextView profile_name;
    RecyclerView group_list_recycle_view;
    Toolbar mainToolbar;
    myProfile_adapter myProfile_adapter;
    ArrayList<String> joinedGroupListKey;

   // List<yourGroupData> yourGroupList;

    //use for tracking which group user opened
    static int yourGroupIntoPosition = -1;
    static String yourGroupIntoId = null;

    //listener and query for newMessageTracker
    Query newMessageQuery;
    ChildEventListener newMessageListener;

    boolean breaks = false; //for breaking the loop in the listener;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        profileImage = findViewById(R.id.profile_image);
        upload_profile_image = findViewById(R.id.upload_profile_image_button);
        profile_name = findViewById(R.id.profile_name);
        group_list_recycle_view = findViewById(R.id.Own_group_list_recycleview);

        group_list_recycle_view.setHasFixedSize(true);
        group_list_recycle_view.setLayoutManager(new LinearLayoutManager(this));

        loadingBar = new ProgressDialog(this);
        currentUserID = Objects.requireNonNull(getINSTANCE().getMAuth().getCurrentUser()).getUid();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference("profileImage");

        joinedGroupListKey = new ArrayList<>();

        load_data();
        loadGroup();

    }

    @Override
    protected void onStart() {
        super.onStart();



        //dynamic updating last message for which group user last entered and send a message
       if(yourGroupIntoPosition != -1 && yourGroupIntoId != null){

           getINSTANCE().getRootRef().child("Users").child(currentUserID).child("joinedGroups").child(yourGroupIntoId).addListenerForSingleValueEvent(new ValueEventListener() {
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.hasChild("lastmsgUserName")){

                       getYourGroupListInstance().getYourGroupList().get(yourGroupIntoPosition).setLastmsgUserName(Objects.requireNonNull(dataSnapshot.child("lastmsgUserName").getValue()).toString());
                       getYourGroupListInstance().getYourGroupList().get(yourGroupIntoPosition).setLastMessage(Objects.requireNonNull(dataSnapshot.child("lastMessage").getValue()).toString());
                       getYourGroupListInstance().getYourGroupList().get(yourGroupIntoPosition).setLastmsgTime(Objects.requireNonNull(dataSnapshot.child("lastmsgTime").getValue()).toString());
                       myProfile_adapter.notifyDataSetChanged();

                   }


                   yourGroupIntoPosition = -1;
                   yourGroupIntoId = null;
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }


       //track new message dynamically
        newMessageTracker();

    }

    private void newMessageTracker(){

        newMessageQuery = getINSTANCE().getRootRef().child("Users").child(currentUserID).child("joinedGroups");
        newMessageListener = newMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                  for (int i=0; i<getYourGroupListInstance().getYourGroupList().size(); i++){
                    if(getYourGroupListInstance().getYourGroupList().get(i).getGroupId().equals(dataSnapshot.getKey())){

                        //updating last message
                        getYourGroupListInstance().getYourGroupList().get(i).setLastmsgUserName(Objects.requireNonNull(dataSnapshot.child("lastmsgUserName").getValue()).toString());
                        getYourGroupListInstance().getYourGroupList().get(i).setLastMessage(Objects.requireNonNull(dataSnapshot.child("lastMessage").getValue()).toString());
                        getYourGroupListInstance().getYourGroupList().get(i).setLastmsgTime(Objects.requireNonNull(dataSnapshot.child("lastmsgTime").getValue()).toString());

                        // updating user message count
                        /*long tempMessageCountUser =  Long.parseLong(getYourGroupListInstance().getYourGroupList().get(i).getMsgCountUser()) + 1;
                        String messageCountUser = String.valueOf(tempMessageCountUser);*/

                        final int finalI = i;

                        getINSTANCE().getRootRef().child("GROUP").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                getYourGroupListInstance().getYourGroupList().get(finalI).setMsgCount(Objects.requireNonNull(dataSnapshot.child("msgCount").getValue()).toString());
                                yourGroupData temp =  getYourGroupListInstance().getYourGroupList().remove(finalI);
                                getYourGroupListInstance().getYourGroupList().add(0,temp);

                                myProfile_adapter.notifyDataSetChanged();
                                breaks = true;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(breaks){
                        breaks = false;
                        break;
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
        });

    }

    private void loadGroup() {

       // yourGroupList = new ArrayList<>();
        myProfile_adapter = new myProfile_adapter(myProfile.this,this, getYourGroupListInstance().getYourGroupList());
        group_list_recycle_view.setAdapter(myProfile_adapter);

        //getting last messages and message count(specific to current user not the group itself) for joined groups
        getINSTANCE().getRootRef().child("Users").child(currentUserID).child("joinedGroups").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              for (DataSnapshot data : dataSnapshot.getChildren()){

                  yourGroupData groupData;

                  if(!Boolean.parseBoolean(Objects.requireNonNull(data.getValue()).toString())){ //if last message is available

                      groupData = data.getValue(yourGroupData.class); //getting data

                      if(groupData !=null){

                          if(data.hasChild("msgCountUser")){ //if message counter available
                              groupData.setGroupId(data.getKey());
                              getYourGroupListInstance().getYourGroupList().add(groupData);
                          }else { //if no message counter available
                              groupData.setGroupId(data.getKey());
                              groupData.setMsgCountUser("0");
                              getYourGroupListInstance().getYourGroupList().add(groupData);
                          }
                      }
                  }else { //if no last message is available
                      groupData = new yourGroupData();
                      groupData.setGroupId(data.getKey());
                      getYourGroupListInstance().getYourGroupList().add(groupData);
                  }
              }


              //getting joined group information
                for (int i=0; i<getYourGroupListInstance().getYourGroupList().size(); i++){

                    final int finalI = i; //temp variable
                    getINSTANCE().getRootRef().child("GROUP").child(getYourGroupListInstance().getYourGroupList().get(i).getGroupId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            getYourGroupListInstance().getYourGroupList().get(finalI).setMsgCount(Objects.requireNonNull(dataSnapshot.child("msgCount").getValue()).toString());
                            getYourGroupListInstance().getYourGroupList().get(finalI).setGroupName(Objects.requireNonNull(dataSnapshot.child("groupName").getValue()).toString());

                            if(dataSnapshot.hasChild("groupImage")){ //if has group image
                                getYourGroupListInstance().getYourGroupList().get(finalI).setGroupImage(Objects.requireNonNull(dataSnapshot.child("groupImage").getValue()).toString());
                            }

                            //sending to adapter
                            myProfile_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    /*addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){

                    getINSTANCE().getRootRef().child("GROUP").child(Objects.requireNonNull(dataSnapshot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            group_data groupData = dataSnapshot.getValue(group_data.class);
                            groupList.add(groupData);
                            myProfile_adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        });


*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.log_out){
            getINSTANCE().getMAuth().signOut();
            Intent intent = new Intent(myProfile.this, MainActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.groups){
            Intent intent = new Intent(myProfile.this, group_list.class);
            startActivity(intent);
        }
        return true;
    }

    private void load_data() {

        getINSTANCE().getRootRef().child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.hasChild("image")){
                            String pName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            String pImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                            profile_name.setText(pName);
                            Glide.with(myProfile.this)
                                    .load(pImage)
                                    .format(DecodeFormat.PREFER_ARGB_8888)
                                    .dontAnimate()
                                    .placeholder(R.drawable.dummyavatar)
                                    .into(profileImage);

                        }else if(dataSnapshot.exists()) {
                            String pName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            profile_name.setText(pName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { //requesting for pick image from gallery
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                assert result != null;
                profile_image_Uri = result.getUri();
                upload_profile_image_to_storage();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                loadingBar.dismiss();
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void upload_profile_image_to_storage(){
        final StorageReference filePath = UserProfileImagesRef.child(currentUserID+".jpg");
        UploadTask  uploadTask =filePath.putFile(profile_image_Uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    update_profile_image_download_ure(downloadUri.toString());

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(myProfile.this, "could not get image download uri", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void update_profile_image_download_ure(String imageUriFromStorage) {
        getINSTANCE().getRootRef().child("Users").child(currentUserID).child("image")
                .setValue(imageUriFromStorage)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            loadingBar.dismiss();
                        }else {
                            loadingBar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(myProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void select_image(View view) {
        CropImage.activity()  //opening crop image activity for choosing image from gallery and cropping, this activity is from a custom api library
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void create_group(View view) {
       Intent intent = new Intent(myProfile.this,group_activity.class);
       startActivity(intent);
    }


    @Override
    public void onclick_own_group(yourGroupData group_data, int position, int newMessage) {
        yourGroupIntoPosition = position;
        yourGroupIntoId = group_data.getGroupId();
        Intent intent = new Intent(myProfile.this,group_host_activity.class);
        intent.putExtra("groupData",group_data);
        intent.putExtra("position",position);
        intent.putExtra("newMessage",newMessage);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        newMessageQuery.removeEventListener(newMessageListener);
    }
}
