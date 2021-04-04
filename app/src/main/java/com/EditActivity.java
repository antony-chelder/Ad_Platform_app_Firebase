package com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.tony_fire.descorder.databinding.EditLayoutBinding;
import com.utils.CountryManager;
import com.utils.DialogCountryHelper;
import com.utils.ImagesManager;
import com.utils.MyConstans;
import com.utils.OnBitmapLoaded;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnBitmapLoaded {
    private EditLayoutBinding rootElement;
    public final int MAX_UPLOAD_IMAGE_SIZE = 1920;

    private StorageReference mStorageRef;
    private String[] uploadUri = new String[3];
    private String[] uploadNewUri = new String[3];
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
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
    private List<Bitmap> bitmapArrayList;
    private ImagesManager imagesManager;
    private boolean isImagesloaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootElement = EditLayoutBinding.inflate(getLayoutInflater());
        setContentView(rootElement.getRoot());
        init();
    }

    private void init() {
        imagesUris = new ArrayList<>();
        bitmapArrayList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this);
        imagesManager = new ImagesManager(this, this);
        rootElement.viewPager2.setAdapter(imageAdapter);
        rootElement.viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                rootElement.tvImagesCounter.setText(dataText);

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.category_spinnet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rootElement.spinner.setAdapter(adapter);

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
        NewPost newPost = (NewPost) i.getSerializableExtra(MyConstans.NEW_POST_INTENT);
        if (newPost == null) return;
        rootElement.ItemTel.setText(newPost.getTel());
        rootElement.ItemTitile.setText(newPost.getTitle());
        rootElement.itemPrice.setText(newPost.getPrice());
        rootElement.Tvemail.setText(newPost.getEmail());
        rootElement.edDesc.setText(newPost.getDesc());
        rootElement.spinner.setEnabled(false);
        rootElement.selectCountry.setText(newPost.getSelectcountry());
        rootElement.selectCity.setText(newPost.getSelectcity());
        rootElement.indexChoose.setText(newPost.getIndex());


        temp_cat = newPost.getCat();
        temp_totalviews = newPost.getTotalViews();
        temp_uid = newPost.getUid();
        temp_time = newPost.getTime();
        temp_key = newPost.getKey();

        uploadUri[0] = newPost.getImageId();
        uploadUri[1] = newPost.getImageId2();
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
        rootElement.tvImagesCounter.setText(dataText);


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
                           PublishPost();
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
            PublishPost();
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
                            PublishPost();

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
                        PublishPost();

                    }


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {
            PublishPost();

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
                for (String s : tempUriArray) {
                    if (!s.equals("empty")) imagesUris.add(s);
                }
                imageAdapter.UpdateImages(imagesUris);
                String dataText;
                if (imagesUris.size() > 0) {

                    dataText = rootElement.viewPager2.getCurrentItem() + 1 + "/" + imagesUris.size();
                } else {
                    dataText = 0 + "/" + imagesUris.size();
                }
                rootElement.tvImagesCounter.setText(dataText);


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

    private void PublishPost() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(DbManager.MAIN_ADS_PATH);
            NewPost post = new NewPost();
            post.setImageId(uploadUri[0]);
            post.setImageId2(uploadUri[1]);
            post.setImageId3(uploadUri[2]);
            post.setTitle(rootElement.ItemTitile.getText().toString());
            post.setCat(rootElement.spinner.getSelectedItem().toString());
            post.setSelectcity(rootElement.selectCity.getText().toString());
            post.setSelectcountry(rootElement.selectCountry.getText().toString());
            post.setIndex(rootElement.indexChoose.getText().toString());
            post.setTel(rootElement.ItemTel.getText().toString());
            post.setPrice(rootElement.itemPrice.getText().toString());
            post.setEmail(rootElement.Tvemail.getText().toString());
            post.setDesc(rootElement.edDesc.getText().toString());

            if (editstate) {
                updatePost(post);
            } else {
                SavePost(post);
            }
        }
    }

    private void updatePost(NewPost post) {

        if(mAuth.getUid()==null)return;
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



    private void SavePost(NewPost post) {

        if(mAuth.getUid()==null)return;
        String key = databaseReference.push().getKey();
        post.setKey(key);
        post.setCat(rootElement.spinner.getSelectedItem().toString());
        post.setTime(String.valueOf(System.currentTimeMillis()));
        post.setUid(mAuth.getUid());

        if (key != null) {
            Status_Item status_item = new Status_Item();
            status_item.cat_time = post.getCat() + "_" + post.getTime();
            status_item.filter_time = post.getTime();
            status_item.title_time= post.getTitle().toLowerCase() + "_" + post.getTime();

            status_item.country_title_time =  (post.getSelectcountry() + "_" +  post.getTitle().toLowerCase() + "_" + post.getTime()).toLowerCase();
            status_item.country_city_title_time= (post.getSelectcountry()  + "_" + post.getSelectcity() + "_" +  post.getTime()).toLowerCase();
            status_item.country_city_index_title_time = (post.getSelectcountry()  + "_" + post.getSelectcity() + "_"  + post.getIndex() + "_"
                    + post.getTitle() + "_" + post.getTime()).toLowerCase();
            databaseReference.child(key).child(mAuth.getUid()).child("ads").setValue(post);
            databaseReference.child(key).child("status").setValue(status_item);
        }


        Intent i = new Intent();
        i.putExtra("cat", rootElement.spinner.getSelectedItem().toString());
        setResult(RESULT_OK, i);
    }






    public void onClickSave(View view) {
        if(!isFieldEmpty()){
            Toast.makeText(this, R.string.message_of_empty, Toast.LENGTH_LONG).show();
            return;
        }
        if (isImagesloaded) {
            progressDialog.show();
            if (!editstate) {
                uploadImage();
            } else {
                if (IsimageUpdate) {
                    uploadUpdateImage();
                } else {
                    PublishPost();
                }

            }
        }



    }

    private Boolean isFieldEmpty(){
        String country = rootElement.selectCountry.getText().toString();
        String city = rootElement.selectCity.getText().toString();
        String title = rootElement.ItemTitile.getText().toString();
        String price = rootElement.itemPrice.getText().toString();
        String desc = rootElement.edDesc.getText().toString();
        String tel = rootElement.ItemTel.getText().toString();
        String index = rootElement.indexChoose.getText().toString();

         return (!country.equals(getString(R.string.select_country)) && !TextUtils.isEmpty(index)  && !city.equals(getString(R.string.select_city)) && !TextUtils.isEmpty(title)
                && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(tel));

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

    public void onClickSetCountry(View view) {
        String city = rootElement.selectCity.getText().toString();
        if(!city.equals(getString(R.string.select_city))) {
            rootElement.selectCity.setText(R.string.select_city);
        }
            DialogCountryHelper.INSTANCE.showDialog(this, CountryManager.INSTANCE.getAllCountries(this), (TextView) view);
    }
    public void onClickSetCity(View view) {
        String country = rootElement.selectCountry.getText().toString();
        if(!country.equals(getString(R.string.select_country))) {
            DialogCountryHelper.INSTANCE.showDialog(this, CountryManager.INSTANCE.getAllCities(this,country ), (TextView) view);
        }else{
            Toast.makeText(this, "Страна не выбрана", Toast.LENGTH_LONG).show();
        }
    }

    public EditLayoutBinding getRootElement() {
        return rootElement;
    }
}
