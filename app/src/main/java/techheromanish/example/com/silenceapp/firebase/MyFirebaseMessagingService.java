package techheromanish.example.com.silenceapp.firebase;

/**
 * Created by Manish on 11/22/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import techheromanish.example.com.silenceapp.sinch.LoginActivity;
import techheromanish.example.com.silenceapp.sinch.PlaceCallActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("onMessageReceived","Check");

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {

                Log.d("Try","Just got into try");
                JSONObject json = new JSONObject(remoteMessage.getData().toString());


                parseNotificationData(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
//                Toast.makeText(this, "Exception found", Toast.LENGTH_SHORT).show();
            }
        }

    }


    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void parseNotificationData(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");
            JSONObject payload = data.getJSONObject("payload");

            int action = payload.getInt("action");
            String mobile  = payload.getString("mobile");
            Log.e("action",String.valueOf(action));

            //handling push
            handlePush(action, mobile);

        }
        catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
    }


    private void handlePush(int action, String mobile) {

        String requested_action="";

        switch (action){

            case 0: requested_action = "Camera";
                    handleVideoCallRequest(mobile);
                break;

            case 1: requested_action = "Sms";
                break;

            case 2: requested_action = "Call";
                break;

            case 3: requested_action = "Silence";
                handleSilenceActionRequest();
                break;

            case 4: requested_action = "Video";
                    break;

            default: requested_action = "Unknown";
        }

        Toast.makeText(this,requested_action +  " requested by a remote user.", Toast.LENGTH_SHORT).show();
    }

    private void handleVideoCallRequest(String mobile) {

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("mobile", mobile);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    private void handleSilenceActionRequest() {

        AudioManager am;
        am= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        if(am.getRingerMode()== AudioManager.RINGER_MODE_SILENT || am.getRingerMode()== AudioManager.RINGER_MODE_VIBRATE ){
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }else{

            Log.d("Broadcast result info","Already in non -silent mode");

        }


    }





}