package com.xm.vlcdemo.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Adapter.FilelistAdapter;
import com.xm.vlcdemo.Data.FileData;
import com.xm.vlcdemo.MainApplication;
import com.xm.vlcdemo.R;


import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rl_flist;
    MainApplication application;
    FilelistAdapter flistadapter;
    ArrayList<FileData>flist;
    Button btn_menu;
    String path="";
    int type=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        init();
    }
    public void init(){
        application=(MainApplication)getApplicationContext();
        type=getIntent().getIntExtra("type",0);
        btn_menu=(Button)findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(this);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.Q) {
          path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        }else{
          path=Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        flist=application.readfilelist(path,type);
        rl_flist=(RecyclerView)findViewById(R.id.rl_flist);
        flistadapter=new FilelistAdapter(this,flist);
        rl_flist.setAdapter(flistadapter);
        rl_flist.setLayoutManager(new LinearLayoutManager(this));
        flistadapter.setonitemclicklistener(new FilelistAdapter.OnItemClicklistener() {
            @Override
            public void onitemclick(View v, int position) {
                    FileData fdata = flist.get(position);
                    Toast.makeText(getApplicationContext(), flist.get(position).getFname(), Toast.LENGTH_LONG).show();
                    if (fdata.isIsdirectory()) {
                        path = fdata.getFpath();
                        flist = application.readfilelist(fdata.getFpath(), type);
                        flistadapter.setFileDatalist(flist);
                    } else {
                        Intent i = new Intent(getApplicationContext(), VideoActivity.class);
                        i.putExtra("path", fdata.getFpath());
                        i.putExtra("type", type);
                        startActivity(i);
                    }

            }
        });

    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_menu:

                break;
        }
    }
}
