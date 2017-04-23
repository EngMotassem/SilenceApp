package techheromanish.example.com.silenceapp.broadcast;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import techheromanish.example.com.silenceapp.activities.ReminderAlertActivity;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 3/30/2017.
 */

public class Alarm extends WakefulBroadcastReceiver {

    public static  Ringtone ringtone;

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("onReceive", "Just got into onReceieve");
        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)


        Intent i = new Intent(context, ReminderAlertActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }



}