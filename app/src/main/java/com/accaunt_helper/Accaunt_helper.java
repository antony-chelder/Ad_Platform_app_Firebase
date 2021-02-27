package com.accaunt_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tony_fire.descorder.MainActivity;
import com.tony_fire.descorder.R;

public class Accaunt_helper {
    private FirebaseAuth mAuth;
    private MainActivity activity;
    private GoogleSignInClient googleSignInClient;
    public static final int GOOGLE_SIGN_IN_CODE = 54;
    public static final int GOOGLE_SIGN_IN_LINK_CODE = 27;
    private String tempEmail,tempPassword;


    public Accaunt_helper(FirebaseAuth mAuth, MainActivity activity) {
        this.mAuth = mAuth;
        this.activity = activity;
        SignInGoogleManager();
    }
    //SignUp by Email
    public void signUp(String email,String password){
        if(!email.equals("")&&!password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(mAuth.getCurrentUser() != null) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendEmailVerify(user);
                                }
                                activity.getuserData();



                            } else {
                                // If sign in fails, display a message to the user.
                                FirebaseAuthUserCollisionException exception = ( FirebaseAuthUserCollisionException)
                                        task.getException();
                                if(exception == null) return;
                                if(exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                    LinkEmailPass(email,password);

                                }


                            }
                        }
                    });
        }
        else {
            Toast.makeText(activity, R.string.emaiorpassempty, Toast.LENGTH_SHORT).show();
            activity.getuserData();
        }

    }
    public   void signIn(String email,String password){
        if(!email.equals("")&& !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(activity, "Log In done!", Toast.LENGTH_SHORT).show();
                                activity.getuserData();

                            }
                                else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLog", "signInWithEmail:failure", task.getException());
                                Toast.makeText(activity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });


        }
        else {
            Toast.makeText(activity, R.string.emaiorpassempty, Toast.LENGTH_SHORT).show();
            activity.getuserData();
        }


    }

    public void signOut(){
        if(mAuth.getCurrentUser() ==null)return;
        if(mAuth.getCurrentUser().isAnonymous())return;
        mAuth.signOut();
        googleSignInClient.signOut();
        activity.getuserData();

    }

    private void sendEmailVerify(FirebaseUser user){
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showDialog(R.string.alert,R.string.check_mail);
                }
            }
        });

    }
    //Sign In by Google Accaunt
    private void SignInGoogleManager(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(activity.getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(activity,gso);
    }

    public void signINGoogle(int code){
        Intent signinIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signinIntent,code);

    }

    public void signInFirebaseGoogle(String idtoken,int index){
        AuthCredential credential = GoogleAuthProvider.getCredential(idtoken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(activity, "Log In done!", Toast.LENGTH_SHORT).show();
                    if(index == 1) LinkEmailPass(tempEmail,tempPassword);
                    activity.getuserData();


                }
                else{

                }

            }
        });


    }
    //Dialog
    public void showDialog(int title,int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();

    }

    public void showDialogSigninWithLink(int title,int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signINGoogle(GOOGLE_SIGN_IN_LINK_CODE);

            }
        });
        builder.create();
        builder.show();

    }

    public void showDialogNoVerify(int title,int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();

            }
        });
        builder.setNegativeButton(R.string.send_email_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mAuth.getCurrentUser()!=null) {
                    sendEmailVerify(mAuth.getCurrentUser());
                }


            }
        });
        builder.create();
        builder.show();

    }

    private void LinkEmailPass(String email,String password){
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        if(mAuth.getCurrentUser() !=null){
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, R.string.link_ok_massage, Toast.LENGTH_SHORT).show();
                            if(task.getResult() == null) return;
                            activity.getuserData();


                        } else {

                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }else {
            tempEmail = email;
            tempPassword = password;
            showDialogSigninWithLink(R.string.alert,R.string.sign_link_massage);


        }
    }
    public void SignInAnonimus(){
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    activity.getuserData();

                }

            }
        });
    }

}
