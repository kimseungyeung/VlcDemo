package com.xm.vlcdemo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.vlcdemo.Adapter.FilelistAdapter;
import com.xm.vlcdemo.Constants;
import com.xm.vlcdemo.Data.FileData;
import com.xm.vlcdemo.Dialog.ExitDialog;
import com.xm.vlcdemo.MainApplication;
import com.xm.vlcdemo.R;
import com.xm.vlcdemo.Service.MusicService;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity implements IVLCVout.Callback, View.OnClickListener {
    public static final int Request_Read_Storage=100;
    ExitDialog exitDialog=null;
    private static final String TAG = "VideoActivity";
    RelativeLayout ll_bar;
    RelativeLayout ll_view;
    ImageButton imbtn_view;
    TextView tv_maxtime,tv_nowtime;
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    String mFilePath;
    boolean isRtsp;
    boolean isview = false;
    boolean isplaying;
    LibVLC libvlc;
    MediaPlayer mMediaPlayer;

    public int mVideoWidth;
    public int mVideoHeight;
    SeekBar pr_bar;
    int type=0;
    Intent mintent;
    MainApplication application;
    RecyclerView rl_flist;
    FilelistAdapter filelistAdapter;
    ArrayList<FileData>flist;
    ArrayList<String>playlist;
    int nowcount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, Request_Read_Storage);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, Request_Read_Storage);
            }
        }else{
            init();
        }


    }

    public void init(){
        type=getIntent().getIntExtra("type",0);
        application=(MainApplication)getApplicationContext();
        rl_flist=(RecyclerView)findViewById(R.id.rl_flist);


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        ll_bar=(RelativeLayout)findViewById(R.id.ll_bar);
        ll_view=(RelativeLayout)findViewById(R.id.ll_view);
        imbtn_view=(ImageButton)findViewById(R.id.imbtn_view);
        imbtn_view.setOnClickListener(this);
        tv_nowtime=(TextView)findViewById(R.id.tv_nowtime);
        tv_maxtime=(TextView)findViewById(R.id.tv_maxtime);
        pr_bar=(SeekBar) findViewById(R.id.pr_bar);
        hideSystemUI();
        //재생할 파일 경로


        mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/House.mp4";
        mFilePath=getIntent().getStringExtra("path");
        File f=new File(mFilePath);
        flist=application.readfilelist(f.getParentFile().getAbsolutePath(),type);
        filelistAdapter=new FilelistAdapter(this,flist);
        rl_flist.setAdapter(filelistAdapter);
        rl_flist.setLayoutManager(new LinearLayoutManager(this));
        playlist=new ArrayList<>();
        application.isplaylist(playlist,mFilePath,type);
        playlist.add(mFilePath);
        filelistAdapter.setonitemclicklistener(new FilelistAdapter.OnItemClicklistener() {
            @Override
            public void onitemclick(View v, int position) {
                FileData fdata=flist.get(position);
                Toast.makeText(getApplicationContext(),flist.get(position).getFname(),Toast.LENGTH_LONG).show();

                if(fdata.isIsdirectory()){

                    flist=application.readfilelist(fdata.getFpath(),type);
                    filelistAdapter.setFileDatalist(flist);
                }else{
                    isplaying=false;
                    playlist=new ArrayList<>();
                    application.isplaylist(playlist,fdata.getFpath(),type);
                    mFilePath=fdata.getFpath();
                    playlist.add(mFilePath);
                    nowcount=0;
                    application.releasePlayer(VideoActivity.this,mSurface);
                    application.createPlayer(TAG,VideoActivity.this,mFilePath, mSurface,eventListener
                            ,VideoActivity.this,false);

                }

            }
        });


        isRtsp = true;
        //isRtsp = true;
        mSurface=(SurfaceView)findViewById(R.id.surface);
        holder=(SurfaceHolder)mSurface.getHolder();
        mSurface.setOnClickListener(this);

        if(type== Constants.TYPE_VIDEO) {
            application.createPlayer(TAG,this,mFilePath, mSurface,eventListener,this,false);
        }else if(type==Constants.TYPE_MUSIC){
           mintent =new Intent(getApplicationContext(), MusicService.class);
            startService(mintent);
        }
    }
    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("player",String.valueOf(type));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(type==Constants.TYPE_VIDEO) {
            isplaying=false;
            application.releasePlayer(this,mSurface);
        }else if(type==Constants.TYPE_MUSIC){
            stopService(mintent);
            application.releasePlayer(this,mSurface);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    //시스템 UI 표시
    private void showSystemUI() {
        ll_view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        imbtn_view.setVisibility(View.VISIBLE);
        ll_bar.setVisibility(View.VISIBLE);
        rl_flist.setVisibility(View.VISIBLE);
    }

    //전체 화면
    private void hideSystemUI() {
        ll_view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        imbtn_view.setVisibility(View.GONE);
        ll_bar.setVisibility(View.GONE);
        rl_flist.setVisibility(View.GONE);
    }
    MediaPlayer.EventListener eventListener =new MediaListner(this);

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imbtn_view:
                if(isplaying){
                if(isRtsp) {
                    application.getmMeidaplayer().pause();
                    btnset(true);
                    isRtsp=false;
                }else{
                  application.getmMeidaplayer().play();
                    btnset(false);
                  isRtsp=true;
                }
                }else{

                    application.releasePlayer(this,mSurface);
                    btnset(false);
                    isRtsp = true;
                    application.createPlayer(TAG,this,mFilePath, mSurface,eventListener,this,false);
                }
                break;
            case R.id.surface:
                if(isview){
                    hideSystemUI();
                    isview=false;


                }else{
                    showSystemUI();
                    isview=true;
                }
                break;
            case R.id.btn_ok:
                exitDialog.dismiss();
                finish();
                break;
            case R.id.btn_cancel:
                exitDialog.dismiss();
                break;

        }
    }
    SeekBar.OnSeekBarChangeListener seekBarChangeListener =new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
            mMediaPlayer.setTime(progress);


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    public void btnset(boolean isstart){
        if(isstart){
            imbtn_view.setImageDrawable(getDrawable(R.drawable.play_selected));
        }else{
            imbtn_view.setImageDrawable(getDrawable(R.drawable.stop_selected));
        }
    }
    public void timeset(String pt,String mt){
            tv_nowtime.setText(pt);
            tv_maxtime.setText(mt);

    }
    public String calctime(int ps){
        String result="";
        int sec=ps/1000;
        int min=sec/60;
        int hour=min/60;
        int rsec=sec-(min*60)-(hour*3600);
        NumberFormat numberFormat=NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        String smin=numberFormat.format(min);
        String shour=numberFormat.format(hour);
        String ssec=numberFormat.format(rsec);
        result=shour+" : "+smin+" : "+ssec;
        return result;
    }
    public int getmedialength(){
        return (int)application.getmMeidaplayer().getLength();
    }
    public int getmediatime(){
        return (int)application.getmMeidaplayer().getTime();
    }
    public IVLCVout getout(){
        return application.getmMeidaplayer().getVLCVout();
    }
    public static int getNavigationBarHeight(Context context) {
        if (context != null) {
            int statusBarHeightResourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (statusBarHeightResourceId > 0) {
                return context.getResources().getDimensionPixelSize(statusBarHeightResourceId);
            }
        }
        return 0;
    }



    private class MediaListner implements MediaPlayer.EventListener{
        private WeakReference<VideoActivity> act;

        private MediaListner(VideoActivity activity){
            act=new WeakReference<VideoActivity>(activity);

        }


        @Override
        public void onEvent(MediaPlayer.Event event) {
            final VideoActivity mm=act.get();

            int maxtime;
            int nowtime;
            switch (event.type){
                case MediaPlayer.Event.TimeChanged:
                    Log.d(TAG, "timechanged");
                    nowtime=(int)mm.getmediatime();
                    maxtime= (int)(mm.getmedialength());
                    mm.timeset(mm.calctime(nowtime),mm.calctime(maxtime));
                    mm.pr_bar.setProgress((int)mm.getmediatime());


                    break;
                case MediaPlayer.Event.Buffering:
                    Log.d(TAG, "buffering");
                    break;
                case MediaPlayer.Event.Opening:
                    Log.d(TAG, "opening");
                    break;
                case MediaPlayer.Event.Stopped:
                    Log.d(TAG, "stopped");

                    if(mm.isplaying) {
                        mm.btnset(true);
                        mm.isplaying = false;
                        maxtime= (int)(mm.getmedialength()/1000);
                        mm.timeset(mm.calctime(maxtime),mm.calctime(maxtime));
                        application.releasePlayer(VideoActivity.this,mSurface);
                        application.createPlayer(TAG,VideoActivity.this,playlist.get(nowcount)
                                ,mSurface,eventListener
                                ,VideoActivity.this,false);
                        nowcount++;
                    }
                    break;
                case MediaPlayer.Event.Playing:
                    Log.d(TAG, "playing");

                    mm.pr_bar.setMax(mm.getmedialength());
                    int w=mm.ll_view.getWidth();
                    int h=mm.ll_view.getHeight();
                    mm.getout().setWindowSize(w,h);
                    mm.pr_bar.setOnSeekBarChangeListener(mm.seekBarChangeListener);
                    mm.isplaying=true;

                    break;
                case MediaPlayer.Event.Paused:
                    Log.d(TAG, "paused");
                    break;
                case MediaPlayer.Event.PositionChanged:
//                    Log.d(TAG, "positionchanged");

                    break;


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Request_Read_Storage:
                if(grantResults.length>0
                    &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,getString(R.string.permission_ok),Toast.LENGTH_LONG).show();
                    init();
                }else{
                    Toast.makeText(this,getString(R.string.permission_no),Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        exitDialog=  new ExitDialog(this,this,getString(R.string.finish_video));
        exitDialog.show();
    }
}