package com.tony_fire.descorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.db.DbManager;
import com.EditActivity;
import com.db.NewPost;
import com.accaunt_helper.Accaunt_helper;
import com.adapter.DataSender;
import com.adapter.PostAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.utils.MyConstans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.accaunt_helper.Accaunt_helper.GOOGLE_SIGN_IN_CODE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private TextView useremail;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private PostAdapter.onItemClickCustom onItemClickCustom;
    public RecyclerView rc;
    private PostAdapter postAdapter;
    private DataSender dataSender;
    private DbManager dbManager;
    public static String MAUTH = "";
    public String current_cat = MyConstans.DIFFERENT;
    private final int EDIT_RES = 12;
    private AdView adView;
    private Accaunt_helper accaunt_helper;
    private ImageView imPhoto;
    private MenuItem newAdItem;
    private  int checkghost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        addAds();
        init();
        setOnScrollListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account !=null) Picasso.get().load(account.getPhotoUrl()).into(imPhoto);
        if(adView !=null){
            adView.resume();
        }

            dbManager.getDataFromDb(current_cat,"0");

        }




    @Override
    protected void onPause() {
        super.onPause();
        if(adView !=null){
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adView !=null){
            adView.destroy();
        }
    }

    private  void init(){
        setOnItemClickCustom();
        rc = findViewById(R.id.rc_view);
        rc.setLayoutManager(new LinearLayoutManager(this));
        List<NewPost> arraypost = new ArrayList<>();
        postAdapter = new PostAdapter(arraypost,this,onItemClickCustom);
        rc.setAdapter(postAdapter);

        navigationView = findViewById(R.id.nav_view);
        imPhoto = navigationView.getHeaderView(0).findViewById(R.id.imPhoto);


        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        newAdItem = toolbar.getMenu().findItem(R.id.new_add);
        ToolbarItemClick();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this,drawerLayout,toolbar,R.string.toggleopen,R.string.toggleclose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        useremail = navigationView.getHeaderView(0).findViewById(R.id.tv_mail);
        mAuth = FirebaseAuth.getInstance();
        accaunt_helper = new Accaunt_helper(mAuth,this);


        Menu menu = navigationView.getMenu();

        MenuItem categorycount = menu.findItem(R.id.id_cat);
        MenuItem categorycount2 = menu.findItem(R.id.id_ads_cat);

        SpannableString sp = new SpannableString(categorycount.getTitle());
        SpannableString sp2 = new SpannableString(categorycount2.getTitle());
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.nav_view_accaunt)),0,sp.length(),0);
        sp2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.nav_view_accaunt)),0,sp2.length(),0);
        categorycount.setTitle(sp);
        categorycount2.setTitle(sp2);
        //Test
        getDataDb();
         dbManager = new DbManager(dataSender,this);
         postAdapter.setDbManager(dbManager);






        


    }

    private  void setOnScrollListener(){
        rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!rc.canScrollVertically(1)){
                    //Log.d("Mylog","Cant scroll down");
                    /*dbManager.getDataFromDb(current_cat,postAdapter.getMainList().get(postAdapter.getMainList()
                            .size()-1).getTime());
                    rc.scrollToPosition(0);*/
                }
                else if(!rc.canScrollVertically(-1)){
                    //Log.d("Mylog","Cant scroll up");

                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void getDataDb(){
        dataSender = new DataSender() {
            @Override
            public void onDataReceived(List<NewPost> listdata) {
                Collections.reverse(listdata);
                postAdapter.Updateadapter(listdata);

            }
        };

    }

    private void setOnItemClickCustom(){
        onItemClickCustom = new PostAdapter.onItemClickCustom() {
            @Override
            public void onItemSelected(int position) {
                Log.d("MyLog","Position:"+position);

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case EDIT_RES:
                if( resultCode == RESULT_OK && data !=null){
                    current_cat = data.getStringExtra("cat");

                }
                break;
            case Accaunt_helper.GOOGLE_SIGN_IN_CODE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    if(account !=null) {
                        Picasso.get().load(account.getPhotoUrl()).into(imPhoto);
                        accaunt_helper.signInFirebaseGoogle(account.getIdToken(), 0);

                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }

                break;
            case Accaunt_helper.GOOGLE_SIGN_IN_LINK_CODE:
                Task<GoogleSignInAccount> task2 = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task2.getResult(ApiException.class);

                    if(account !=null) accaunt_helper.signInFirebaseGoogle(account.getIdToken(),1);
                } catch (ApiException e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getuserData();



    }




    public void getuserData(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            if(currentUser.isAnonymous()){
                checkghost = 0;
                newAdItem.setVisible(false);
                useremail.setText(R.string.guest);
            } else {
                checkghost = 1;
                newAdItem.setVisible(true);
                useremail.setText(currentUser.getEmail());
            }

            MAUTH = mAuth.getUid();

            onResume();

        }
        else {

            accaunt_helper.SignInAnonimus();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.id_others_ads:


                current_cat = MyConstans.DIFFERENT;
                dbManager.getDataFromDb(current_cat,"0");

                break;
            case R.id.id_ads:
                    if(checkghost==1) {
                        current_cat = MyConstans.MY_ADS;
                        dbManager.getDataFromDb(current_cat, "0");
                    }else {
                        Toast.makeText(this, R.string.cantseecategory, Toast.LENGTH_LONG).show();
                    }



            break;

            case R.id.fv_ads:
                    if(checkghost == 1) {
                        current_cat = MyConstans.MY_FAVS;
                        dbManager.getDataFromDb(current_cat, "0");
                    } else {
                        Toast.makeText(this, R.string.cantseecategory, Toast.LENGTH_LONG).show();
                    }




                break;


            case R.id.id_cars:
                current_cat = getResources().getStringArray(R.array.category_spinnet)[0];
                dbManager.getDataFromDb("Машины","0");
                break;
            case R.id.id_pc:
                current_cat = getResources().getStringArray(R.array.category_spinnet)[1];
                dbManager.getDataFromDb("Компьютеры","0");
                break;
            case R.id.id_smartphone:
                current_cat = getResources().getStringArray(R.array.category_spinnet)[2];
                dbManager.getDataFromDb("Смартфоны","0");
                break;
            case R.id.id_dm:
                current_cat = getResources().getStringArray(R.array.category_spinnet)[3];
                dbManager.getDataFromDb("Бытовая техника","0");
                break;
            case R.id.id_sign_up:
                SignDialog(R.string.sign_up,R.string.sign_up_1,R.string.google_sign,0);

                break;
            case R.id.id_sign_in:
                SignDialog(R.string.sign_in,R.string.sign_in_1,R.string.google_sign_in,1);


                break;
            case R.id.id_sign_out:
                if(checkghost == 1) {
                    accaunt_helper.signOut();
                    current_cat = MyConstans.DIFFERENT;
                    dbManager.getDataFromDb(current_cat, "0");
                    imPhoto.setImageResource(android.R.color.transparent);
                } else {
                    Toast.makeText(this, R.string.cantexit, Toast.LENGTH_LONG).show();
                }


                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SignDialog(int title,int buttontitle,int googletitle,int index){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.sign_layout,null);
        dialogBuilder.setView(view);
        TextView titleTextview = view.findViewById(R.id.tvAlertTitile);
        TextView titleTextview2 = view.findViewById(R.id.tv_message);
        titleTextview.setText(title);
        Button b_signUpEmail = view.findViewById(R.id.b_sighn_up);
        SignInButton b_signUpGoogle = view.findViewById(R.id.b_google_sign);
        Button b_ForgetPassword = view.findViewById(R.id.b_forget);
        switch (index){
            case 0:
                b_ForgetPassword.setVisibility(View.GONE);
                break;
            case 1:
                b_ForgetPassword.setVisibility(View.VISIBLE);
                break;
        }
        EditText edEmail = view.findViewById(R.id.edEmail);
        EditText edPassword = view.findViewById(R.id.edPass);
        b_signUpEmail.setText(buttontitle);
        b_signUpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {

                    if (mAuth.getCurrentUser().isAnonymous()) {
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (index == 0)
                                        accaunt_helper.signUp(edEmail.getText().toString(), edPassword.getText().toString());
                                    else
                                        accaunt_helper.signIn(edEmail.getText().toString(), edPassword.getText().toString());

                                }


                            }
                        });

                    }

                }
                dialog.dismiss();


            }



            });

        b_signUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() !=null) {
                    if (mAuth.getCurrentUser().isAnonymous()) {
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    accaunt_helper.signINGoogle(GOOGLE_SIGN_IN_CODE);

                                }

                            }
                        });


                    }
                }

                dialog.dismiss();

            }
        });
        b_ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edPassword.isShown()) {
                    edPassword.setVisibility(View.GONE);
                    b_signUpEmail.setVisibility(View.GONE);
                    b_signUpGoogle.setVisibility(View.GONE);
                    titleTextview2.setVisibility(View.VISIBLE);
                    titleTextview.setText(R.string.forget_password);
                    b_ForgetPassword.setText(R.string.send_forget_password);
                    titleTextview2.setText(R.string.tv_password_message);

                }else {
                    if(!edEmail.getText().toString().equals("")) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(edEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, R.string.send_completed, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                                else {
                                    Toast.makeText(MainActivity.this,R.string.error_send, Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }else {
                        Toast.makeText(MainActivity.this, R.string.email_empty, Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });
         dialog = dialogBuilder.create();
         if(dialog.getWindow() !=null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();


    }

    private void addAds(){
        MobileAds.initialize(this);
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }
    private void ToolbarItemClick(){
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.new_add){
                    if(mAuth.getCurrentUser()!= null){
                        if(mAuth.getCurrentUser().isEmailVerified()) {
                            Intent i = new Intent(MainActivity.this, EditActivity.class);
                            startActivityForResult(i, EDIT_RES);
                        } else {
                            accaunt_helper.showDialogNoVerify(R.string.alert,R.string.mail_not_verified);

                        }
                    }

                }


                return false;
            }
        });
    }

}