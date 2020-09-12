package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.chat_application.group.myProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;

import static com.example.chat_application.singleton.firebase_init_singleton.getINSTANCE;

public class MainActivity extends AppCompatActivity {

    TextInputEditText email, password;
   // private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, myProfile.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        //mAuth = FirebaseAuth.getInstance();
        currentUser = getINSTANCE().getMAuth().getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void login(View view) {
        String user_email = Objects.requireNonNull(email.getText()).toString();
        String user_password = Objects.requireNonNull(password.getText()).toString();

        if (TextUtils.isEmpty(user_email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(user_password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            getINSTANCE().getMAuth().signInWithEmailAndPassword(user_email,user_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()){
                               Intent intent = new Intent(MainActivity.this, myProfile.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                               finish();
                           }else {
                               String message = Objects.requireNonNull(task.getException()).toString();
                               password.setError(message);
                           }
                        }
                    });
        }



        }


    public void create_account(View view) {
        Intent intent = new Intent(this, register_activity.class);
        startActivity(intent);
    }
}
