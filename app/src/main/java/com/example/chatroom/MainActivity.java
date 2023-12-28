package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText,passwordEditText,nicknameEditText;
    private Button signInButton,signUpButton,signOutButton,chatroomButton;
    private TextView welcomeTextView;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        signOutButton = findViewById(R.id.signOutButton);
        chatroomButton = findViewById(R.id.chatroomButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user!=null){
            setComponentsVisibility(user!=null, user.getDisplayName());
        }else{
            setComponentsVisibility(user!=null, "");
        }

    }

    public void signup(View view){
        if(!emailEditText.getText().toString().isEmpty() &&
        !passwordEditText.getText().toString().isEmpty() &&
        !nicknameEditText.getText().toString().isEmpty()){
            auth.createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                user = auth.getCurrentUser();

                                updateUser(user,nicknameEditText.getText().toString());

                                setComponentsVisibility(true, user.getDisplayName());
                                showMessage("Success","User profile created!");
                            }else {
                                user = null;
                                setComponentsVisibility(false, "");
                                showMessage("Error","Something went wrong!"/*task.getException().getLocalizedMessage()*/);
                            }
                        }
                    });
        }else {
            showMessage("Error","Please provide all info!");
        }
    }

    public void signin(View view){
        if(!emailEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty()) {
            auth.signInWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user = auth.getCurrentUser();
                        updateUser(user,nicknameEditText.getText().toString());

                        setComponentsVisibility(true, user.getDisplayName());
                        showMessage("Success","User signed in successfully!");
                    }else {
                        user = null;

                        setComponentsVisibility(false, "");
                        showMessage("Error","Check your credentials!"/*task.getException().getLocalizedMessage()*/);
                    }
                }
            });
        }
    }

    public void signout (View view){
        auth.signOut();
        user = null;
        setComponentsVisibility(false, "");
        emptyEditTexts();
        Toast.makeText(this,"You are successfully signed out!",Toast.LENGTH_SHORT).show();
    }

    public void chat(View view){
        if (user!=null){
            Intent intent = new Intent(this, ChatroomActivity.class);
            intent.putExtra("nickname",user.getDisplayName());
            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else {
            showMessage("Error","Please sign-in or create an account first!");
        }
    }

    private void updateUser(FirebaseUser user, String nickname){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build();
        user.updateProfile(request);
    }

    private void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    private void emptyEditTexts(){
        emailEditText.setText("");
        passwordEditText.setText("");
        nicknameEditText.setText("");
    }

    private void setComponentsVisibility(boolean userAuthenticated,String nickname){
        if (userAuthenticated) {
            emailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            nicknameEditText.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            chatroomButton.setVisibility(View.VISIBLE);

            welcomeTextView.setText("Welcome back,\n"+nickname);
            welcomeTextView.setVisibility(View.VISIBLE);
        }else {
            emailEditText.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            nicknameEditText.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            chatroomButton.setVisibility(View.GONE);

            welcomeTextView.setText("");
            welcomeTextView.setVisibility(View.GONE);
        }
    }
}