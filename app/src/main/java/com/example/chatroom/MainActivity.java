package com.example.chatroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText,passwordEditText,nicknameEditText;
    private Button signInButton,signUpButton,signOutButton,findSomeoneButton;
    private TextView welcomeTextView;

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
        findSomeoneButton = findViewById(R.id.findSomeoneButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        if (FirebaseUtil.getUser()!=null){
            setComponentsVisibility(true, FirebaseUtil.getNickname());
        }else{
            setComponentsVisibility(false, "");
        }

    }

    public void signup(View view){
        if(!emailEditText.getText().toString().isEmpty() &&
        !passwordEditText.getText().toString().isEmpty() &&
        !nicknameEditText.getText().toString().isEmpty()){

            FirebaseUtil.signup(emailEditText.getText().toString()
                    ,passwordEditText.getText().toString()
                    ,nicknameEditText.getText().toString()
                    ,this);

        }else {
            showMessage("Error","Please provide all info!");
        }
    }

    public void signin(View view){
        if(!emailEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty()) {

            FirebaseUtil.signin(emailEditText.getText().toString()
                    ,passwordEditText.getText().toString()
                    ,this);

        }else {
            showMessage("Error","Please provide all info!");
        }
    }

    public void signout (View view){
        FirebaseUtil.signout();

        setComponentsVisibility(false, "");
        emptyEditTexts();
        Toast.makeText(this,"You are successfully signed out!",Toast.LENGTH_SHORT).show();
    }

    public void chat(View view){
        Intent intent = new Intent(this, ChatroomFindUserActivity.class);
        startActivity(intent);
    }

    public void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    private void emptyEditTexts(){
        emailEditText.setText("");
        passwordEditText.setText("");
        nicknameEditText.setText("");
    }

    public void setComponentsVisibility(boolean userAuthenticated,String nickname){
        if (userAuthenticated) {
            emailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            nicknameEditText.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            findSomeoneButton.setVisibility(View.VISIBLE);

            welcomeTextView.setText("Welcome back,\n"+nickname);
            welcomeTextView.setVisibility(View.VISIBLE);
        }else {
            emailEditText.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            nicknameEditText.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            findSomeoneButton.setVisibility(View.GONE);

            welcomeTextView.setText("");
            welcomeTextView.setVisibility(View.GONE);
        }
    }
}