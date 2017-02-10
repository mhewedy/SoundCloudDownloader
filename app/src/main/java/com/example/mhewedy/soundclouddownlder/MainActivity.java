package com.example.mhewedy.soundclouddownlder;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "HELLO", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String extras = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (extras != null) {
            String url = extras.split("\n")[1];
            Log.i(TAG, url);

            final SCService scService = new SCService();

            scService.getApi()
                    .getTrack(url)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Track, Pair<Track, Stream>>() {
                        @Override
                        public Pair<Track, Stream> call(Track track) {
                            return Pair.create(track,
                                    scService.getApi().getStream(track.id).toBlocking().first());
                        }
                    })
                    .subscribe(new Action1<Pair<Track, Stream>>() {
                        @Override
                        public void call(Pair<Track, Stream> trackStream) {
                            Log.i(TAG, trackStream.second.http_mp3_128_url);
                            downloadMp3(trackStream.first.title, trackStream.second.http_mp3_128_url);
//                            moveTaskToBack(true);
                            finish();
                        }
                    });
        }
    }

    private void downloadMp3(String title, String mp3Url) {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(mp3Url));
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, title + ".mp3");
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
