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
import com.squareup.picasso.Picasso;
import com.tony_fire.descorder.MainActivity;
import com.tony_fire.descorder.R;
import com.utils.MyConstans;

import java.util.List;



public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData>   {
    private List<NewPost> mainPostList;
    private Context context;
    private onItemClickCustom onItemClickCustom;
    private DbManager dbManager;
    private int myviewType = 0;
    private int VIEW_ADS = 0;
    private int VIEW_BUTTON = 1;
    private boolean isFirstPage = true;
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

        int index = 1;
        if(!isFirstPage)index = 2;
        if(position == mainPostList.size()-1 && mainPostList.size()- index == MyConstans.ADS_LIMIT){
            holder.setNextItemData();

        } else  if(position == 0&& !isFirstPage){
            holder.setStartItemData();

        }else {
            holder.setData(mainPostList.get(position));
            SetFavifSelected(holder);

        }




    }

    @Override
    public int getItemViewType(int position) {
        if(mainPostList.get(position).getUid() == null){
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
            mainPostList.add(new NewPost());

        }else if(!isFirstPage && newPostListdata.size()<MyConstans.ADS_LIMIT&& adsButtonState == BACK_ADS){
            loadFirstPage();


        }

        if(newPostListdata.size() == MyConstans.ADS_LIMIT)newPostListdata.add( new NewPost());
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
        private TextView tvTitle,tvPriceTel,tvDesc,tvView,tvTel;
        private ImageView imAds,imEye;
        public ImageButton deletbutton,editbutton,imFav;
        private LinearLayout editlayout;
        private ConstraintLayout editpanel;
        private onItemClickCustom onItemClickCustom;
        public String favPath;


        public ViewHolderData(@NonNull View itemView,onItemClickCustom onItemClickCustom) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
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

            if( newPost.getUid().equals(MainActivity.MAUTH))
            {
                editlayout.setVisibility(View.VISIBLE);

            }
            else {
                editlayout.setVisibility(View.GONE);

            }
            Picasso.get().load(newPost.getImageId()).into(imAds);

            tvTitle.setText(newPost.getTitle());
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
                    dbManager.updateFav(newPost,ViewHolderData.this);



                }
            });

        }


        @Override
        public void onClick(View view) {
            NewPost newPost = mainPostList.get(getAdapterPosition());

            dbManager.updateTotalViews(newPost);

            int totalviews = Integer.parseInt(newPost.getTotalViews());
            totalviews++;
            newPost.setTotalViews(String.valueOf(totalviews));
            Intent i = new Intent(context, ShowAddActivityActivity.class);
            i.putExtra(MyConstans.NEW_POST_INTENT,newPost);
            context.startActivity(i);
            onItemClickCustom.onItemSelected(getAdapterPosition());


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
