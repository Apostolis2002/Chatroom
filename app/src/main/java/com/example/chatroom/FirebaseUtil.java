package com.example.chatroom;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    // Firebase Realtime Database
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

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
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (getUser() != null){
                            // Set user's nickname
                            getUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nickname).build());

                            // Wait until the profile has been updated
                            while (getNickname() == null){}

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
                    }else {
                        activity.setComponentsVisibility(false, "");
                        activity.showMessage("Error","Something went wrong!");
                    }
                }
            });
    }

    static void signin(String email, String password,MainActivity activity) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        activity.setComponentsVisibility(true, getNickname());
                        activity.showMessage("Success","User signed in successfully!");
                    }else {
                        activity.setComponentsVisibility(false, "");
                        activity.showMessage("Error","Check your credentials!");
                    }
                }
            });
    }

    static void signout() {
        auth.signOut();
    }

    /*public static User getUser(String uid) {
        User searchedUser;
        DatabaseReference reference = database.getReference().child("users").child(uid);
        reference.addListenerForSingleValueEvent();

        searchedUser = new User()
        return searchedUser;
    }*/

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
                        if (Objects.equals(key, getUid())){
                            activity.showMessage("Error","You cannot talk to yourself!\nFind another user to talk!");
                        }else{
                            // get the nickname and the email from the nested maps
                            Map<String, Object> userMap = (Map<String, Object>) value;
                            String nickname = (String) userMap.get("nickname");
                            String email = (String) userMap.get("email");
                            //activity.showMessage("FireBase",nickname+"\n"+email);
                            User user = new User(key,email,nickname);
                            activity.addUserToLayout(user);
                        }
                    }
                }else {
                    activity.showMessage("Error","The user you are looking for doesn't exist!\nSearch a different email!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
