package com.xm.vlcdemo.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.xm.vlcdemo.Constants;
import com.xm.vlcdemo.MainApplication;
import com.xm.vlcdemo.R;
import com.xm.vlcdemo.Activity.VideoActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;

public class MusicService extends Service {
    MediaPlayer mMediaPlayer=null;
    LibVLC libvlc;
    String mFilePath;
    MainApplication application;
    @Override
    public IBinder onBind(Intent intent) {

      // String mFilePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/House.mp4";

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFilePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Samsung/Music/Over_the_Horizon.mp3";
        File f=new File(mFilePath);
        application=(MainApplication)getApplicationContext();

        application.createPlayerM(mFilePath,false);
        Intent i=new Intent(getApplicationContext(), VideoActivity.class);
        PendingIntent p=PendingIntent.getActivity(getApplicationContext(), Constants.NOTI_CLICK,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(p)
                .setContentTitle(f.getName())
                .setContentText("재생중입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        NotificationManager notificationManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(R.drawable.ic_launcher_foreground,builder.build());
        return super.onStartCommand(intent, flags, startId);

    }


}
