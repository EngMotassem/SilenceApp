package techheromanish.example.com.silenceapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import techheromanish.example.com.silenceapp.activities.SplashActivity;
import techheromanish.example.com.silenceapp.drawer_fragments.AddReminderFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.SmsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.MyNotificationManager;
import techheromanish.example.com.silenceapp.models.Message;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class SmsAlertActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Message> messageList;
    ArrayList<Message> newmessageList;
    private Context context;
    TextView textViewMobile, textViewMessage;
    Button buttonDismiss;
    Ringtone ringtone;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting it transparent
        newmessageList = new ArrayList<Message>();

        context = this;


        //initViews
        showDialog(this);


    }

    public void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_sms_alert);

        textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessagecontent);
        textViewMobile = (TextView) dialog.findViewById(R.id.textViewMobile);
        buttonDismiss = (Button) dialog.findViewById(R.id.buttonDismiss);


        //setting OnClick listener
        buttonDismiss.setOnClickListener(this);
        //handling the broadcast
        handleBroadcast();



        dialog.show();

    }





    private void handleBroadcast() {

        //getting current_time
        String current_time = getCurrentTime();
        Log.d("current_time",current_time);
        //getting the message arraylist
        messageList = getMessageList();

        String mobile = "", message = "";

        if(messageList!=null) {

            for (int i = 0; i < messageList.size(); i++) {

                Message msg = messageList.get(i);
                String time = msg.getTime();

                if (time.trim().equals(current_time.trim())) {
                    mobile = msg.getMobile();
                    message = msg.getMessage();
                    Log.d("index", String.valueOf(i));
                }else{
                    newmessageList.add(msg);
                }
            }

            saveMessageArrayList();
        }

        Log.d("mobile",mobile);
        Log.d("message",message);
        //setting UI data
        setUIdata(mobile,message);

        //sending the message
        sendSMS(mobile,message);


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

         ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();

    }

    private void setUIdata(String mobile, String message) {

        textViewMessage.setText(message);
        textViewMobile.setText(mobile);
    }


    private void saveMessageArrayList() {
        Type listOfObjects = new TypeToken<List<Message>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(newmessageList,listOfObjects);
        Log.d("listString",json);
        KeyValueDb.set(context, Config.MESSAGE_ARRAYLIST,json,1);
    }

    private ArrayList<Message> getMessageList() {
        String listString = KeyValueDb.get( context, Config.MESSAGE_ARRAYLIST,"");
        Type listOfObjects = new TypeToken<List<Message>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);
    }

    private String getCurrentTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkk:mm ", Locale.US);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.d("smsstatus","Message sent");
        } catch (Exception ex) {
            Log.d("smsstatus",ex.getMessage().toString());
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if(v==buttonDismiss){
            ringtone.stop();
            this.finish();
            dialog.dismiss();


            if(messageList!=null) {
                if (messageList.size() == 0) {
                    SmsFragment.textViewNosms.setVisibility(View.VISIBLE);
                    SmsFragment.recyclerViewMessages.setVisibility(View.GONE);
                }
            }else{
                SmsFragment.textViewNosms.setVisibility(View.VISIBLE);
                SmsFragment.recyclerViewMessages.setVisibility(View.GONE);
            }
        }

    }
}
