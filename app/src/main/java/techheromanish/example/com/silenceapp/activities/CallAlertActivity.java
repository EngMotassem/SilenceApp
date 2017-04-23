package techheromanish.example.com.silenceapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.drawer_fragments.CallFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.SmsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Call;
import techheromanish.example.com.silenceapp.models.Message;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class CallAlertActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Call> callList;
    ArrayList<Call> newCAllList;
    private Context context;
    TextView textViewMobile;
    Button buttonDismiss;
    Ringtone ringtone;
    String mobile = "";
    Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newCAllList = new ArrayList<Call>();

        context = this;


        //initViews
        showDialog(this);

    }


    private void handleBroadcast() {
        //getting current_time
        String current_time = getCurrentTime();
        Log.d("current_time", current_time);
        //getting the message arraylist
        callList = getCallList();

        if (callList != null) {

            for (int i = 0; i < callList.size(); i++) {

                Call call = callList.get(i);
                String time = call.getTime();

                if (time.trim().equals(current_time.trim())) {
                    mobile = call.getMobile();
                    Log.d("index", String.valueOf(i));
                    //removing the current object from the reminderList
                    //update the callList
                }else{
                    newCAllList.add(call);
                }
            }

            saveCallArrayList();
        }

        Log.d("mobile", mobile);
        //setting UI data
        setUIdata(mobile);

        //making the call
        placeCall();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();

    }

    private void setUIdata(String mobile) {
        textViewMobile.setText(mobile);
    }


    private void saveCallArrayList() {
        Type listOfObjects = new TypeToken<List<Call>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(newCAllList, listOfObjects);
        Log.d("listString", json);
        KeyValueDb.set(context, Config.CALL_ARRAYLIST, json, 1);
    }

    private ArrayList<Call> getCallList() {
        String listString = KeyValueDb.get(context, Config.CALL_ARRAYLIST, "");
        Type listOfObjects = new TypeToken<List<Call>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);
    }

    private String getCurrentTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkk:mm ", Locale.US);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

    public void placeCall() {

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + mobile));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        if(v==buttonDismiss){
            ringtone.stop();
            this.finish();
            dialog.dismiss();

            CallFragment.textViewNoCalls.setVisibility(View.VISIBLE);
            CallFragment.recyclerViewCalls.setVisibility(View.GONE);
        }

    }


    //Alert Dialog after delete


        public void showDialog(Activity activity){
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.activity_call_alert);

            textViewMobile = (TextView) dialog.findViewById(R.id.textViewMobile);
            buttonDismiss = (Button) dialog.findViewById(R.id.buttonDismiss);


            //setting OnClick listener
            buttonDismiss.setOnClickListener(this);
            //handling the broadcast
            handleBroadcast();


            dialog.show();

        }



}
