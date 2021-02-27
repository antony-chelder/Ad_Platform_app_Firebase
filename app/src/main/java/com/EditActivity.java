package com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.adapter.ImageAdapter;
import com.db.DbManager;
import com.db.NewPost;
import com.db.Status_Item;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.screens.Choose_images;
import com.tony_fire.descorder.R;
import com.utils.ImagesManager;
import com.utils.MyConstans;
import com.utils.OnBitmapLoaded;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnBitmapLoaded {
    public final int MAX_UPLOAD_IMAGE_SIZE = 1920;

    private StorageReference mStorageRef;
    private String[] uploadUri = new String[3];
    private String[] uploadNewUri = new String[3];
    private Spinner spinner;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private EditText edtitle,edprice,edtel,edDesc,edEmail;
    private Boolean editstate = false;
    private String temp_cat = "";
    private String temp_uid = "";
    private String temp_totalviews = "";
    private String temp_time = "";
    private String temp_key = "";
    private Boolean IsimageUpdate = false;
    private ProgressDialog progressDialog;
    private int loadimagecounter = 0;
    private List<String> imagesUris;
    private ImageAdapter imageAdapter;
    private TextView images_conter;
    private ViewPager vp;
    private List<Bitmap> bitmapArrayList;
    private ImagesManager imagesManager;
    private boolean isImagesloaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();
    }

    private void init() {
        images_conter = findViewById(R.id.tvImagesCounter);
        imagesUris = new ArrayList<>();
        bitmapArrayList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this);
        imagesManager = new ImagesManager(this,this);
        vp = findViewById(R.id.view_pager2);
        vp.setAdapter(imageAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                images_conter.setText(dataText);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        uploadUri[0] = "empty";
        uploadUri[1] = "empty";
        uploadUri[2] = "empty";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Идет загрузка...");
        edtitle = findViewById(R.id.ItemTitile);
        edprice = findViewById(R.id.itemPrice);
        edtel = findViewById(R.id.ItemTel);
        edDesc = findViewById(R.id.edDesc);
        edEmail = findViewById(R.id.Tvemail);
        edtitle = findViewById(R.id.ItemTitile);


        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.category_spinnet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");


        getMyIntent();


    }

    private void getMyIntent() {
        if (getIntent() != null) {
            Intent i = getIntent();
            editstate = i.getBooleanExtra(MyConstans.EDITState, false);
            if (editstate) SetDataAdds(i);
        }

    }

    private void SetDataAdds(Intent i) {
        //Picasso.get().load(i.getStringExtra(MyConstans.IMAGEID)).into(imItem);
        NewPost newPost = (NewPost)i.getSerializableExtra(MyConstans.NEW_POST_INTENT);
        if(newPost==null)return;
        edtel.setText(newPost.getTel());
        edtitle.setText(newPost.getTitle());
        edprice.setText(newPost.getPrice());
        edEmail.setText(newPost.getEmail());
        edDesc.setText(newPost.getDesc());
        temp_cat = newPost.getCat();
        temp_totalviews = newPost.getTotalViews();
        temp_uid = newPost.getUid();
        temp_time = newPost.getTime();
        temp_key = newPost.getKey();

        uploadUri[0] = newPost.getImageId();
        uploadUri[1] =newPost.getImageId2();
        uploadUri[2] = newPost.getImageId3();
        for (String s : uploadUri) {
            if (!s.equals("empty")) imagesUris.add(s);
        }
        isImagesloaded = true;
        imageAdapter.UpdateImages(imagesUris);
        String dataText;
        if (imagesUris.size() > 0) {

            dataText = 1 + "/" + imagesUris.size();
        } else {
            dataText = 0 + "/" + imagesUris.size();
        }
        images_conter.setText(dataText);


    }

    private void uploadImage() {
        if (loadimagecounter < uploadUri.length) {
            if (!uploadUri[loadimagecounter].equals("empty")) {
                Bitmap bitmap = bitmapArrayList.get(loadimagecounter);


                ByteArrayOutputStream out = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                byte[] byteArray = out.toByteArray();
                final StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "_image");
                UploadTask up = mRef.putBytes(byteArray);
                Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return mRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.getResult() == null) return;

                        uploadUri[loadimagecounter] = task.getResult().toString();

                        loadimagecounter++;

                        if (loadimagecounter < uploadUri.length) {
                            uploadImage();
                        } else {
                            SavePost();
                            Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } else {
                loadimagecounter++;
                uploadImage();
            }
        } else {
            SavePost();
            finish();
        }

    }

    private void uploadUpdateImage() {
        Bitmap bitmap = null;

        if (loadimagecounter < uploadUri.length) {

            // Если ссылка на старой позиции равна ссылке на новой позиции
            if (uploadUri[loadimagecounter].equals(uploadNewUri[loadimagecounter])) {
                loadimagecounter++;
                uploadUpdateImage();

                // Если ссылка на старой позиции  НЕ равна ссылке на новой позиции и ссылка на новой позиции НЕ "empty"
            } else if (!uploadUri[loadimagecounter].equals(uploadNewUri[loadimagecounter]) &&
                    !uploadNewUri[loadimagecounter].equals("empty")) {
                bitmap = bitmapArrayList.get(loadimagecounter);

            }
            //Если в старом массиве НЕ "empty",а новой на той же позиции "empty,значит удалить старую картинку и ссылку"
            else if (!uploadUri[loadimagecounter].equals("empty") && uploadNewUri[loadimagecounter].equals("empty")) {
                StorageReference mRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[loadimagecounter]);
                mRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        uploadUri[loadimagecounter] = "empty";
                        loadimagecounter++;
                        if (loadimagecounter < uploadUri.length) {
                            uploadUpdateImage();

                        } else {
                            UpdatePost();

                        }


                    }
                });

            }
            if (bitmap == null) return;


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            byte[] byteArray = out.toByteArray();
            final StorageReference mRef;
            //2 - A Если ссылка на старой позиции НЕ равна "empty",то перезаписываем старую ссылку на новую
            if (!uploadUri[loadimagecounter].equals("empty")) {
                mRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[loadimagecounter]);

            }
            //2 - B Если ссылка на старой позиции равна "empty",то записываем новую картинку на Firebase Storage
            else {
                mRef = mStorageRef.child(System.currentTimeMillis() + "_image");

            }


            UploadTask up = mRef.putBytes(byteArray);
            Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri[loadimagecounter] = task.getResult().toString();

                    loadimagecounter++;
                    if (loadimagecounter < uploadUri.length) {
                        uploadUpdateImage();

                    } else {
                        UpdatePost();

                    }


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {
            UpdatePost();

        }

    }

    private String[] getUrisFromChoose(Intent data) {
        if (editstate) {
            uploadNewUri[0] = data.getStringExtra("urimain");
            uploadNewUri[1] = data.getStringExtra("uriimage1");
            uploadNewUri[2] = data.getStringExtra("uriimage2");
            return uploadNewUri;
        } else {
            uploadUri[0] = data.getStringExtra("urimain");
            uploadUri[1] = data.getStringExtra("uriimage1");
            uploadUri[2] = data.getStringExtra("uriimage2");
            return uploadUri;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15 && data != null) {
            if (resultCode == RESULT_OK) {
                IsimageUpdate = true;

                imagesUris.clear();
                String[] tempUriArray = getUrisFromChoose(data);
                isImagesloaded = false;
                imagesManager.resizeMultiLargeImage(Arrays.asList(tempUriArray));
                for (String s : tempUriArray)  {
                    if (!s.equals("empty")) imagesUris.add(s);
                }
                imageAdapter.UpdateImages(imagesUris);
                String dataText;
                if (imagesUris.size() > 0) {

                    dataText = vp.getCurrentItem() + 1 + "/" + imagesUris.size();
                } else {
                    dataText = 0 + "/" + imagesUris.size();
                }
                images_conter.setText(dataText);


            }
        }
    }

    public void onClickImage(View view) {
        Intent i = new Intent(EditActivity.this, Choose_images.class);

        i.putExtra(MyConstans.IMAGEID, uploadUri[0]);
        i.putExtra(MyConstans.IMAGEID2, uploadUri[1]);
        i.putExtra(MyConstans.IMAGEID3, uploadUri[2]);

        startActivityForResult(i, 15);


    }

    private void UpdatePost() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(DbManager.MAIN_ADS_PATH);



            NewPost post = new NewPost();
            post.setImageId(uploadUri[0]);
            post.setImageId2(uploadUri[1]);
            post.setImageId3(uploadUri[2]);
            post.setTitle(edtitle.getText().toString());
            post.setTel(edtel.getText().toString());
            post.setPrice(edprice.getText().toString());
            post.setEmail(edEmail.getText().toString());
            post.setDesc(edDesc.getText().toString());

            post.setKey(temp_key);
            post.setCat(temp_cat);
            post.setTime(temp_time);
            post.setUid(temp_uid);
            post.setTotalViews(temp_totalviews);
            post.setTotalViews("0");
            databaseReference.child(temp_key).child(mAuth.getUid()).child("ads").setValue(post).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
                                finish();
                            }


                    });
        }
    }





    private void SavePost() {
        databaseReference = FirebaseDatabase.getInstance().getReference(DbManager.MAIN_ADS_PATH);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getUid() != null) {
            String key = databaseReference.push().getKey();

                NewPost post = new NewPost();

                post.setImageId(uploadUri[0]);
                post.setImageId2(uploadUri[1]);
                post.setImageId3(uploadUri[2]);
                post.setTitle(edtitle.getText().toString());
                post.setTel(edtel.getText().toString());
                post.setPrice(edprice.getText().toString());
                post.setEmail(edEmail.getText().toString());
                post.setDesc(edDesc.getText().toString());
                post.setKey(key);
                post.setCat(spinner.getSelectedItem().toString());
                post.setTime(String.valueOf(System.currentTimeMillis()));
                post.setUid(mAuth.getUid());

                if (key != null) {
                    Status_Item status_item = new Status_Item();
                    status_item.cat_time = post.getCat() + "_" + post.getTime();
                    status_item.filter_time = post.getTime();
                    databaseReference.child(key).child(mAuth.getUid()).child("ads").setValue(post);
                    databaseReference.child(key).child("status").setValue(status_item);
                }


                Intent i = new Intent();
                i.putExtra("cat", spinner.getSelectedItem().toString());
                setResult(RESULT_OK, i);
            }


    }

    public void onClickSave(View view) {
        if (isImagesloaded && !edtitle.getText().toString().isEmpty() &&
                !edtel.getText().toString().isEmpty() && !edprice.getText().toString().isEmpty() && !edDesc.getText().toString().isEmpty()  ) {
            progressDialog.show();
            if (!editstate) {
                uploadImage();
            } else {
                if (IsimageUpdate) {
                    uploadUpdateImage();
                } else {
                    UpdatePost();
                }

            }
        }else {
            Toast.makeText(this, R.string.message_of_empty, Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public void OnBitmapLoaded( final List<Bitmap> bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bitmapArrayList.clear();
                bitmapArrayList.addAll(bitmap);
                isImagesloaded = true;

            }
        });


    }
}
