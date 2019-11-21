package com.example.musicplayer;

public class MusicUtils {

    public static final int MAX_PROGRESS = 10000;


    public String milliSecondsToTime(long milliseconds){

        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0){
            finalTimerString = hours + ":";
        }
        if (seconds < 10){
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }


    public int getProgressSeekBar (long currentDuration, long totalDuration){

        Double progress = (double) 0;
        progress = (((double)currentDuration) / totalDuration) * MAX_PROGRESS;

        return progress.intValue();
    }


    public int progressToTimer(int progress, int totalDuration){

        int currentDuration = 0;
        totalDuration = (int) ((double) progress / MAX_PROGRESS) * totalDuration;

        return currentDuration * 1000;
    }
}
