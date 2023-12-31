package com.example.chatroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatroomFindUserActivity extends AppCompatActivity {
    private EditText findUserNicknameEditText;
    private Button searchButton;
    private TextView findUserTextView;
    private LinearLayout usersLinearLayout;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_find_user);

        findUserNicknameEditText = findViewById(R.id.findUserNicknameEditText);
        searchButton = findViewById(R.id.searchButton);
        findUserTextView = findViewById(R.id.findUserTextView);
        usersLinearLayout = findViewById(R.id.usersLinearLayout);

        nickname = FirebaseUtil.getNickname();
        findUserTextView.setText("Let's find someone to talk,\n"+nickname);
    }

    public void searchUsers(View view) {
        String userNickname = findUserNicknameEditText.getText().toString();
        FirebaseUtil.checkSearchedUserIfExist(userNickname,this);

    }

    void addUserToLayout(User user) {

        // Button creation
        Button userToChatButton = new Button(this);

        // Button layout
        userToChatButton.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        userToChatButton.setTextSize(14);
        userToChatButton.setBackgroundColor(getColor(R.color.white));

        // Button margins
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(params);
        marginParams.setMargins(0, 10, 0, 10);
        userToChatButton.setLayoutParams(marginParams);

        // Button text
        StringBuilder userToChatButtonText = new StringBuilder();
        userToChatButtonText.append(user.getNickname());
        userToChatButtonText.append("\n");
        userToChatButtonText.append(user.getEmail());
        userToChatButton.setText(userToChatButtonText);

        // Add button to layout
        usersLinearLayout.addView(userToChatButton);

        // Button all Capital letters
        userToChatButton.setAllCaps(false);

        // Button onClickListener
        userToChatButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatroomActivity.class);
            intent.putExtra("userToChatUid", user.getUid());
            intent.putExtra("userToChatNickname", user.getNickname());
            startActivity(intent);
        });
    }

    public void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}