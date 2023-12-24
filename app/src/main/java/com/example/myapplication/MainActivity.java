package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements  SharedPreferences.OnSharedPreferenceChangeListener{

    private SeekBar seekBar;
    private TextView textView;
    private boolean isTimerOn;
    private Button button;
    private CountDownTimer countDownTimer;
    private int defaultInterval;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView2);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;
        setIntervalSharedPreferences(sharedPreferences);

        button = findViewById(R.id.button);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progressInMillis = progress * 1000;
                updateTimer(progressInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void start(View view) {
        if(!isTimerOn){
            button.setText("Стоп");
            seekBar.setEnabled(false);
            isTimerOn = true;

            countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound", true)){
                        String melodyName = sharedPreferences.getString("timer_melody", "bell");
                        if(melodyName.equals("bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
                            mediaPlayer.start();
                        }
                        else  if(melodyName.equals("alarm_siren")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
                            mediaPlayer.start();
                        }
                        else  if(melodyName.equals("bip")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound3);
                            mediaPlayer.start();
                        }
                    }
                    resetTimer();
                }
            };
            countDownTimer.start();
        }
        else{
           resetTimer();
        }
    }
    private void updateTimer(long millisUntilFinished){
        int minutes = (int) millisUntilFinished/1000/60;
        int seconds = (int) millisUntilFinished/1000 -(minutes*60);

        String minutesString = "";
        String secondsString = "";

        if(minutes < 10){
            minutesString = "0" + minutes;
        }
        else {
            minutesString = String.valueOf(minutes);
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }
        else {
            secondsString = String.valueOf(seconds);
        }
        textView.setText(minutesString + ":" + secondsString);
    }
    private void resetTimer(){
        countDownTimer.cancel();
        button.setText("Старт");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalSharedPreferences(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
        }
        else if(id == R.id.action_about){
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
        }
        return super.onOptionsItemSelected(item);
    }
    private void setIntervalSharedPreferences(SharedPreferences sharedPreferences){

        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));

        long defaultIntervalInMillis = defaultInterval * 1000;
        updateTimer(defaultIntervalInMillis);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if(key.equals("default_interval")){
            setIntervalSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}