package techheromanish.example.com.silenceapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.broadcast.Alarm;
import techheromanish.example.com.silenceapp.drawer_fragments.AddReminderFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class ReminderAlertActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewReminder, textViewReminderTitle, textViewReminderDesc;
    Button buttonDismiss;
    ArrayList<Reminder> reminderList;
    ArrayList<Reminder> newreminderList;
    Ringtone ringtone;
    int rem_index = 0;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_reminder_alert);

        newreminderList = new ArrayList<Reminder>();

        //initializing Views
        showDialog(this);
    }


    private void setUIData() {

        reminderList = new ArrayList<Reminder>();
        //initializing reminderList with the previous values
        reminderList = getReminderArrayList(this);

        String title = "";
        String desc = "";
        String current_time = getCurrentTime();
        Log.d("current_time",current_time);
        if(reminderList!=null) {

            for (int i = 0; i < reminderList.size(); i++) {

                Reminder rem = reminderList.get(i);
                String time = rem.getTime();
                if (time.trim().equals(current_time.trim())) {

                    rem_index = i;
                    title = rem.getTitle();
                    desc = rem.getDescription();
                    Log.d("index", String.valueOf(rem_index));
                    //removing the current object from the reminderList
                    reminderList.remove(i);

                    //update the reminderList and Pending ArrayList
                }else{
                    newreminderList.add(rem);
                }

            }

            saveReminderArrayList();
        }


        textViewReminderTitle.setText(title);
        textViewReminderDesc.setText(desc);

        Log.d("title",title);
        Log.d("desc",desc);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
    }





    public  void saveReminderArrayList(){
        Type listOfObjects = new TypeToken<List<Reminder>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(newreminderList,listOfObjects);
        Log.d("listString",json);
        KeyValueDb.set(this,Config.REMINDER_ARRAYLIST,json,1);
    }


    private String getCurrentTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkk:mm ", Locale.US);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());
    }

    public synchronized ArrayList<Reminder> getReminderArrayList(Context context){

        String listString = KeyValueDb.get(context, Config.REMINDER_ARRAYLIST,"");
        Type listOfObjects = new TypeToken<List<Reminder>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);
    }


    @Override
    public void onClick(View v) {

        ringtone.stop();
        this.finish();
        dialog.dismiss();

        if(reminderList!=null) {
            if (reminderList.size() == 0) {
                AddReminderFragment.textViewNoreminder.setVisibility(View.VISIBLE);
                AddReminderFragment.recyclerViewReminders.setVisibility(View.GONE);
            }
        }else{
            AddReminderFragment.textViewNoreminder.setVisibility(View.VISIBLE);
            AddReminderFragment.recyclerViewReminders.setVisibility(View.GONE);
        }

    }

    public void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_reminder_alert);

        textViewReminder = (TextView) dialog.findViewById(R.id.textViewReminder);
        textViewReminderTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
        textViewReminderDesc = (TextView) dialog.findViewById(R.id.textViewDescription);

        buttonDismiss = (Button) dialog.findViewById(R.id.buttonDismiss);

        //setting OnClick listener
        buttonDismiss.setOnClickListener(this);
        //setting up the data
        setUIData();




        dialog.show();

    }

}
