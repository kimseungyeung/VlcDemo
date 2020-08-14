package com.xm.vlcdemo;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Adapter.FilelistAdapter;
import com.xm.vlcdemo.Data.FileData;
import com.xm.vlcdemo.Data.ImageData;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

import static com.xm.vlcdemo.Constants.TYPE_IMAGE;
import static com.xm.vlcdemo.Constants.TYPE_MUSIC;
import static com.xm.vlcdemo.Constants.TYPE_VIDEO;

public class MainApplication extends Application {

   private MediaPlayer mMediaPlayer;
   private LibVLC libvlc;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setmMeidaplayer(MediaPlayer mMeidaplayer) {
        this.mMediaPlayer = mMeidaplayer;
    }

    public MediaPlayer getmMeidaplayer() {
        return this.mMediaPlayer;
    }

    public void setLibvlc(LibVLC libvlc) {
        this.libvlc = libvlc;
    }

    public LibVLC getLibvlc() {
        return libvlc;
    }

    /*
     *  동영상 플레이어 시작
     *  String mediaPath : 파일 경로
     */
    public void createPlayer(String TAG, Activity act, String mediaPath, SurfaceView mSurface,
                             MediaPlayer.EventListener eventListener, IVLCVout.Callback callback, boolean isURL) {
        //플레이어가 있다면 종료(제거)
        SurfaceHolder holder=null;
        Log.d(TAG, "createPlayer");
        try {
            // 미디어 파일 경로 메시지 풍선으로 화면에 표시
//            if (mediaPath.length() > 0) {
//                Toast toast = Toast.makeText(this, mediaPath, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
//                        0);
//                toast.show();
//            }

            if(holder==null){
                holder=(SurfaceHolder)mSurface.getHolder();
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


            //옵셕 적용하여 libvlc 생성
            libvlc = new LibVLC(act, options);

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
            vout.addCallback(callback);
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
            Log.e("error",e.getMessage());
           // Toast.makeText(getApplicationContext(), "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }
    /*
     *  동영상 플레이어 종료
     */
    public void releasePlayer(IVLCVout.Callback callback,SurfaceView surfaceView) {
        //라이브러리가 없다면
        //바로 종료
        if (libvlc == null)
            return;
        if(mMediaPlayer != null) {
            //플레이 중지

            mMediaPlayer.stop();

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            //콜백함수 제거
            vout.removeCallback(callback);

            //연결된 뷰 분리
            vout.detachViews();
        }

        libvlc.release();
        libvlc = null;

//        mVideoWidth = 0;
//        mVideoHeight = 0;
    }
    /*
     *  동영상 플레이어 시작
     *  String mediaPath : 파일 경로
     */
    public void createPlayerM(String mediaPath, boolean isURL) {
        //플레이어가 있다면 종료(제거)

        Log.d("TAG", "createPlayer");
        try {



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


            //옵셕 적용하여 libvlc 생성
            libvlc = new LibVLC(getApplicationContext(), options);

            // 화면 자동을 꺼지는 것 방지


            // mediaplay 클래스 생성  (libvlc 라이브러리)
            mMediaPlayer = new MediaPlayer(libvlc);
            // 이벤트 리스너 연결


            // 영상을 surface 뷰와 연결 시킴
            final IVLCVout vout = mMediaPlayer.getVLCVout();

            //콜백 함수 등록
//            vout.addCallback(this);
            //서페이스 홀더와 연결


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
    public ArrayList<FileData> readfilelist(String fpath,int type) {
        ArrayList<FileData>  flist=new ArrayList<>();
        File forder=new File(fpath);
        File ff=forder.getParentFile();
        if(!ff.getPath().equals(Environment.getExternalStorageDirectory().getParentFile().getAbsolutePath())) {
            FileData f1 = new FileData(null, "...", ff.getAbsolutePath(), true);
            f1.setIsparent(true);
            flist.add(f1);
        }
        try {
            for (File f : forder.listFiles()) {
                if(type==TYPE_VIDEO) {
                    if (f.isDirectory()) {
                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);
                    } else if (f.getName().contains(".mp4")
                            || f.getName().contains(".wmv") ||
                            f.getName().contains(".mkv") ||
                            f.getName().contains(".divx") ||
                            f.getName().contains(".flv") ||
                            f.getName().contains(".swf") ||
                            f.getName().contains(".ts") ||
                            f.getName().contains(".trp")


                    ) {
                        Bitmap thumb = getvideothumbnail(f);
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);
                    }
                }else if(type==TYPE_MUSIC){
                    if (f.isDirectory()) {
                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);
                    } else if (f.getName().contains(".mp3")
                            || f.getName().contains(".wmv"))
                    {
                        Bitmap thumb = getvideothumbnail(f);
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);
                    }
                }
                else if(type==TYPE_IMAGE){
                    if (f.isDirectory()) {
                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);
                    } else if (f.getName().contains(".jpg")
                            || f.getName().contains(".png"))
                    {

                        Bitmap thumb = getThumbNail(getUriFromPath(f.getAbsolutePath()));
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);
                    }
                }
            }
            return flist;
        }catch (Exception e){

        }
        return flist;
    }
    public ArrayList<FileData> readfilelist2(String fpath, int type,int count) {
        ArrayList<FileData>flist=new ArrayList<>();
        File forder=new File(fpath);
        File ff=forder.getParentFile();

        if(!ff.getPath().equals(Environment.getExternalStorageDirectory().getParentFile().getAbsolutePath())
        &&count==0) {
            FileData f1 = new FileData(null, "...", ff.getAbsolutePath(), true);
            f1.setIsparent(true);
            flist.add(f1);
        }
        try {
            File[] allf=forder.listFiles();
            if(count>=allf.length){
                return new ArrayList<>();
            }
            int max=count+20;
            if(count+20>allf.length){
                max=allf.length;
            }
            for (int i=count; i<max; i++) {

                File f=allf[i];
                if(type==TYPE_VIDEO) {
                    if (f.isDirectory()) {
                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);
                    } else if (f.getName().contains(".mp4")
                            || f.getName().contains(".wmv") ||
                            f.getName().contains(".mkv") ||
                            f.getName().contains(".divx") ||
                            f.getName().contains(".flv") ||
                            f.getName().contains(".swf") ||
                            f.getName().contains(".ts") ||
                            f.getName().contains(".trp")


                    ) {
                        Bitmap thumb = getvideothumbnail(f);
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);
                    }
                }else if(type==TYPE_MUSIC){
                    if (f.isDirectory()) {
                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);
                    } else if (f.getName().contains(".mp3")
                            || f.getName().contains(".wmv"))
                    {
                        Bitmap thumb = getvideothumbnail(f);
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);
                    }
                }
                else if(type==TYPE_IMAGE){
                    if (f.isDirectory()) {

                        FileData fdata = new FileData(null, f.getName(), f.getAbsolutePath(), true);
                        flist.add(fdata);

                    } else if (f.getName().contains(".jpg")
                            || f.getName().contains(".png"))
                    {

                        Bitmap thumb = getThumbNail(getUriFromPath(f.getAbsolutePath()));
                        FileData fdata = new FileData(thumb, f.getName(), f.getAbsolutePath(), false);
                        flist.add(fdata);


                    }
                }
            }
            return flist;
        }catch (Exception e){

        }
        return flist;
    }
    public ArrayList<ImageData> getimage(String fpath){
        ArrayList<ImageData>  flist=new ArrayList<>();
        File forder=new File(fpath);
        for(File f:forder.listFiles()) {

           if (f.getName().contains(".jpg")
                    || f.getName().contains(".png")) {

                Bitmap thumb = getThumbNail(getUriFromPath(f.getAbsolutePath()));
                ImageData imageData = new ImageData(thumb, f.getAbsolutePath(), f.getName());
                flist.add(imageData);
            }
        }
        return flist;
    }
    //절대경로->Uri
    public Uri getUriFromPath(String filePath) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null);

        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;
    }
    //Uri->절대경로
    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }

    public void isplaylist(ArrayList<String>playlist,String fpath,int type){
        try {
            File nowfile=new File(fpath);
            for (File f : nowfile.getParentFile().listFiles()) {
                if(type==TYPE_VIDEO) {
                    if (f.isDirectory()) {
                      isplaylist(playlist,f.getAbsolutePath(),type);
                    } else if (f.getName().contains(".mp4")
                            || f.getName().contains(".wmv") ||
                            f.getName().contains(".mkv") ||
                            f.getName().contains(".divx") ||
                            f.getName().contains(".flv") ||
                            f.getName().contains(".swf") ||
                            f.getName().contains(".ts") ||
                            f.getName().contains(".trp")


                    ) {
                        if(!nowfile.getName().equals(f.getName())) {
                            playlist.add(f.getAbsolutePath());
                        }
                    }
                }else if(type==TYPE_MUSIC){
                    if (f.isDirectory()) {
                        isplaylist(playlist,f.getAbsolutePath(),type);
                    } else if (f.getName().contains(".mp3")
                            || f.getName().contains(".wmv"))
                    {
                        if(!nowfile.getName().equals(f.getName())) {
                            playlist.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }catch (Exception e){
         Log.e("error",e.getMessage());
        }
    }
    public Bitmap getvideothumbnail(File source) throws Exception{
        Bitmap resultbit=null;
        ContentResolver cc= getContentResolver();
        Size size=new Size(100,100);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
            resultbit= ThumbnailUtils.createVideoThumbnail(source,size,null);
        }else{

            resultbit=ThumbnailUtils.createVideoThumbnail(source.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        }
        return resultbit;
    }
    private Bitmap getThumbNail(Uri uri) {
        Log.d("test","from uri : "+uri);
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = getContentResolver();
        //content 프로토콜로 리턴되기 때문에 실제 파일의 위치로 변환한다.
        Cursor cursor = cor.query(uri, filePathColumn, null, null, null);

        Bitmap thumbnail = null;
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long ImageId = cursor.getLong(columnIndex);
            if(ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);
            } else {
                Toast.makeText(this, "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }
//    public Bitmap resizeimage(int size,String fpath){
//        try {
//
//
//            //option.inJustDecodeBounds = true;
//            File f=new File(fpath);
//            Bitmap resultbit=null;
//            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
//                resultbit=ThumbnailUtils.createImageThumbnail(f,"",);
//            }else{
//                resultbit=ThumbnailUtils(f.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//
//            }
//
//            return resultbit;
//        }catch (Exception e){
//            Log.e("error",e.getMessage());
//        }
//        return null;
//    }
}
