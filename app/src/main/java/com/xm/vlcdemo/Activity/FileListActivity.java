package com.xm.vlcdemo.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Adapter.FilelistAdapter;
import com.xm.vlcdemo.Data.FileData;
import com.xm.vlcdemo.MainApplication;
import com.xm.vlcdemo.R;


import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rl_flist;
    MainApplication application;
    FilelistAdapter flistadapter;
    ArrayList<FileData>flist;
    Button btn_menu;
    String path="";
    TextView tv_path,tv_count;
    int type=0;
    int num=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist_activity);
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
        tv_path=findViewById(R.id.tv_path);
        tv_count=findViewById(R.id.tv_count);
        flist=new ArrayList<>();
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
                    tv_path.setText(path);
                    flist.clear();
                    flist=new ArrayList<>();
                    num=0;
                    task(path, type);
                } else {
                    Intent i = new Intent(getApplicationContext(), VideoActivity.class);
                    i.putExtra("path", fdata.getFpath());
                    i.putExtra("type", type);
                    startActivity(i);
                }
            }
        });

                task(path,type);

                rl_flist.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int nowpoint=((LinearLayoutManager)rl_flist.getLayoutManager()).
                                findLastCompletelyVisibleItemPosition();
                        int maxpoint=flistadapter.getItemCount()-1;
                        if(nowpoint==maxpoint)
                        task(path,type);

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
    public void task(String path,int type){
        Disposable backgroudtask=null;
        backgroudtask= Observable.fromCallable(()->{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            flist.addAll(application.readfilelist2(path,type,num));
                        }
                    });
                }
            }).start();

            return flist;

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result)->{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flistadapter.setFileDatalist(flist);
                                    int c=flist.size()-1;
                                    tv_count.setText(String.valueOf(c));
                                    num+=20;
                                }
                            });

                        }
                    }).start();
                });
    }

}
