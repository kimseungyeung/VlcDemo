package com.xm.vlcdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Adapter.FilelistAdapter;

public class FileListActivity extends AppCompatActivity {
    RecyclerView rl_flist;
    FilelistAdapter flistadapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void init(){
        rl_flist=(RecyclerView)findViewById(R.id.rl_flist);
        flistadapter=new FilelistAdapter(this,null);
        rl_flist.setAdapter(flistadapter);
        rl_flist.setLayoutManager(new LinearLayoutManager(this));
    }
}
