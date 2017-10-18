package io.drifterapp.drifter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by gse on 18/10/2017.
 *
 * Basic Object that contains the crucial information an audio file needs,
 * but you can add more if necessary.
 */
public class Audio implements Serializable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private Bitmap bitmap;
    private String duration;
    private transient  Uri uriImage; //transient to exclude this from Gson serialization

    public Audio(String data, String title, String album, String artist,
                 Uri uriImage, String duration, @Nullable Bitmap bitmap) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.bitmap = bitmap;
        this.uriImage = uriImage;
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setUriImage(Uri uriImage) {
        this.uriImage = uriImage;
    }

    public Uri getUriImage() {
        return uriImage;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}

