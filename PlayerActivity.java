package com.example.musicplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
Button btn_pause,btn_next,btn_previous;
TextView songTextLabel;
SeekBar  songSeekbar;
int Position;
static MediaPlayer mediaplayer;
Thread updateseekbar;

ArrayList<File> mySongs;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

    btn_pause=(Button)findViewById(R.id.pause);
    btn_next=(Button)findViewById(R.id.next);
    btn_previous=(Button)findViewById(R.id.previous);

    songTextLabel = (TextView) findViewById(R.id.songLabel);
    songSeekbar = (SeekBar) findViewById(R.id.seekBar);

    updateseekbar = new Thread()
    {

        @Override
        public void run() {
            int totalduration = mediaplayer.getDuration();
            int currentposition = 0;
            while(currentposition<totalduration)
            {
                try {
                    sleep(500);
                    currentposition = mediaplayer.getCurrentPosition();
                    songSeekbar.setProgress(currentposition);
                    }
                catch (InterruptedException e) {
                 e.printStackTrace();
                    }
            }
        }
    };

     if(mediaplayer!=null)
     {
         mediaplayer.stop();
         mediaplayer.release();
     } // Doubt

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("Songs");
        String songname = (String) i.getStringExtra("songname");

        songTextLabel.setText(songname);
        songTextLabel.setSelected(true);

        Position = i.getIntExtra("pos",0);

        Uri u = Uri.parse(mySongs.get(Position).toString());
        mediaplayer = MediaPlayer.create(getApplicationContext(),u);
        mediaplayer.start();
        songSeekbar.setMax(mediaplayer.getDuration());

        updateseekbar.start();
        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                  mediaplayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(mediaplayer.getDuration());

                if(mediaplayer.isPlaying())
                {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mediaplayer.pause();

                }
                else
                {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mediaplayer.start();

                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaplayer.stop();
                mediaplayer.release();
                Position=((Position+1)%mySongs.size());

                Uri u = Uri.parse(mySongs.get(Position).toString());
                mediaplayer=MediaPlayer.create(getApplicationContext(),u);

                songTextLabel.setText(mySongs.get(Position).getName().toString());
                mediaplayer.start();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaplayer.stop();
                mediaplayer.release();
                Position=((Position-1)<0)?(mySongs.size()-1):(Position-1);

                Uri u = Uri.parse(mySongs.get(Position).toString());
                mediaplayer=MediaPlayer.create(getApplicationContext(),u);

                songTextLabel.setText(mySongs.get(Position).getName().toString());
                mediaplayer.start();

            }
        });
    }
}
