package com.example.chat_application.group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat_application.Data.group_data;
import com.example.chat_application.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;


public class create_group_fragment extends Fragment {

    private Create_Group_Fragment_ViewModel create_group_fragment_viewModel_ob;
    private Uri group_image_uri;
    private Uri group_image_download_Uri;
    TextInputEditText groupnameinput_edittext,groupdescription_edittext;
    Button newgroupcreate_button;
    AppCompatButton uploadgroupphoto;
    ImageView group_image;
    private String currentUser, localGroupId, groupImage;
    private StorageReference groupImagesRef;
    private ProgressDialog loadingBar;

    public static create_group_fragment newInstance() {
        return new create_group_fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_group_fragment, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupnameinput_edittext=view.findViewById(R.id.group_name_edit_text);
        groupdescription_edittext=view.findViewById(R.id.group_description_edit_text);
        newgroupcreate_button=view.findViewById(R.id.newgroupcreatebutton);
        uploadgroupphoto=view.findViewById(R.id.upload_image_for_create_group);
        group_image = view.findViewById(R.id.group_image);
        loadingBar = new ProgressDialog(getContext());
        create_group_fragment_viewModel_ob = new ViewModelProvider(this).get(Create_Group_Fragment_ViewModel.class);

        currentUser = Objects.requireNonNull(getINSTANCE().getMAuth().getCurrentUser()).getUid();
        groupImagesRef = FirebaseStorage.getInstance().getReference("GroupImage");


        newgroupcreate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createAnewGroup();
            }
        });

        uploadgroupphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = CropImage.activity()  //opening crop image activity for choosing image from gallery and cropping, this activity is from a custom api library
                        .getIntent(requireContext());
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });


        create_group_fragment_viewModel_ob.getImage_uri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                group_image.setImageURI(uri);
            }
        });
        groupnameinput_edittext.addTextChangedListener(textWatcher);
        groupdescription_edittext.addTextChangedListener(textWatcher);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {  //requesting for pick image from gallery
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                assert result != null;
                group_image_uri = result.getUri();
                create_group_fragment_viewModel_ob.setImage_uri(group_image_uri);
                group_image.setImageURI(group_image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void upload_group_image_to_storage(){
        final StorageReference filePath = groupImagesRef.child(localGroupId+".jpg");
        UploadTask uploadTask =filePath.putFile(group_image_uri);
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
                    group_image_download_Uri = task.getResult();
                    assert group_image_download_Uri != null;
                    groupImage = group_image_download_Uri.toString();
                    add_group_image_download_uri(groupImage);

                } else {

                    Toast.makeText(getContext(), "could not get image download uri", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void add_group_image_download_uri(String groupImageUri) {
        getINSTANCE().getRootRef().child("GROUP").child(localGroupId).child("groupImage")
                .setValue(groupImageUri)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(getContext(), "Group Created Successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();

                        }else {
                            loadingBar.dismiss();
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    //watch edit text if it is empty or not
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          String group_name = Objects.requireNonNull(groupnameinput_edittext.getText()).toString().trim();
          String group_description = Objects.requireNonNull(groupdescription_edittext.getText()).toString().trim();
          newgroupcreate_button.setEnabled(!group_name.isEmpty() && !group_description.isEmpty());  //setting the button enable if all edit text are not empty
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createAnewGroup(){
      final String group_name, group_description, group_id;

        loadingBar.setTitle("Create Group");
        loadingBar.setMessage("Please wait, your group is creating...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

          group_name = Objects.requireNonNull(groupnameinput_edittext.getText()).toString().trim();
          group_description = Objects.requireNonNull(groupdescription_edittext.getText()).toString().trim();
          group_id = getINSTANCE().getRootRef().child("GROUP").push().getKey();
          localGroupId = group_id;
          group_data groupData = new group_data(group_id,group_name,group_description,currentUser);
          assert group_id != null;
          getINSTANCE().getRootRef().child("GROUP").child(group_id).setValue(groupData)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {

                          getINSTANCE().getRootRef().child("Users").child(currentUser).child("joinedGroups").child(group_id).setValue(true);
                          getINSTANCE().getRootRef().child("GROUP").child(group_id).child("members").child(currentUser).setValue(true);


                          if(group_image_uri != null){
                              upload_group_image_to_storage();
                          }else {
                              loadingBar.dismiss();
                              Toast.makeText(getContext(), "Group Created Successfully", Toast.LENGTH_SHORT).show();
                              requireActivity().finish();
                          }
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(getContext(), "Error "+e.toString(), Toast.LENGTH_LONG).show();
                      }
                  });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
