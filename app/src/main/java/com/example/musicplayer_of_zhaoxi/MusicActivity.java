package com.example.musicplayer_of_zhaoxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity{
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekbar;
    private TextView textView;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean iffirst = false;
    private boolean isChanging=false;
    private int position;
    private File thisfile;
    private boolean ifchange = false;
    private String[] data = {"阿刁","离人","something just like this","致爱丽丝","遥远的她(live)"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Intent intent = getIntent();
        Button play = findViewById(R.id.play);
        Button pause = findViewById(R.id.pause);
        Button lastmusic = findViewById(R.id.lastmusic);
        Button nextmusic = findViewById(R.id.nextmusic);
        Intent intent1 = getIntent();
        String musicname = intent1.getStringExtra("musicname");
        position = intent1.getIntExtra("position",0);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new MySeekbar());
        FloatingActionButton fanhui = findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                Intent intent = new Intent(MusicActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
            }
        });
        nextmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> muscilist = Arrays.asList(data);
                String musicname = muscilist.get(position);
                SharedPreferences preferences = getSharedPreferences("musicurl", MODE_PRIVATE);
                String url = preferences.getString(musicname, "");
                String fileName = url.substring(url.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + fileName);
                do{
                    if(position==4){
                        position = 0;
                    }
                    else{
                        position=position + 1;
                    }
                    String musicname1 = muscilist.get(position);
                    SharedPreferences preferences1 = getSharedPreferences("musicurl", MODE_PRIVATE);
                    String url1 = preferences1.getString(musicname1, "");
                    String fileName1 = url1.substring(url1.lastIndexOf("/"));
                    String directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    file = new File(directory1 + fileName1);
                }while(!file.exists());
                String musicname2 = muscilist.get(position);
                SharedPreferences preferences2 = getSharedPreferences("musicurl", MODE_PRIVATE);
                String url2 = preferences2.getString(musicname2, "");
                String fileName2 = url2.substring(url2.lastIndexOf("/"));
                String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                thisfile = new File(directory2 + fileName2);
                ifchange = true;
                mediaPlayer.reset();
                initMediaPlayer();
                mediaPlayer.start();
            }
        });
        lastmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> muscilist = Arrays.asList(data);
                String musicname = muscilist.get(position);
                SharedPreferences preferences = getSharedPreferences("musicurl", MODE_PRIVATE);
                String url = preferences.getString(musicname, "");
                String fileName = url.substring(url.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + fileName);
                do{
                    if(position==0){
                        position = data.length-1;
                    }
                    else{
                        position=position-1;
                    }
                    String musicname1 = muscilist.get(position);
                    SharedPreferences preferences1 = getSharedPreferences("musicurl", MODE_PRIVATE);
                    String url1 = preferences1.getString(musicname1, "");
                    String fileName1 = url1.substring(url1.lastIndexOf("/"));
                    String directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    file = new File(directory1 + fileName1);
                }while(!file.exists());
                String musicname2 = muscilist.get(position);
                SharedPreferences preferences2 = getSharedPreferences("musicurl", MODE_PRIVATE);
                String url2 = preferences2.getString(musicname2, "");
                String fileName2 = url2.substring(url2.lastIndexOf("/"));
                String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                thisfile = new File(directory2 + fileName2);
                ifchange = true;
                mediaPlayer.reset();
                initMediaPlayer();
                mediaPlayer.start();
                }
        });

        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            initMediaPlayer();
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }
    private void initMediaPlayer(){
        try {
            if(!ifchange) {
                Intent intent = getIntent();
                String directory = intent.getStringExtra("directory");
                String fileName = intent.getStringExtra("fileName");
                thisfile = new File(directory + fileName);
            }
            mediaPlayer.setDataSource(thisfile.getPath());
            mediaPlayer.prepare();
            TextView musictext = findViewById(R.id.musicname);
            List<String> muscilist = Arrays.asList(data);
            String musicname = muscilist.get(position);
            musictext.setText(musicname);
            seekbar.setMax(mediaPlayer.getDuration());//设置进度条
            //----------定时器记录播放进度---------//
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(isChanging) {
                        return;
                    }
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                }
            };
            mTimer.schedule(mTimerTask, 0, 10);
            iffirst=true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                }
                else {
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();;
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging=true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekbar.getProgress());
            isChanging=false;
        }

    }
}