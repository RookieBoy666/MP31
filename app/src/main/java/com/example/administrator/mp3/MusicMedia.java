package com.example.administrator.mp3;

/**
 * Created by lenovo on 2018/1/22.
 */



/**
 * Created by Administrator on 2018/1/7.
 */
public class MusicMedia {
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
