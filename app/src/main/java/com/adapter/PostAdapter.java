package com.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.db.DbManager;
import com.EditActivity;
import com.db.NewPost;
import com.ShowAddActivityActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.tony_fire.descorder.MainActivity;
import com.tony_fire.descorder.R;
import com.utils.MyConstans;

import java.util.List;



public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData>   {
    public static final String NEXT_PAGE = "next_page";
    public static final String BACK_PAGE = "back_page";
    private List<NewPost> mainPostList;
    private Context context;
    private onItemClickCustom onItemClickCustom;
    private DbManager dbManager;
    private int myviewType = 0;
    private int VIEW_ADS = 0;
    private int VIEW_BUTTON = 1;
    public boolean isFirstPage = true;
    private final int NEXT_ADS = 1;
    private final int BACK_ADS = 2;
    private int adsButtonState = 0;



    public PostAdapter(List<NewPost> arrayPost, Context context,onItemClickCustom onItemClickCustom ) {
        this.mainPostList = arrayPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;

    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==VIEW_BUTTON){
            view = LayoutInflater.from(context).inflate(R.layout.end_ads_item,parent,false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_ads,parent,false);

        }
        return new  ViewHolderData(view,onItemClickCustom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {

        switch (mainPostList.get(position).getUid()){
            case NEXT_PAGE :  holder.setNextItemData();
                break;

            case BACK_PAGE: holder.setStartItemData();
                break;
            default:   holder.setData(mainPostList.get(position));
                SetFavifSelected(holder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mainPostList.get(position).getUid().equals(NEXT_PAGE)||mainPostList.get(position).getUid().equals(BACK_PAGE)){
            myviewType = 1;

        }
        else {
            myviewType = 0;
        }
        return myviewType;
    }

    @Override
    public int getItemCount() {
        return mainPostList.size();
    }


    private void DeleteDialog( final NewPost newPost, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_massage);
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbManager.DeleteItem(newPost);
                mainPostList.remove(position);
                notifyItemRemoved(position);


            }
        });

        builder.show();
    }

    public interface onItemClickCustom{
        void onItemSelected(int position);



    }
    public void Updateadapter(List<NewPost> newPostListdata){
        mainPostList.clear();
        if(!isFirstPage&& newPostListdata.size()==MyConstans.ADS_LIMIT || adsButtonState == NEXT_ADS&& !isFirstPage){
            NewPost tempPost = new NewPost();
            tempPost.setUid(BACK_PAGE);
            mainPostList.add(tempPost);

        }else if(!isFirstPage && newPostListdata.size()<MyConstans.ADS_LIMIT&& adsButtonState == BACK_ADS){

            loadFirstPage();

        }

        if(newPostListdata.size() == MyConstans.ADS_LIMIT){
            NewPost tempPost = new NewPost();
            tempPost.setUid(NEXT_PAGE);
            newPostListdata.add(tempPost);
        }
        mainPostList.addAll(newPostListdata);
        notifyDataSetChanged();
        adsButtonState = 0;



    }

    private void loadFirstPage(){
        isFirstPage = true;

        dbManager.getDataFromDb(((MainActivity)context).current_cat,"0");
    }

    public void setDbManager(DbManager dbManager){

        this.dbManager = dbManager;


    }

    private void SetFavifSelected(ViewHolderData holder){
        if (mainPostList.get(holder.getAdapterPosition()).isFav()){
            holder.imFav.setImageResource(R.drawable.ic_fav_selected);
        }
        else {
           holder.imFav.setImageResource(R.drawable.ic_fav_no_selected);

        }
    }

    //ViewHolderClass

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvTitle,tvPriceTel,tvDesc,tvView,tvTel,tvFavCounter,tvCountryCity;
        private ImageView imAds,imEye,imLoc;
        public ImageButton deletbutton,editbutton,imFav;
        private LinearLayout editlayout;
        private ConstraintLayout editpanel;
        private onItemClickCustom onItemClickCustom;


        public ViewHolderData(@NonNull View itemView,onItemClickCustom onItemClickCustom) {
            super(itemView);
            tvCountryCity = itemView.findViewById(R.id.text_country_city_ad_item);
            imLoc = itemView.findViewById(R.id.loc_image);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFavCounter = itemView.findViewById(R.id.tv_favCounter);
            tvView = itemView.findViewById(R.id.tvTotalViews);
            tvPriceTel = itemView.findViewById(R.id.tvPrice1);
            tvTel = itemView.findViewById(R.id.tvTel1);
            tvDesc = itemView.findViewById(R.id.tvDesx);
            deletbutton = itemView.findViewById(R.id.ImDeleteitem);
            editbutton = itemView.findViewById(R.id.imItemEdit);
            editlayout = itemView.findViewById(R.id.editLayout);
            editpanel = itemView.findViewById(R.id.edit_panel);
            imFav = itemView.findViewById(R.id.imFav);
            imAds = itemView.findViewById(R.id.imAds);
            imEye = itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;


        }
        public void setStartItemData(){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbManager.getBackDataFromDb(((MainActivity)context).current_cat,mainPostList.get(1).getTime());

                    ((MainActivity)context).rc.scrollToPosition(0);
                    isFirstPage = false;
                    adsButtonState = BACK_ADS;
                }
            });
        }
        public void setNextItemData(){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     dbManager.getDataFromDb(((MainActivity)context).current_cat,mainPostList.get(mainPostList.size()-2).getTime());

                    ((MainActivity)context).rc.scrollToPosition(0);
                    isFirstPage = false;
                    adsButtonState = NEXT_ADS;
                }
            });
        }
        public void setData( final NewPost newPost){
            FirebaseUser user = ((MainActivity)context).getmAuth().getCurrentUser();
            if(user !=null){
                editlayout.setVisibility(newPost.getUid().equals(user.getUid())? View.VISIBLE:View.GONE);
                imFav.setVisibility(user.isAnonymous()?View.GONE:View.VISIBLE);
                tvFavCounter.setVisibility(user.isAnonymous()?View.GONE:View.VISIBLE);
                imFav.setEnabled(!newPost.getUid().equals(user.getUid()) && !user.isAnonymous());
            }
            

            Picasso.get().load(newPost.getImageId()).into(imAds);
            String country = newPost.getSelectcountry() + "," + newPost.getSelectcity();
            tvCountryCity.setText(country);
            tvTitle.setText(newPost.getTitle());
            tvFavCounter.setText(String.valueOf(newPost.getFavCounter()));
            String price = "Price:"+ newPost.getPrice();
            String tel = "Telephone:"+ newPost.getTel();
            tvPriceTel.setText(price);
            tvTel.setText(tel);
            String textdesc;
            if(newPost.getDesc().length()>50){
                textdesc = newPost.getDesc().substring(0,50) + "...";
            }
            else{
                textdesc = newPost.getDesc();

            }
            tvDesc.setText(textdesc);
            tvView.setText(newPost.getTotalViews());



            deletbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DeleteDialog(newPost,getAdapterPosition());
                }
            });
            editbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(MyConstans.NEW_POST_INTENT,newPost);
                    i.putExtra(MyConstans.EDITState,true);
                    context.startActivity(i);

                }
            });
            imFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTvFavCounter(newPost,tvFavCounter);
                    dbManager.updateFav(newPost,ViewHolderData.this);



                }
            });

        }

        private void setTvFavCounter(NewPost newPost,TextView tvFavCounter){
            int fCount = Integer.parseInt(tvFavCounter.getText().toString());
            fCount = (newPost.isFav()) ? --fCount :++fCount ;
            tvFavCounter.setText(String.valueOf(fCount));

        }


        @Override
        public void onClick(View view) {

            NewPost newPost = mainPostList.get(getAdapterPosition());
            FirebaseUser user = ((MainActivity)context).getmAuth().getCurrentUser();
            if(!newPost.getUid().equals(user.getUid())){
            dbManager.updateTotalViews(newPost);
            int totalviews = Integer.parseInt(newPost.getTotalViews());
            totalviews++;
            newPost.setTotalViews(String.valueOf(totalviews));
            Intent i = new Intent(context, ShowAddActivityActivity.class);
            i.putExtra(MyConstans.NEW_POST_INTENT,newPost);
            context.startActivity(i);
            onItemClickCustom.onItemSelected(getAdapterPosition());
            }else if(newPost.getUid().equals(user.getUid()) || user.isAnonymous()) {
                Intent i = new Intent(context, ShowAddActivityActivity.class);
                i.putExtra(MyConstans.NEW_POST_INTENT,newPost);
                context.startActivity(i);
                onItemClickCustom.onItemSelected(getAdapterPosition());

            }




        }

    }

    public void ClearAdapter(){
        mainPostList.clear();
        notifyDataSetChanged();

    }
    public List<NewPost> getMainList(){
        return mainPostList;
    }
}
