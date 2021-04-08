package com.example.beeponcetimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {
    private final String TAG = "Main";
    Button spbtn;
    Button erbtn;
    TextView hnLabel;
    TextView mnLabel;
    TextView snLabel;
    ProgressBar progressBar;
    CountDownTimer cdt;
    long timerDuration;
    Chronometer chronometer;
    long timerStartedTime;
    long timeLeft;
    // long millisUntilFinished;
    long timerDuration_final;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_timer);
        SharedPreferences sharedPref = TimerActivity.this.getPreferences(Context.MODE_PRIVATE);
        String savedTime = sharedPref.getString("BeepOnceTimer_time", "000030");

        hnLabel = findViewById(R.id.hnLabel);
        mnLabel = findViewById(R.id.mnLabel);
        snLabel = findViewById(R.id.snLabel);
        spbtn = findViewById(R.id.spbtn);
        erbtn = findViewById(R.id.erbtn);
        progressBar = findViewById(R.id.progress);
        chronometer = findViewById(R.id.chronometer);
        hnLabel.setText(savedTime.substring(0,2));
        mnLabel.setText(savedTime.substring(2,4));
        snLabel.setText(savedTime.substring(4,6));

        spbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (spbtn.getText().equals("Start")) {
                    startTimer();
                } else if (spbtn.getText().equals("Pause")) {
                    pauseTimer();
                } else if (spbtn.getText().equals("Resume")) {
                    resumeTimer();
                } else if (spbtn.getText().equals("Edit")) {
                    editTimer();
                } else if (spbtn.getText().equals("Reset")) {
                    resetTimer();
                }
            }
        });
        erbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (erbtn.getText().equals("Reset")) {

                    resetTimer();
                } else if (erbtn.getText().equals("Edit")) {
                    editTimer();
                }
            }
        });
    }


    private void editTimer() {
        progressBar.setProgress(0);
        spbtn.setText(R.string.start);
        erbtn.setText(R.string.edit);
        Intent intent = new Intent(TimerActivity.this, EditActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {

                    String returnValue = data.getStringExtra("changedTime");
                    if (!(returnValue.equals("nochange"))) {
                        hnLabel.setText(String.format("%s", returnValue.substring(0, 2)));
                        mnLabel.setText(String.format("%s", returnValue.substring(2, 4)));
                        snLabel.setText(String.format("%s", returnValue.substring(4, 6)));

                    }
                }
                break;
            }
        }
    }

    private void pauseTimer() {
        spbtn.setText(R.string.resume);
        erbtn.setText(R.string.reset);
        timeLeft = timerDuration - (System.currentTimeMillis() - timerStartedTime);
        timerDuration = timeLeft;
        cdt.cancel();
        cdt = new CountDownTimer(timerDuration, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millisUntilFinished_temp = millisUntilFinished + 1000;
                long hours = millisUntilFinished_temp / (1000 * 60 * 60);
                long minutes = (millisUntilFinished_temp % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished_temp % (1000 * 60)) / (1000);
                String hourstring = String.valueOf(hours);
                String minutestring = String.valueOf(minutes);
                String secondstring = String.valueOf(seconds);
                if (hours < 10) {
                    hourstring = "0" + hourstring;
                }
                if (minutes < 10) {
                    minutestring = "0" + minutestring;
                }
                if (seconds < 10) {
                    secondstring = "0" + secondstring;
                }
                hnLabel.setText(String.format("%s", hourstring));
                mnLabel.setText(String.format("%s", minutestring));
                snLabel.setText(String.format("%s", secondstring));
                progressBar.setProgress((int)(timerDuration_final / 1000 - millisUntilFinished / 1000)-1);

            }

            @Override
            public void onFinish() {
                if(Integer.parseInt(hnLabel.getText().toString())==0&&Integer.parseInt(mnLabel.getText().toString())==0&&Integer.parseInt(snLabel.getText().toString())==1) {
                    progressBar.setProgress(progressBar.getProgress() + 1);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                    mp.start();
                    stopTimer();
                    Toast.makeText(TimerActivity.this, "Time's Up!", Toast.LENGTH_LONG).show();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerActivity.this, "Beep Once Timer")
                            .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
                            .setContentTitle("Time's Up!")
                            .setContentText("The timer has run out")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, builder.build());

                }
            }
        };
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Beep Once Timer", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void resumeTimer() {
        spbtn.setText(R.string.pause);
        erbtn.setText(R.string.reset);
        timerStartedTime = System.currentTimeMillis();
        cdt.start();
    }

    private void stopTimer() {
        spbtn.setText(R.string.reset);
        erbtn.setText(R.string.edit);
        hnLabel.setText("00");
        mnLabel.setText("00");
        snLabel.setText("00");
        cdt.cancel();
    }

    private void resetTimer() {

        cdt.cancel();
        long millisUntilFinished_temp = timerDuration_final;
        long hours = millisUntilFinished_temp / (1000 * 60 * 60);
        long minutes = (millisUntilFinished_temp % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millisUntilFinished_temp % (1000 * 60)) / (1000);
        String hourstring = String.valueOf(hours);
        String minutestring = String.valueOf(minutes);
        String secondstring = String.valueOf(seconds);
        if (hours < 10) {
            hourstring = "0" + hourstring;
        }
        if (minutes < 10) {
            minutestring = "0" + minutestring;
        }
        if (seconds < 10) {
            secondstring = "0" + secondstring;
        }
        hnLabel.setText(String.format("%s", hourstring));
        mnLabel.setText(String.format("%s", minutestring));
        snLabel.setText(String.format("%s", secondstring));
        progressBar.setProgress(0);
        spbtn.setText(R.string.start);
        erbtn.setText(R.string.edit);
    }

    private void startTimer() {
        try {
            timerStartedTime = System.currentTimeMillis();
            timerDuration = (Long.parseLong(hnLabel.getText().toString()) * 60 * 60 * 1000) + (Long.parseLong(mnLabel.getText().toString()) * 60 * 1000) + (Long.parseLong(snLabel.getText().toString()) * 1000);
            timerDuration_final = timerDuration;
            SharedPreferences sharedPref = TimerActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("BeepOnceTimer_time", String.format("%s%s%s", hnLabel.getText().toString(), mnLabel.getText().toString(), snLabel.getText().toString()));
            editor.apply();
            progressBar.setMax((int) timerDuration / 1000);
            spbtn.setText(R.string.pause);
            erbtn.setText(R.string.reset);
            cdt = new CountDownTimer(timerDuration, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long millisUntilFinished_temp = millisUntilFinished + 1000;
                    long hours = millisUntilFinished_temp / (1000 * 60 * 60);
                    long minutes = (millisUntilFinished_temp % (1000 * 60 * 60)) / (1000 * 60);
                    long seconds = (millisUntilFinished_temp % (1000 * 60)) / (1000);
                    String hourstring = String.valueOf(hours);
                    String minutestring = String.valueOf(minutes);
                    String secondstring = String.valueOf(seconds);
                    if (hours < 10) {
                        hourstring = "0" + hourstring;
                    }
                    if (minutes < 10) {
                        minutestring = "0" + minutestring;
                    }
                    if (seconds < 10) {
                        secondstring = "0" + secondstring;
                    }
                    hnLabel.setText(String.format("%s", hourstring));
                    mnLabel.setText(String.format("%s", minutestring));
                    snLabel.setText(String.format("%s", secondstring));
                    progressBar.setProgress((int)(timerDuration_final / 1000 - millisUntilFinished / 1000)-1);
                }

                @Override
                public void onFinish() {
                    if(Integer.parseInt(hnLabel.getText().toString())==0&&Integer.parseInt(mnLabel.getText().toString())==0&&Integer.parseInt(snLabel.getText().toString())==1) {
                        progressBar.setProgress(progressBar.getProgress() + 1);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                        mp.start();
                        stopTimer();
                        Toast.makeText(TimerActivity.this, "Time's Up!", Toast.LENGTH_LONG).show();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerActivity.this, "Beep Once Timer")
                                .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
                                .setContentTitle("Time's Up!")
                                .setContentText("The timer has run out")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, builder.build());
                    }
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(TimerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}