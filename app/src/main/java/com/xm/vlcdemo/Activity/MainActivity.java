package com.xm.vlcdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xm.vlcdemo.Dialog.ExitDialog;
import com.xm.vlcdemo.R;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_music, btn_video, btn_image;
    boolean checkimage=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        btn_image = (Button) findViewById(R.id.btn_image);
        btn_image.setOnClickListener(this);
        btn_music = (Button) findViewById(R.id.btn_music);
        btn_music.setOnClickListener(this);
        btn_video = (Button) findViewById(R.id.btn_video);
        btn_video.setOnClickListener(this);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        checkfile(path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_music:
                Intent i2 = new Intent(getApplicationContext(), FileListActivity.class);
                i2.putExtra("type", 1);
                startActivity(i2);
                break;
            case R.id.btn_video:
                Intent i1 = new Intent(getApplicationContext(), FileListActivity.class);
                i1.putExtra("type", 2);
                startActivity(i1);
                break;
            case R.id.btn_image:
                Intent i3 = new Intent(getApplicationContext(), FileListActivity.class);
                i3.putExtra("type", 3);
//                Intent i3=new Intent(getApplicationContext(),ImageActivity.class);
                startActivity(i3);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void checkfile(String path) {

        File f = new File(path);

        File[] flist=f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(!checkimage) {
                    if (pathname.isDirectory()) {
                        checkfile(pathname.getAbsolutePath());
                    } else {
                        if (pathname.getName().contains(".jpg")) {
                            checkimage=true;
                            Log.d("chekcimage","check ok");
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            }
        });

        }

}


