package com.xm.vlcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCUtil;

import java.lang.ref.WeakReference;
import java.security.Permission;
import java.security.cert.CertPathValidatorException;
import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IVLCVout.Callback, View.OnClickListener {
    public static final int Request_Read_Storage=100;
    private static final String TAG = "event";
    LinearLayout ll_bar;
    RelativeLayout ll_view;
    ImageButton imbtn_view;
    TextView tv_time;
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    String mFilePath;
    boolean isRtsp;
    boolean isview = false;
    boolean isplaying;
    LibVLC libvlc;
    MediaPlayer mMediaPlayer;

    int mVideoWidth;
    int mVideoHeight;
    SeekBar pr_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ll_bar=(LinearLayout)findViewById(R.id.ll_bar);
        ll_view=(RelativeLayout)findViewById(R.id.ll_view);
        imbtn_view=(ImageButton)findViewById(R.id.imbtn_view);
        imbtn_view.setOnClickListener(this);
        tv_time=(TextView)findViewById(R.id.tv_time);
        pr_bar=(SeekBar) findViewById(R.id.pr_bar);
        hideSystemUI();
        //재생할 파일 경로

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if(Environment.isExternalStorageLegacy()){
//
//            }
//        }else{
            mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/YAHA.mp4";
//        mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/H.wmv";
        mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/House.mp4";
        mFilePath=getIntent().getStringExtra("path");
//        }
        //mFilePath에 url 경로 지정
        //mFilePath= "rtsp://192.168.25.41:8554/live.ts";
        //rtsp의 경우 true
        isRtsp = true;
        //isRtsp = true;
        mSurface=(SurfaceView)findViewById(R.id.surface);
        holder=(SurfaceHolder)mSurface.getHolder();
        mSurface.setOnClickListener(this);
        createPlayer(mFilePath,false);
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
//        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    /*
     *  동영상 플레이어 시작
     *  String mediaPath : 파일 경로
     */
    private void createPlayer(String mediaPath, boolean isURL) {
        //플레이어가 있다면 종료(제거)
        releasePlayer();
        Log.d(TAG, "createPlayer");
        try {
            // 미디어 파일 경로 메시지 풍선으로 화면에 표시
            if (mediaPath.length() > 0) {
                Toast toast = Toast.makeText(this, mediaPath, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }


            //libvlc 생성
            // 옵션 추가 하기
            // 다른 옵션 추가시 여기에 add로 추가해주면 됨.
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--file-caching=0");
            options.add("--audio-time-stretch"); // time stretching
            options.add(":network-caching=100");
            options.add(":clock-jitter=0");
            options.add(":clock-synchro=0");
            options.add(":fullscreen");

            options.add(":sout = #transcode{vcodec=x264,vb=800,scale=0.25,acodec=none,fps=23}");
            options.add("file-caching=0");
            options.add(":display");
            options.add(":no-sout-rtp-sap");

//            String[] options = {
//                    ":file-caching=0",
//                    ":network-caching=300",
//                    ":sout = #transcode{vcodec=x264,vb=800,scale=0.25,acodec=none,fps=23}" +
//                            ":display :no-sout-rtp-sap :no-sout-standard-sap :ttl=1 :sout-keep"};

            //옵셕 적용하여 libvlc 생성
            libvlc = new LibVLC(this, options);

            // 화면 자동을 꺼지는 것 방지
            holder.setKeepScreenOn(true);

            // mediaplay 클래스 생성  (libvlc 라이브러리)
            mMediaPlayer = new MediaPlayer(libvlc);
            // 이벤트 리스너 연결
            mMediaPlayer.setEventListener(eventListener);

            // 영상을 surface 뷰와 연결 시킴
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            //콜백 함수 등록
            vout.addCallback(this);
            //서페이스 홀더와 연결
            vout.attachViews();

            //동영상 파일 로딩
            Media m;
            if(isURL) {
                m = new Media(libvlc, Uri.parse(mediaPath));
            }
            else {
                m = new Media(libvlc, mediaPath);
            }
            mMediaPlayer.setMedia(m);

            // 재생 시작
            mMediaPlayer.play();

        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }
    /*
     *  동영상 플레이어 종료
     */
    private void releasePlayer() {
        //라이브러리가 없다면
        //바로 종료
        if (libvlc == null)
            return;
        if(mMediaPlayer != null) {
            //플레이 중지

            mMediaPlayer.stop();

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            //콜백함수 제거
            vout.removeCallback(this);

            //연결된 뷰 분리
            vout.detachViews();
        }

        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    //시스템 UI 표시
    private void showSystemUI() {
        ll_view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        imbtn_view.setVisibility(View.VISIBLE);
        ll_bar.setVisibility(View.VISIBLE);
    }

    //전체 화면
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        ll_view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        imbtn_view.setVisibility(View.GONE);
        ll_bar.setVisibility(View.GONE);
    }
    MediaPlayer.EventListener eventListener =new MediaListner(this);

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imbtn_view:
                if(isplaying){
                if(isRtsp) {
                    mMediaPlayer.pause();
                    btnset(true);
                    isRtsp=false;
                }else{
                  mMediaPlayer.play();
                    btnset(false);
                  isRtsp=true;
                }}else{

                   createPlayer(mFilePath,false);
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

        }
    }
    SeekBar.OnSeekBarChangeListener seekBarChangeListener =new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
            imbtn_view.setImageDrawable(getDrawable(R.drawable.start));
        }else{
            imbtn_view.setImageDrawable(getDrawable(R.drawable.stop));
        }
    }
    public void timeset(int pt,int mt){
        tv_time.setText(String.valueOf(pt)+" : "+String.valueOf(mt));
    }

    private static class MediaListner implements MediaPlayer.EventListener{
        private WeakReference<MainActivity> act;
        private MediaListner(MainActivity activity){
            act=new WeakReference<MainActivity>(activity);
        }


        @Override
        public void onEvent(MediaPlayer.Event event) {
            final MainActivity mm=act.get();
            int maxtime;
            int learningtime;
            switch (event.type){
                case MediaPlayer.Event.TimeChanged:
                    Log.d(TAG, "timechanged");
                    learningtime=(int)mm.mMediaPlayer.getTime()/1000;
                    maxtime= (int)(mm.mMediaPlayer.getLength()/1000);
                    mm.timeset(learningtime,maxtime);
                   
                    //mm.pr_bar.setProgress((int)mm.mMediaPlayer.getTime());
//                    if(mm.isview){
//                        mm.hideSystemUI();
//                    }
                    break;
                case MediaPlayer.Event.Buffering:
                    Log.d(TAG, "buffering");
                    break;
                case MediaPlayer.Event.Opening:
                    Log.d(TAG, "opening");
                    break;
                case MediaPlayer.Event.Stopped:
                    Log.d(TAG, "stopped");
                    maxtime= (int)(mm.mMediaPlayer.getLength()/1000);
                    mm.timeset(maxtime,maxtime);
                    mm.isplaying=false;
                    mm.btnset(true);
                    break;
                case MediaPlayer.Event.Playing:
                    Log.d(TAG, "playing");
                    mm.pr_bar.setMax((int)mm.mMediaPlayer.getLength());
                    int w=mm.ll_view.getWidth();
                    int h=mm.ll_view.getHeight();
                    mm.mMediaPlayer.getVLCVout().setWindowSize(w,h);
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
    //영상 사이즈 변경
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

}