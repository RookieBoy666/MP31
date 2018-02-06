package com.example.administrator.mp3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private static TextView hint,current,total;//声明提示信息的文本框
    public static SeekBar seekBar;
    private Button start,stop,pause,begin,end;

    private ListView musicListView = null;
    public static ArrayList<MusicService.MusicMedia> musicList = null; //音乐信息列表
    private ArrayList<Map<String, Object>> listems = null;//需要显示在listview里的信息




    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    seekBar.setMax(msg.arg1);

                    String str=toTime(msg.arg1);
                    total.setText(str);
                    str=toTime(msg.arg2);
                    seekBar.setProgress(msg.arg2);
                    current.setText(str);

                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //获取各功能按钮
        start=(Button)findViewById(R.id.start);//播放
        pause=(Button)findViewById(R.id.pause);//播放
        stop=(Button)findViewById(R.id.stop);//播放
        begin=(Button)findViewById(R.id.begin);//播放
        end=(Button)findViewById(R.id.end);//播放

        musicListView = (ListView)findViewById(R.id.listView);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        musicList  = scanAllAudioFiles();

        listems = new ArrayList<Map<String, Object>>();
        for (Iterator iterator = musicList.iterator(); iterator.hasNext();) {
            Map<String, Object> map = new HashMap<String, Object>();
            MusicService.MusicMedia mp3Info = (MusicService.MusicMedia) iterator.next();
//            map.put("id",mp3Info.getId());
            map.put("title", mp3Info.getTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
//            map.put("albumid", mp3Info.getAlbumId());
            map.put("duration", mp3Info.getTime());
            map.put("size", mp3Info.getSize());
            map.put("url", mp3Info.getUrl());

            map.put("bitmap", R.raw.aa);

            listems.add(map);

        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                this,
                listems,
                R.layout.perm,
                new String[] {"bitmap","title","artist", "size","duration"},
                new int[] {R.id.video_imageView,R.id.video_title,R.id.video_singer,R.id.video_size,R.id.video_duration}
        );

        //listview里加载数据
        musicListView.setAdapter(mSimpleAdapter);

        start.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);

        hint=(TextView)findViewById(R.id.textView);

        current=(TextView)findViewById(R.id.current);
        total=(TextView)findViewById(R.id.total);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int ta =seekBar.getProgress();
                current.setText(toTime(ta));

                Intent aa = new Intent(MainActivity.this, MusicService.class);
                aa.putExtra("action", "change");
                aa.putExtra("currentP", ta);
                startService(aa);
            }
        });

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(MainActivity.this, MusicService.class);
                aa.putExtra("action", "begin");
                startService(aa);

                start.setEnabled(true);
                pause.setEnabled(true);
                stop.setEnabled(true);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(MainActivity.this, MusicService.class);
                stopService(aa);

                start.setEnabled(false);
                pause.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(MainActivity.this, MusicService.class);
                aa.putExtra("action", "start");
                startService(aa);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(MainActivity.this, MusicService.class);
                aa.putExtra("action", "pause");
                startService(aa);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(MainActivity.this, MusicService.class);
                aa.putExtra("action", "stop");
                startService(aa);
            }
        });
    }

    public static String toTime(int time){
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /*加载媒体库里的音频*/
    public ArrayList<MusicService.MusicMedia> scanAllAudioFiles(){
        //生成动态数组，并且转载数据
        ArrayList<MusicService.MusicMedia> mylist = new ArrayList<MusicService.MusicMedia>();

        /*查询媒体数据库
        参数分别为（路径，要查询的列名，条件语句，条件参数，排序）
        视频：MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        图片;MediaStore.Images.Media.EXTERNAL_CONTENT_URI

         */
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));


                if (size >1024*800){//大于800K
                    MusicService.MusicMedia musicMedia = new MusicService.MusicMedia();
                    musicMedia.setId(id);
                    musicMedia.setArtist(artist);
                    musicMedia.setSize(size);
                    musicMedia.setTitle(tilte);
                    musicMedia.setTime(duration);
                    musicMedia.setUrl(url);
                    musicMedia.setAlbum(album);
                    musicMedia.setAlbumId(albumId);

                    mylist.add(musicMedia);

                }
                cursor.moveToNext();
            }
        }
        return mylist;
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "tupian红");
        map.put("info", "cdsjhbvcldsahblvdbvdvbdsvgfdkv");
        map.put("video_imageView", R.drawable.red);

        list.add(map);

       /* Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("title", "tupian绿");
        map2.put("info", "cdsjhbvcldsahblvdbvdvbdsvgfdkv");
        map2.put("picture", R.drawable.green);
        list.add(map2);
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("title", "tupian黑");
        map3.put("info", "cdsjhbvcldsahblvdbvdvbdsvgfdkv");
        map3.put("picture", R.drawable.yellow);
        list.add(map3);

*/
        return list;


    }



    }

