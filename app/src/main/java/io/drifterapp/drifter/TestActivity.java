package io.drifterapp.drifter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.drifterapp.drifter.recyclerView.CustomTouchListener;
import io.drifterapp.drifter.recyclerView.EndlessRecyclerOnScrollListener;
import io.drifterapp.drifter.recyclerView.RecyclerView_Adapter;
import io.drifterapp.drifter.recyclerView.onItemClickListener;

public class TestActivity extends AudioBaseActivity  {

    ///ArrayList<Audio> audioList;
    RecyclerView recyclerView;
    RecyclerView_Adapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;
    private int mLoadedItems;

    NestedScrollView nestedScrollView;  //for smooth scrolling of recyclerview as well as to detect the end of recyclerview
    ArrayList<Audio> songMainList = new ArrayList<>();  //partial list in which items are added


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerView_Adapter(songMainList, getApplication());
        recyclerView.setAdapter(adapter);
        setRVContent();
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedscrollview);
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));
                if (diff == 0) {
                    //NestedScrollView scrolled to bottom
                    for (int i = 0; i <= 30; i++) {
                        if (mLoadedItems<songMainList.size()) {
                            adapter.setDisplayCount(mLoadedItems++);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(TestActivity.this, ""+songMainList.size(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void setRVContent() {
        if (songMainList != null && songMainList.size() > 0) {
            mLoadedItems = 10;//by default, load only ten item
            adapter.setDisplayCount(mLoadedItems);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);


            recyclerView.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    playAudio(index, songMainList);
                }
            }));
        } else {
            //todo - make stubview list is empty/null
        }
    }

    private void initList() {
        if (cursor != null && cursor.getCount() > 0) {
            songMainList = new ArrayList<>();
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

                songMainList.add(new Audio(data, track, album, artist, albumArtUri, durationinString, bitmap));

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
