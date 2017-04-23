package techheromanish.example.com.silenceapp.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import techheromanish.example.com.silenceapp.activities.CallAlertActivity;

/**
 * Created by Manish on 3/31/2017.
 */

public class CallReceiver extends WakefulBroadcastReceiver {



    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("onReceive", "Just got into onReceieve");

        Intent i = new Intent(context, CallAlertActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }



}
