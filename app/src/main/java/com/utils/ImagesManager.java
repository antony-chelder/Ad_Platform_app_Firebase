package com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImagesManager {
    private Context context;
    private final int MAX_SIZE = 1000;
    private int width;
    private int height;
    private OnBitmapLoaded onBitmapLoaded;
    List<Bitmap> bmlist ;


    public ImagesManager(Context context,OnBitmapLoaded onBitmapLoaded) {
        this.context = context;
        this.onBitmapLoaded = onBitmapLoaded;
        bmlist = new ArrayList<>();


    }


    public int [] getImageSize(String uri){
        int[] size = new int[2];
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(uri));
            BitmapFactory.decodeStream(inputStream,null,options);
            if(getImageRotation(uri)== 0 ) {
                size[0] = options.outWidth;
                size[1] = options.outHeight;
            }
            else {
                size[1] = options.outWidth;
                size[0] = options.outHeight;
            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void resizeMultiLargeImage( final List<String> uris){
        List<int[]> sizeList = new ArrayList<>();
        for(int i = 0; i <uris.size(); i++){
          int[] sizes = getImageSize(uris.get(i));
          width = sizes[0];
          height = sizes[1];
          sizeList.add(new int[]{width,height});
        float imageRatio = (float) width / (float) height;
        if(imageRatio>1) {
            if (width > MAX_SIZE)
                width = MAX_SIZE;
            height = (int) (width / imageRatio);

        } else {
            if(height>MAX_SIZE) {
                height = MAX_SIZE;
                width = (int) (height * imageRatio);
            }

        }

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bmlist.clear();
                    for(int i = 0;i<sizeList.size();i++) {
                        if(!uris.get(i).equals("empty")&& !uris.get(i).startsWith("http")&& sizeList.get(i)[0]>MAX_SIZE|| sizeList.get(i)[1]>MAX_SIZE) {
                            Bitmap bm = Picasso.get().load(Uri.fromFile(new File(uris.get(i)))).resize(sizeList.get(i)[0], sizeList.get(i)[1]).get();
                            bmlist.add(bm);
                        } else if(uris.get(i).startsWith("http")){
                            Bitmap bm = Picasso.get().load(uris.get(i)).get();
                            bmlist.add(bm);

                        }else if(!uris.get(i).equals("empty")
                                &&!uris.get(i).startsWith("http")&& sizeList.get(i)[0]<MAX_SIZE&&sizeList.get(i)[1]<MAX_SIZE){
                            Bitmap bm = Picasso.get().load(Uri.fromFile(new File(uris.get(i)))).get();
                            bmlist.add(bm);

                        }
                        else {
                            bmlist.add(null);
                        }
                    }
                    onBitmapLoaded.OnBitmapLoaded(bmlist);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private int getImageRotation(String imagePath){
        int rotation = 0;
        File imageFile = new File(imagePath);
        try {

            ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
            int Orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            if(ExifInterface.ORIENTATION_ROTATE_90 == Orientation||ExifInterface.ORIENTATION_ROTATE_270 == Orientation){
                rotation = 90;

            }
            else {
                rotation = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotation;



}
  }

