package com.db;

import java.io.Serializable;

public class NewPost implements Serializable {
    private String imageId;
    private String selectcountry;
    private String selectcity;
    private String index;
    private String selectcat;
    private String imageId2;
    private String imageId3;
    private String title;
    private String price;
    private String tel;
    private String desc;
    private String key;
    private String uid;
    private String time;
    private String cat;
    private String email;
    private String totalViews = "0";
    private String totalCalls = "0";
    private String totalEmails = "0";

    public String getSelectcat() {
        return selectcat;
    }

    public void setSelectcat(String selectcat) {
        this.selectcat = selectcat;
    }

    public String getSelectcountry() {
        return selectcountry;
    }

    public void setSelectcountry(String selectcountry) {
        this.selectcountry = selectcountry;
    }

    public String getSelectcity() {
        return selectcity;
    }

    public void setSelectcity(String selectcity) {
        this.selectcity = selectcity;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getFavCounter() {
        return favCounter;
    }

    public void setFavCounter(long favCounter) {
        this.favCounter = favCounter;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    private long favCounter = 0;
    private boolean isFav = false;

    public String getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(String totalCalls) {
        this.totalCalls = totalCalls;
    }

    public String getTotalEmails() {
        return totalEmails;
    }

    public void setTotalEmails(String totalEmails) {
        this.totalEmails = totalEmails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageId2() {
        return imageId2;
    }

    public void setImageId2(String imageId2) {
        this.imageId2 = imageId2;
    }

    public String getImageId3() {
        return imageId3;
    }

    public void setImageId3(String imageId3) {
        this.imageId3 = imageId3;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
