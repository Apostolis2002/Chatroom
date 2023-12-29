package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatroomFindUserActivity extends AppCompatActivity {
    private EditText findUserEmailEditText;
    private Button startConversationButton;
    private TextView findUserTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_find_user);

    }
}