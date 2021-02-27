package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import com.tony_fire.descorder.R;
import com.utils.ImagesManager;
import com.utils.OnBitmapLoaded;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter implements OnBitmapLoaded {
    private Activity context;
    private LayoutInflater inflater;
    private List<Bitmap> bmList;
    private ImagesManager imagesManager;



    public ImageAdapter(Activity context) {
        this.context = context;
        imagesManager = new ImagesManager(context,this);
        inflater = LayoutInflater.from(context);
        bmList = new ArrayList<>();
    }

    @Override
    public int getCount() {


        return bmList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_item_layout,container,false);
        ImageView im = view.findViewById(R.id.imageviewPager);
        im.setImageBitmap(bmList.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    public void UpdateImages(List<String> images){

            imagesManager.resizeMultiLargeImage(images);

        }


    @Override
    public void OnBitmapLoaded( final List<Bitmap> bitmap) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bmList.clear();
                bmList.addAll(bitmap);
                notifyDataSetChanged();

            }
        });


    }


}
