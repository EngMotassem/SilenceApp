package techheromanish.example.com.silenceapp.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import techheromanish.example.com.silenceapp.activities.SmsAlertActivity;

/**
 * Created by Manish on 3/31/2017.
 */

public class SmsReceiver extends WakefulBroadcastReceiver {



    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("onReceive", "Just got into onReceieve");
        String mobile = intent.getStringExtra("mobile");
        String message = intent.getStringExtra("message");

        if(mobile!=null) {
            Log.d("mobileinOnReceive", mobile);
            Log.d("message", message);
        }
        Intent i = new Intent(context, SmsAlertActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }



}
