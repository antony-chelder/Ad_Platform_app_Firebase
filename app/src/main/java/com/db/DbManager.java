package com.db;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adapter.DataSender;
import com.adapter.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tony_fire.descorder.R;
import com.utils.MyConstans;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    Context context;
    public static final String MY_FAV_PATH = "myFav";
    public static final String MAIN_ADS_PATH = "main_ads_path";
    public static final String FAV_ADS_PATH = "fav_ads_path";
    public static final String USER_FAV_ID = "user_fav_id";
    public static final String ORDER_BY_CAT_TIME = "/status/cat_time";
    public static final String ORDER_BY_TIME = "/status/filter_time";
    private Query mquery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseStorage fs;
    private int deleteImageCounter = 0;

    public void DeleteItem(final NewPost newPost) {
        StorageReference storageReference = null;
        switch (deleteImageCounter) {
            case 0:
                if (!newPost.getImageId().equals("empty")) {
                    storageReference = fs.getReferenceFromUrl(newPost.getImageId());
                } else {
                    deleteImageCounter++;
                    DeleteItem(newPost);
                }
                break;
            case 1:
                if (!newPost.getImageId2().equals("empty")) {
                    storageReference = fs.getReferenceFromUrl(newPost.getImageId2());
                } else {
                    deleteImageCounter++;
                    DeleteItem(newPost);
                }
                break;
            case 2:
                if (!newPost.getImageId3().equals("empty")) {
                    storageReference = fs.getReferenceFromUrl(newPost.getImageId3());
                } else {

                    deleteDbItem(newPost);
                    storageReference = null;
                    deleteImageCounter = 0;
                }
                break;

        }
        if (storageReference == null) return;
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteImageCounter++;
                if (deleteImageCounter < 3) {
                    DeleteItem(newPost);
                } else {
                    deleteImageCounter = 0;
                    deleteDbItem(newPost);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "An error occured,image not deleted", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void deleteDbItem(NewPost newPost) {
        DatabaseReference databaseReference = db.getReference(DbManager.MAIN_ADS_PATH);
        databaseReference.child(newPost.getKey()).child("status").removeValue();
        databaseReference.child(newPost.getKey()).child(FAV_ADS_PATH).child(mAuth.getUid()).removeValue();
        databaseReference.child(newPost.getKey()).child(mAuth.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, R.string.item_deleted, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "An error occured,item not deleted", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public DbManager(DataSender dataSender, Context context) {
        this.dataSender = dataSender;
        this.context = context;
        newPostList = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
        fs = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void getDataFromDb(String cat, String lastTime) {
        if(mAuth.getUid() == null)return;
        DatabaseReference databaseReference = db.getReference(MAIN_ADS_PATH);
        if(cat.equals(MyConstans.DIFFERENT)){
            if(lastTime.equals("0")) {
                mquery = databaseReference.orderByChild(ORDER_BY_TIME).limitToLast(MyConstans.ADS_LIMIT);
            } else {
                mquery = databaseReference.orderByChild(ORDER_BY_TIME).endAt(lastTime).limitToLast(MyConstans.ADS_LIMIT);

            }
        }else if(cat.equals(MyConstans.MY_ADS)){
            mquery = databaseReference.orderByChild(mAuth.getUid()+ "/ads/uid").equalTo(mAuth.getUid());

        }
        else if(cat.equals(MyConstans.MY_FAVS)){
            mquery = databaseReference.orderByChild(FAV_ADS_PATH + "/" + mAuth.getUid() + "/" + USER_FAV_ID).equalTo(mAuth.getUid());


        }else {
            if (lastTime.equals("0")) {
                mquery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(cat).endAt(cat + "\uf8ff");
            } else {
                mquery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(cat).endAt(cat + "\uf8ff");

            }

        }
        readDataUpdate();





        }
    public void getBackDataFromDb(String cat, String lastTime) {
        if(mAuth.getUid() == null)return;
        DatabaseReference databaseReference = db.getReference(MAIN_ADS_PATH);
        if(cat.equals(MyConstans.DIFFERENT)){

                mquery = databaseReference.orderByChild(ORDER_BY_TIME).startAt(lastTime).limitToFirst(MyConstans.ADS_LIMIT);

        }else if(cat.equals(MyConstans.MY_ADS)){
            mquery = databaseReference.orderByChild(mAuth.getUid()+ "/ads/uid").equalTo(mAuth.getUid());

        }
        else if(cat.equals(MyConstans.MY_FAVS)){
            mquery = databaseReference.orderByChild(FAV_ADS_PATH + "/" + mAuth.getUid() + "/" + USER_FAV_ID).equalTo(mAuth.getUid());


        }else {
            if (lastTime.equals("0")) {
                mquery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(cat).endAt(cat + "\uf8ff");
            } else {
                mquery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(cat).endAt(cat + "\uf8ff");

            }

        }
        readDataUpdate();





    }





    public void readDataUpdate() {
        mquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (newPostList.size() > 0) newPostList.clear();


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NewPost newPost = null;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (newPost == null)
                            newPost = dataSnapshot1.child("ads").getValue(NewPost.class);
                    }


                    Status_Item status_item = dataSnapshot.child("status").getValue(Status_Item.class);
                    if(mAuth.getUid() != null) {
                        String favUid = (String) dataSnapshot.child(FAV_ADS_PATH).child(mAuth.getUid()).child(USER_FAV_ID).getValue();
                        if(newPost!= null) newPost.setFavCounter(dataSnapshot.child(FAV_ADS_PATH).getChildrenCount());
                        if(favUid!= null&& newPost !=null)newPost.setFav(true);
                    }

                    if (newPost != null && status_item != null) {
                        newPost.setTotalViews(status_item.totalviews);
                        newPost.setTotalCalls(status_item.totalcalls);
                        newPost.setTotalEmails(status_item.totalemails);


                    }
                    newPostList.add(newPost);


                }
                dataSender.onDataReceived(newPostList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void updateTotalViews(final NewPost newPost) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int totalviews;
        try {
            totalviews = Integer.parseInt(newPost.getTotalViews());

        } catch (NumberFormatException e) {
            totalviews = 0;

        }
        totalviews++;
        Status_Item status_item = new Status_Item();
        status_item.totalviews = String.valueOf(totalviews);
        status_item.totalcalls = newPost.getTotalCalls();
        status_item.totalemails = newPost.getTotalEmails();
        status_item.cat_time = newPost.getCat() + "_" + newPost.getTime();
        status_item.filter_time = newPost.getTime();
        databaseReference.child(newPost.getKey()).child("status").setValue(status_item);

    }

    public void updateTotalCalls(final NewPost newPost) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int totalcalls;
        try {
            totalcalls = Integer.parseInt(newPost.getTotalCalls());

        } catch (NumberFormatException e) {
            totalcalls = 0;

        }
        totalcalls++;
        Status_Item status_item = new Status_Item();
        status_item.totalcalls = String.valueOf(totalcalls);
        status_item.totalviews = newPost.getTotalViews();
        status_item.totalemails = newPost.getTotalEmails();
        status_item.cat_time = newPost.getCat() + "_" + newPost.getTime();
        status_item.filter_time = newPost.getTime();
        databaseReference.child(newPost.getKey()).child("status").setValue(status_item);

    }



    public void updateTotalEmails(final NewPost newPost) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int totalemails;
        try {
            totalemails = Integer.parseInt(newPost.getTotalEmails());

        } catch (NumberFormatException e) {
            totalemails = 0;

        }
        totalemails++;
        Status_Item status_item = new Status_Item();
        status_item.totalemails = String.valueOf(totalemails);
        status_item.totalviews = newPost.getTotalViews();
        status_item.totalcalls = newPost.getTotalCalls();
        status_item.cat_time = newPost.getCat() + "_" + newPost.getTime();
        status_item.filter_time = newPost.getTime();
        databaseReference.child(newPost.getKey()).child("status").setValue(status_item);

    }

    public void updateFav(final NewPost newPost, PostAdapter.ViewHolderData holder) {
        if(newPost.isFav()){
            DeleteFav(newPost,holder);

        } else {
            addFav(newPost,holder);
        }

    }
    private  void addFav( final NewPost newPost, final PostAdapter.ViewHolderData holder){
        if (mAuth.getUid() == null) return;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        databaseReference.child(newPost.getKey()).child(FAV_ADS_PATH).child(mAuth.getUid()).
                child(USER_FAV_ID).setValue(mAuth.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    holder.imFav.setImageResource(R.drawable.ic_fav_selected);
                    newPost.setFav(true);

                }

            }
        });



    }


    private void DeleteFav(NewPost newPost,  final PostAdapter.ViewHolderData holder) {
        if (mAuth.getUid() == null) return;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        databaseReference.child(newPost.getKey()).child(FAV_ADS_PATH).child(mAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    holder.imFav.setImageResource(R.drawable.ic_fav_no_selected);
                    newPost.setFav(false);


                }

            }
        });

    }
}