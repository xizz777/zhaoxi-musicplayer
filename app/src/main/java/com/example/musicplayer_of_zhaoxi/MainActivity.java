package com.example.musicplayer_of_zhaoxi;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private DownloadService.DownloadBinder downloadBinder;
    private String[] data = {"阿刁","离人","something just like this","致爱丽丝","遥远的她(live)"};
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startDownload = (Button) findViewById(R.id.start_download);
        Button pauseDownload = (Button) findViewById(R.id.pause_download);
        Button cancelDownload = (Button) findViewById(R.id.cancel_download);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> muscilist = Arrays.asList(data);
                String musicname = muscilist.get(position);
                Toast.makeText(MainActivity.this, musicname, Toast.LENGTH_SHORT).show();
                        //String url = "http://sr.sycdn.kuwo.cn/5ceebccdf0154913d76348af9fcc1f89/5fd86011/resource/n2/3/75/487080337.mp3";
                        SharedPreferences preferences = getSharedPreferences("musicurl1", MODE_PRIVATE);
                        String url = preferences.getString(musicname, "");
                        String fileName = url.substring(url.lastIndexOf("/"));
                        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        File file = new File(directory + fileName);
                        if (file.exists()) {
                            Intent intent = new Intent(MainActivity.this,MusicActivity.class);
                            intent.putExtra("directory", directory);
                            intent.putExtra("fileName",fileName);
                            intent.putExtra("musicname", musicname);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                        if(!file.exists()){
                            downloadBinder.startDownload(url);
                            String downloadurl = downloadBinder.geturl();
                            SharedPreferences preferences1 = getSharedPreferences("musicurl1", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences1.edit();
                            editor.putString(musicname, downloadurl);
                            editor.apply();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent1 = new Intent(MainActivity.this, MusicActivity.class);
                                    intent1.putExtra("directory", directory);
                                    intent1.putExtra("fileName", fileName);
                                    intent1.putExtra("musicname", musicname);
                                    intent1.putExtra("position", position);
                                    startActivity(intent1);
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(task, 10000);
                        }


            }
        });
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

}
