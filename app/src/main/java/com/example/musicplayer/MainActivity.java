package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;


public class MainActivity extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    
    private FloatingActionButton btn_play;


    private TextView tv_song_current_duration, tv_song_total_duration;
    private CircularImageView image;
    
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    
    private MusicUtils utils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setMusicPlayerComponet();
    }



    private void setMusicPlayerComponet() {

        parent_view = findViewById(R.id.parent_view);
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar);
        btn_play = findViewById(R.id.btn_play);
        tv_song_current_duration = findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = findViewById(R.id.total_duration);
        image = findViewById(R.id.image);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                btn_play.setImageResource(R.drawable.ic_play_arrow);
            }
        });

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("bensound-clearday.mp3");
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            mediaPlayer.prepare();
        }

        catch (Exception e){

            Snackbar.make(parent_view, "Could not load audio file", Snackbar.LENGTH_LONG).show();
        }

        utils = new MusicUtils();

        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });

        buttonPlayerAction();
        updateTimerAndSeekbar();
    }


    private void buttonPlayerAction() {

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View agr0) {

                if (mediaPlayer.isPlaying()){

                    mediaPlayer.pause();
                    btn_play.setImageResource(R.drawable.ic_play_arrow);
                }
                else {
                    mediaPlayer.start();
                    btn_play.setImageResource(R.drawable.ic_pause);
                    mHandler.post(mUpdateTimeTask);
                }

                rotateTheDisk();
            }
        });
    }


    public void controlClick(View view){

        int id = view.getId();
        switch (id){
            case R.id.btn_next:{
                toggleButtonColor((ImageButton) view);
                Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_prev:{
                toggleButtonColor((ImageButton) view);
                Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_repeat:{
                toggleButtonColor((ImageButton) view);
                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_shuffle:{
                toggleButtonColor((ImageButton) view);
                Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }


    private boolean toggleButtonColor(ImageButton bt){

        String selected = (String) bt.getTag(bt.getId());
        if (selected != null){

            bt.setColorFilter(getResources().getColor(R.color.colorDarkOrange), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        }
        else {

            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }


    private void rotateTheDisk() {

        if (!mediaPlayer.isPlaying()) return;
        image.animate().setDuration(100).rotation(image.getRotation()+2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateTheDisk();
                super.onAnimationEnd(animation);
            }
        });
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {

            updateTimerAndSeekbar();

            if (mediaPlayer.isPlaying()){
                mHandler.postDelayed(this, 100);
            }
        }
    };



    private void updateTimerAndSeekbar() {

        long totalDuration = mediaPlayer.getDuration();
        long currentDuration = mediaPlayer.getCurrentPosition();

        tv_song_total_duration.setText(utils.milliSecondsToTime(totalDuration));
        tv_song_current_duration.setText(utils.milliSecondsToTime(currentDuration));

        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mediaPlayer.release();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        else {
            Snackbar.make(parent_view, item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
