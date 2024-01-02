package com.example.chatroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatroomActivity extends AppCompatActivity {
    private TextView userToChatNicknameTextView;
    private EditText writeMessageEditTextText;
    private ScrollView messagesScrollView;
    private LinearLayout messagesLinearLayout;
    private String userToChatNickname;
    private String userToChatUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        userToChatNicknameTextView = findViewById(R.id.userToChatNicknameTextView);
        userToChatNickname = getIntent().getStringExtra("userToChatNickname");
        userToChatUid = getIntent().getStringExtra("userToChatUid");
        userToChatNicknameTextView.setText(userToChatNickname);
        messagesScrollView = findViewById(R.id.messagesScrollView);
        messagesLinearLayout = findViewById(R.id.messagesLinearLayout);

        FirebaseUtil.showChatMessages(userToChatUid,this);

        writeMessageEditTextText = findViewById(R.id.writeMessageEditTextText);
    }

    void addMessageToLayout(String nickName, String message) {

        // TextView creation
        TextView messageTextView = new TextView(this);

        // TextView layout
        messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        messageTextView.setTextSize(16);
        messageTextView.setBackgroundColor(getColor(R.color.white));

        // TextView margins
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(params);
        marginParams.setMargins(0, 5, 0, 5);
        messageTextView.setLayoutParams(marginParams);

        // TextView text
        String messageText = nickName + ": " + message;
        messageTextView.setText(messageText);

        // TextView add to layout
        messagesLinearLayout.addView(messageTextView);
    }

    public void send(View view){
        if (!writeMessageEditTextText.getText().toString().trim().isEmpty()){
            FirebaseUtil.sendMessage(userToChatUid,writeMessageEditTextText.getText().toString());
            writeMessageEditTextText.setText("");
        }else {
            showMessage("Error","Please write a message first!..");
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}