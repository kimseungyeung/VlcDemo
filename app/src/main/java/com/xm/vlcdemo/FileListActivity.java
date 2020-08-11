package com.xm.vlcdemo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Adapter.FilelistAdapter;
import com.xm.vlcdemo.Data.FileData;



import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rl_flist;
    FilelistAdapter flistadapter;
    ArrayList<FileData>flist;
    Button btn_prv;
    String path="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        init();
    }
    public void init(){
        btn_prv=(Button)findViewById(R.id.btn_prv);
        btn_prv.setOnClickListener(this);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.Q) {
          path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        }else{
          path=Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        flist=readfilelist(path);
        rl_flist=(RecyclerView)findViewById(R.id.rl_flist);
        flistadapter=new FilelistAdapter(this,flist);
        rl_flist.setAdapter(flistadapter);
        rl_flist.setLayoutManager(new LinearLayoutManager(this));
        flistadapter.setonitemclicklistener(new FilelistAdapter.OnItemClicklistener() {
            @Override
            public void onitemclick(View v, int position) {
                FileData fdata=flist.get(position);
                Toast.makeText(getApplicationContext(),flist.get(position).getFname(),Toast.LENGTH_LONG).show();
                if(fdata.isIsdirectory()){
                    path=fdata.getFpath();
                    flist=readfilelist(fdata.getFpath());
                    flistadapter.setFileDatalist(flist);
                }else{
                    Intent i=new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("path",fdata.getFpath());
                    startActivity(i);
                }

            }
        });

    }
    public ArrayList<FileData> readfilelist(String fpath) {
      ArrayList<FileData>  flist=new ArrayList<>();
      try {
          for (File f : new File(fpath).listFiles()) {
              if (f.isDirectory()) {
                  FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(),true);
                  flist.add(fdata);
              } else if (f.getName().contains(".mp4")
                      || f.getName().contains(".wmv") ||
                      f.getName().contains(".avi")
                             ) {
                  Bitmap thumb = getthumbnail(f);
                  FileData fdata = new FileData(thumb,f.getName(), f.getAbsolutePath(), false);
                  flist.add(fdata);
              }
          }
          return flist;
      }catch (Exception e){

      }
      return flist;
    }

    public Bitmap getthumbnail(File source) throws Exception{
        Bitmap resultbit=null;
        ContentResolver cc= getContentResolver();
        Size size=new Size(100,100);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
            resultbit=ThumbnailUtils.createVideoThumbnail(source,size,null);
        }else{
            resultbit=ThumbnailUtils.createVideoThumbnail(source.getAbsolutePath(),MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        }
        return resultbit;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_prv:
                File f=new File(path);
                flist =readfilelist(f.getParentFile().getAbsolutePath());
                flistadapter.setFileDatalist(flist);
                path=f.getParentFile().getAbsolutePath();
                break;
        }
    }
}
