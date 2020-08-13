package com.xm.vlcdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xm.vlcdemo.Dialog.ExitDialog;
import com.xm.vlcdemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_music,btn_video;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        btn_music=(Button)findViewById(R.id.btn_music);
        btn_music.setOnClickListener(this);
        btn_video=(Button)findViewById(R.id.btn_video);
        btn_video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_music:
                Intent i=new Intent(getApplicationContext(),FileListActivity.class);
                i.putExtra("type",1);
                startActivity(i);
                break;
            case R.id.btn_video:
                Intent i2=new Intent(getApplicationContext(),FileListActivity.class);
                i2.putExtra("type",2);
                startActivity(i2);
                break;
            case R.id.btn_ok:
                exitDialog.dismiss();
                break;
            case R.id.btn_cancel:
                exitDialog.dismiss();
                break;
        }
    }
    ExitDialog exitDialog=null;
    @Override
    public void onBackPressed() {

       exitDialog=  new ExitDialog(this,this,getString(R.string.finish_video));
       exitDialog.show();
    }
}
