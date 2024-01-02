package com.example.chatroom;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseUtil {

    // Firebase Authorization
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Firebase Realtime Database
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    static FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    static String getUid() {
        return getUser().getUid();
    }

    static String getEmail() {
        return getUser().getEmail();
    }

    static String getNickname() {
        return getUser().getDisplayName();
    }

    static void signup(String email, String password, String nickname, MainActivity activity) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if (getUser() != null){
                        // Set user's nickname
                        getUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nickname).build())
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        //add the new user to database
                                        User user = new User(getUid(),getEmail(),getNickname());
                                        DatabaseReference reference = database.getReference("users").child(getUid());
                                        Map<String, String> userData = new HashMap<>();
                                        userData.put("email", getEmail());
                                        userData.put("nickname",getNickname());
                                        reference.setValue(userData);

                                        activity.setComponentsVisibility(true, getNickname());
                                        activity.showMessage("Success","User profile created!");
                                    }
                                });
                    }
                }else {
                    activity.setComponentsVisibility(false, "");
                    activity.showMessage("Error","Something went wrong!");
                }
            });
    }

    static void signin(String email, String password,MainActivity activity) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    activity.setComponentsVisibility(true, getNickname());
                    activity.showMessage("Success","User signed in successfully!");
                }else {
                    activity.setComponentsVisibility(false, "");
                    activity.showMessage("Error","Check your credentials!");
                }
            });
    }

    static void signout() {
        auth.signOut();
    }

    static void checkSearchedUserIfExist(String userNickname, ChatroomFindUserActivity activity) {
        DatabaseReference reference = database.getReference().child("users");
        reference.orderByChild("nickname").equalTo(userNickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Map<String, Object> nestedMap = (Map<String, Object>) snapshot.getValue();
                    for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if (!Objects.equals(key, getUid())){
                            // get the nickname and the email from the nested maps
                            Map<String, Object> userMap = (Map<String, Object>) value;
                            String nickname = (String) userMap.get("nickname");
                            String email = (String) userMap.get("email");
                            User user = new User(key,email,nickname);
                            activity.addUserToLayout(user);
                        }else {
                            // skip current user, because we don't want to chat with ourselves
                        }
                    }
                }else {
                    activity.showMessage("Error","The user you are looking for doesn't exist!\nSearch a different nickname!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    static void showChatMessages(String userToChatUid, ChatroomActivity activity) {
        DatabaseReference reference1 = database.getReference().child("messages").child(getUid()+"-"+userToChatUid);
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) {
                    Map<String, Object> nestedMap = (Map<String, Object>) snapshot.getValue();
                    String nickname = (String) nestedMap.get("senderNickname");
                    String message = (String) nestedMap.get("messageText");
                    activity.addMessageToLayout(nickname,message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = database.getReference().child("messages").child(userToChatUid+"-"+getUid());
        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map<String, Object> nestedMap = (Map<String, Object>) snapshot.getValue();
                    String nickname = (String) nestedMap.get("senderNickname");
                    String message = (String) nestedMap.get("messageText");
                    activity.addMessageToLayout(nickname,message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    static void sendMessage(String userToChatUid, String message) {
        DatabaseReference reference1 = database.getReference().child("messages").child(getUid()+"-"+userToChatUid);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String messageTime = String.valueOf(System.currentTimeMillis());
                    String messageUid = reference1.push().getKey();
                    reference1.child(messageUid).setValue(new Message(getNickname(),message,messageTime));
                }else {
                    DatabaseReference reference2 = database.getReference().child("messages").child(userToChatUid+"-"+getUid());
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String messageTime = String.valueOf(System.currentTimeMillis());
                                String messageUid = reference2.push().getKey();
                                reference2.child(messageUid).setValue(new Message(getNickname(),message,messageTime));
                            }else {
                                String messageTime = String.valueOf(System.currentTimeMillis());
                                String messageUid = reference1.push().getKey();
                                reference1.child(messageUid).setValue(new Message(getNickname(),message,messageTime));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
