package com.adapter;

import com.db.NewPost;

import java.util.List;

public interface DataSender {
    public void onDataReceived(List<NewPost> listdata );
}
