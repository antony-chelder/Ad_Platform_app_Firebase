package com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.ImageAdapter;
import com.db.DbManager;
import com.db.NewPost;
import com.tony_fire.descorder.R;
import com.utils.MyConstans;

import java.util.ArrayList;
import java.util.List;

public class ShowAddActivityActivity extends AppCompatActivity {
    private TextView tvtitle, tvPrice, tvtel, tvDesc, tvEmail, tvCountry, tvCity, tvVillage;
    private ImageView imMain;
    private List<String> imagesUris;
    private ImageAdapter imageAdapter;
    private TextView images_conter;
    private String tel;
    private NewPost newPost;
    private DbManager dbManager;
    private boolean istotalEmailsAdded = false;
    private boolean istotalEmailsCalls = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showaddactiviyreal);
        init();
    }

    private void init() {
        dbManager = new DbManager(null, this);
        images_conter = findViewById(R.id.tvImagesCounter2);
        imagesUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(this);
        ViewPager vp = findViewById(R.id.view_pager);
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


        tvCountry = findViewById(R.id.text_country);
        tvCity = findViewById(R.id.text_city);
        tvVillage = findViewById(R.id.text_village);
        tvtitle = findViewById(R.id.tvTitle_1);
        tvPrice = findViewById(R.id.tvPrice);
        tvtel = findViewById(R.id.tvTel);
        tvEmail = findViewById(R.id.tvemail_text);
        tvDesc = findViewById(R.id.tvTitle_2);
        //imMain = findViewById(R.id.view_pager);
        if (getIntent() != null) {
            Intent i = getIntent();
            newPost = (NewPost) i.getSerializableExtra(MyConstans.NEW_POST_INTENT);
            if (newPost == null) return;
            tvCountry.setText(newPost.getSelectcountry());
            tvCity.setText(newPost.getSelectcity());
            tvtitle.setText(newPost.getTitle());
            tvVillage.setText(newPost.getIndex());
            tvPrice.setText(newPost.getPrice());
            tvDesc.setText(newPost.getDesc());
            tvtel.setText(newPost.getTel());
            tvEmail.setText(newPost.getEmail());
            tel = newPost.getTel();
            String[] images = new String[3];
            images[0] = newPost.getImageId();
            images[1] = newPost.getImageId2();
            images[2] = newPost.getImageId3();
            for (String s : images) {
                if (!s.equals("empty")) imagesUris.add(s);
            }
            imageAdapter.UpdateImages(imagesUris);
            String dataText;
            if (imagesUris.size() > 0) {

                dataText = 1 + "/" + imagesUris.size();
            } else {
                dataText = 0 + "/" + imagesUris.size();
            }
            images_conter.setText(dataText);
            //Picasso.get().load(i.getStringExtra(MyConstans.IMAGEID)).into(imMain);
        }

    }

    public void OnClckCall(View view) {
        if (!istotalEmailsCalls) {
            dbManager.updateTotalCalls(newPost);
            int totalcalls = Integer.parseInt(newPost.getTotalCalls());
            totalcalls++;
            newPost.setTotalCalls(String.valueOf(totalcalls));
            istotalEmailsCalls = true;
        }


        String temtell = "tel:" + tel;
        Intent icall = new Intent(Intent.ACTION_DIAL);
        icall.setData(Uri.parse(temtell));
        startActivity(icall);


    }

    public void OnClckEmail(View view) {
        if (!istotalEmailsAdded) {
            dbManager.updateTotalEmails(newPost);
            int totalemails = Integer.parseInt(newPost.getTotalEmails());
            totalemails++;
            newPost.setTotalEmails(String.valueOf(totalemails));
            istotalEmailsAdded = true;
        }


        Intent iemail = new Intent(Intent.ACTION_SEND);
        iemail.setType("message/rfc822");
        iemail.putExtra(Intent.EXTRA_EMAIL, new String[]{newPost.getEmail()});
        iemail.putExtra(Intent.EXTRA_SUBJECT, "About your ad");
        iemail.putExtra(Intent.EXTRA_TEXT, "I really interested of your ad!");
        try {
            startActivity(Intent.createChooser(iemail, "Open with:"));

        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.havent_app, Toast.LENGTH_SHORT).show();
        }


    }


}
