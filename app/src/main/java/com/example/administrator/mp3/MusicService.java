package com.example.administrator.mp3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Created by Administrator on 2017/12/26.
 */
public class MusicService  extends Service {
    MediaPlayer mediaPlayer=null;
    int  currentPosition=0;
    int  position=0;

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onCreate() {
        currentPosition=0;
        position=0;
        mediaPlayer=new MediaPlayer();

        Log.v("hjz","onCreate");

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next();
            }
        });

        new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        int ta = mediaPlayer.getDuration();// 总时长
                        Thread.sleep(200);
                        if (mediaPlayer != null) {
                            currentPosition = mediaPlayer.getCurrentPosition();

                            Message msg = new Message();
                            msg.what = 1;
                            msg.arg1 = ta;
                            msg.arg2 = currentPosition;
                            MainActivity.handler.sendMessage(msg);      //把Message发送给Handle
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        position = intent.getIntExtra("position",0);
        int  cc=intent.getIntExtra("currentP",0);

        switch (action)
        {
            case "start":
                try {
                    currentPosition=0;
                    mediaPlayer.reset();//把各项参数恢复到初始状态
                    mediaPlayer.setDataSource(MainActivity.musicList.get(position).getUrl());
                    mediaPlayer.prepare();  //进行缓冲
                    mediaPlayer.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Log.v("hjz","-------------start,startId="+startId);
                break;
            case "next":
                next();
                break;
            case "prev":
                prev();
                break;
            case "pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                Log.v("hjz","-------------pause,startId="+startId);
                break;
            case "change":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.seekTo(cc);
                }
                Log.v("hjz","-------------change,cc="+cc/1000);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer !=null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Log.v("hjz","onDestroy");
    }

    public void  next()
    {
        position++;
        if (position>MainActivity.musicList.size())
        {
            position=0;
            currentPosition=0;
        }

        try {
            currentPosition=0;
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(MainActivity.musicList.get(position).getUrl());
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  prev()
    {
        position--;
        if (position<0)
        {
            position=MainActivity.musicList.size()-1;
            currentPosition=0;
        }

        try {
            currentPosition=0;
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(MainActivity.musicList.get(position).getUrl());
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Created by Administrator on 2018/1/12.
     */
    public static class MusicMedia {
        private int id;          //歌曲编号
        private String tilte;    //歌曲标题
        private String album;    //歌曲的专辑名
        private int albumId;
        private String artist;   //歌曲的歌手名
        private String url ;      //歌曲文件的路径
        private int duration ;   //歌曲的总播放时长
        private Long size ;       //歌曲文件的大小

        public void setId(int dd)
        {
            id=dd;
        }

        public void setSize(Long dd)
        {
            size=dd;
        }

        public void setTime(int dd)
        {
            duration=dd;
        }

        public void setTitle(String str)
        {
            tilte=str;
        }

        public void setAlbum(String str)
        {
            album=str;
        }

        public void setAlbumId(int dd)
        {
            albumId=dd;
        }

        public void setArtist(String str)
        {
            artist=str;
        }

        public void setUrl(String str)
        {
            url=str;
        }


        public int getId()
        {
            return  id;
        }

        public Long getSize()    {
            return  size;
        }

        public int getTime()
        {
            return  duration;
        }

        public String getTitle()
        {
            return  tilte;
        }

        public String getAlbum()
        {
            return  album;
        }

        public String getArtist()
        {
            return  artist;
        }

        public String getUrl()
        {
            return  url;
        }
    }
}