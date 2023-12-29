package com.example.chatroom;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

                                //add the new user to database
                                User user = new User(getUid(),getEmail(),getNickname());
                                DatabaseReference reference = database.getReference("users").child(getUid());
                                reference.setValue(user);

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
}
