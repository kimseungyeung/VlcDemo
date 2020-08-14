package com.xm.vlcdemo.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xm.vlcdemo.Adapter.GridAdapter;
import com.xm.vlcdemo.Data.ImageData;
import com.xm.vlcdemo.MainApplication;
import com.xm.vlcdemo.R;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    GridView gv_thumbnail;
    GridAdapter gridAdapter;
    ArrayList<ImageData>imageDatalist;
    MainApplication application;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        init();
    }
    public void init(){
        application=(MainApplication)getApplicationContext();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera";
       // path=Environment.getExternalStorageDirectory().getAbsolutePath();
        imageDatalist=application.getimage(path);

    gv_thumbnail=(GridView)findViewById(R.id.gv_thumnail);
    gridAdapter=new GridAdapter(this,imageDatalist);
    gv_thumbnail.setAdapter(gridAdapter);


    }
}
