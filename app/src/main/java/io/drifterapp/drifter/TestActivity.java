package io.drifterapp.drifter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.drifterapp.drifter.recyclerView.CustomTouchListener;
import io.drifterapp.drifter.recyclerView.RecyclerView_Adapter;
import io.drifterapp.drifter.recyclerView.onItemClickListener;

public class TestActivity extends AudioBaseActivity {

    ArrayList<Audio> audioList;
    RecyclerView recyclerView;
    RecyclerView_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new RecyclerView_Adapter(audioList, getApplication());
        recyclerView.setAdapter(adapter);
        setRVContent();
    }

    private void setRVContent() {
        if (audioList != null && audioList.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    playAudio(index, audioList);
                }
            }));
        } else {
            //todo - make stubview list is empty/null
        }
    }

    private void initAudio() {
        ContentResolver contentResolver = getContentResolver();
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};
        final String where = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        final Cursor cursor = contentResolver.query(uri, cursor_cols, where, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String durationinString = String.valueOf(duration);

                Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                //Logger.debug(albumArtUri.toString());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, albumArtUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    //bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.ga_july);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                audioList.add(new Audio(data, track, album, artist, albumArtUri, durationinString, bitmap));

            }
        }
        if (cursor != null)
            cursor.close();
    }

    private void initList() {
        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String durationinString = String.valueOf(duration);

                Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                //Logger.debug(albumArtUri.toString());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, albumArtUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    //bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.ga_july);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                audioList.add(new Audio(data, track, album, artist, albumArtUri, durationinString, bitmap));

            }
        }
        if (cursor != null)
            cursor.close();
    }


    @Override
    public void makeSomethingOnAccessGranted() {
        //initSearchCursor();
        initList();
    }




}
