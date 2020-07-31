package com.example.chat_application;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_application.group.myProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.Objects;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;

public class register_activity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
  //  private DatabaseReference RootRef;
    TextInputEditText user_name, user_email, user_password, confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
       // mAuth = FirebaseAuth.getInstance();
        user_name = findViewById(R.id.user_name_edit_text);
        user_email = findViewById(R.id.register_email_edit_text);
        user_password = findViewById(R.id.register_password_edit_text);
        confirm_password = findViewById(R.id.register_confirm_password_edit_text);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(View view) {
        String email = Objects.requireNonNull(user_email.getText()).toString();
        String password = Objects.requireNonNull(user_password.getText()).toString();
        String confirm_pass = Objects.requireNonNull(confirm_password.getText()).toString();
        final String Name = Objects.requireNonNull(user_name.getText()).toString();

        if(!password.equals(confirm_pass)){
            Toast.makeText(this, "Password is not matched", Toast.LENGTH_SHORT).show();
        }else {
            getINSTANCE().getMAuth().createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful() && getINSTANCE().getMAuth().getCurrentUser()!=null){
                                String currentUserID = Objects.requireNonNull(getINSTANCE().getMAuth().getCurrentUser()).getUid();
                                HashMap<String, Object> UserMap = new HashMap<>();
                                UserMap.put("uid",currentUserID);
                                UserMap.put("name",Name);

                                getINSTANCE().getRootRef().child("Users").child(currentUserID).setValue(UserMap);
                                Toast.makeText(register_activity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(register_activity.this, myProfile.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                user_password.setError(message);
                            }
                        }
                    });
        }
    }
}

