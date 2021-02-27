package com.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.squareup.picasso.Picasso;
import com.tony_fire.descorder.R;
import com.utils.ImagesManager;
import com.utils.MyConstans;
import com.utils.OnBitmapLoaded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Choose_images extends AppCompatActivity {
    String[] uris = new String[3];
    private ImageView imMain,im2,im3;
    private ImageView[] imageViews = new ImageView[3];
    private ImagesManager imagesManager;
    private final int MAX_IMAGE_SIZE = 2000;
    private OnBitmapLoaded onBitmapLoaded;
    private boolean isImagesLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_images);
        init();
        getMyIntent();
    }
    private void init(){
        imMain = findViewById(R.id.main_image);
        im2 = findViewById(R.id.image2);
        im3 = findViewById(R.id.image3);
        uris[0] = "empty";
        uris[1] = "empty";
        uris[2] = "empty";
        imageViews[0] = imMain;
        imageViews[1] = im2;
        imageViews[2] = im3;


        setOnBitMapLoaded();
        imagesManager = new ImagesManager(this,onBitmapLoaded);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(returnValue == null) return;
           switch (requestCode){
               case 1:
                   uris[0] = returnValue.get(0);
                   isImagesLoaded = false;

                   imagesManager.resizeMultiLargeImage(Arrays.asList(uris));

                   break;
               case 2:
                   uris[1] = returnValue.get(0);
                   isImagesLoaded = false;

                   imagesManager.resizeMultiLargeImage(Arrays.asList(uris));
                   break;
               case 3:
                   uris[2] = returnValue.get(0);
                   isImagesLoaded = false;

                   imagesManager.resizeMultiLargeImage(Arrays.asList(uris));
                   break;




            }
        }

    }
    private void setOnBitMapLoaded(){
        onBitmapLoaded = new OnBitmapLoaded() {
            @Override
            public void OnBitmapLoaded(List<Bitmap> bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i<bitmap.size();i++) {
                            if(bitmap.get(i)!= null)imageViews[i].setImageBitmap(bitmap.get(i));
                        }
                        isImagesLoaded = true;
                    }
                });

            }
        };
    }


    public void onClickMainImage(View view) {
        if(!isImagesLoaded){
            Toast.makeText(this, "Loading images,please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        getImage(1);
    }

    public void onClickImage2(View view) {
        if(!isImagesLoaded){
            Toast.makeText(this, "Loading images,please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        getImage(2);
    }

    public void onClickImage3(View view) {
        if(!isImagesLoaded){
            Toast.makeText(this, "Loading images,please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        getImage(3);
    }
    private  void getImage(int index){
        /*Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(i,index);*/
        Options options = Options.init()
                .setRequestCode(index)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on star//Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);     //Orientaion         //Custom Path For media Storage

        Pix.start(Choose_images.this, options);
    }
    private void getMyIntent(){
        Intent i = getIntent();
        if(i !=null){


             uris[0] = i.getStringExtra(MyConstans.IMAGEID);
             uris[1] = i.getStringExtra(MyConstans.IMAGEID2);
             uris[2] = i.getStringExtra(MyConstans.IMAGEID3);
             isImagesLoaded = false;
             imagesManager.resizeMultiLargeImage(sortImages(uris));

        }

    }
    private List<String> sortImages(String[] uris){
        List<String> templist = new ArrayList<>();
        for(int i = 0;i<uris.length;i++){
            if(uris[i].startsWith("http")){
                showHttpImages(uris[i],i);
                templist.add("empty");

            }else{
                templist.add(uris[i]);
            }

        }
        return templist;


    }
    private void showHttpImages(String uri,int position){

            Picasso.get().load(uri).into(imageViews[position]);


    }

    public void onClickBack(View view) {
        Intent i = new Intent();
        i.putExtra("urimain",uris[0]);
        i.putExtra("uriimage1",uris[1]);
        i.putExtra("uriimage2",uris[2]);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onClickRemoveImage(View view) {
        imMain.setImageResource(R.drawable.ic_choose_image);
        uris[0] = "empty";
    }


    public void onClickImage1(View view) {
        im2.setImageResource(R.drawable.ic_choose_image);
        uris[1] = "empty";

    }
    public void onClickImage4(View view) {
        im3.setImageResource(R.drawable.ic_choose_image);
        uris[2] = "empty";
    }
}