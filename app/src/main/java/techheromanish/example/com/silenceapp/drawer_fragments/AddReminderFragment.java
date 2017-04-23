package techheromanish.example.com.silenceapp.drawer_fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.primitives.UnsignedInts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.ReminderAdapter;
import techheromanish.example.com.silenceapp.broadcast.Alarm;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

import static techheromanish.example.com.silenceapp.activities.HomeActivity.navigationView;

/**
 * Created by Manish on 3/26/2017.
 */

public class AddReminderFragment extends Fragment implements View.OnClickListener {

    private static final int READ_CALENDAR_PERMISSIONS_REQUEST = 12312;
    ArrayList<Reminder> reminderArrayList;
    Toolbar toolbar;
    ImageView imageViewBack;
    TextView textViewDate, textViewTime;
    EditText editTextTitle, editTextDescription;
    Button buttonRemindme;
    String selectedDate, selectedTime;
    int day, mon, yr, hour, min;
    public static AlarmManager alarms ;
   public static RecyclerView recyclerViewReminders;
    ReminderAdapter reminderAdapter;
    LinearLayoutManager layoutManager;
    public  static TextView textViewNoreminder;
    String previous_time = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        //initializing recyclerView
        recyclerViewReminders = (RecyclerView) view.findViewById(R.id.recyclerViewReminders);
        //initializing textViewNoreminder textView
        textViewNoreminder = (TextView) view.findViewById(R.id.textViewNoReminder);
        //getting the reminderList and intentList data
        reminderArrayList = getReminderArrayList();
        //initializing if the arraylists are null
        if(reminderArrayList==null){
            reminderArrayList = new ArrayList<Reminder>();
        }else{
            recyclerViewReminders.setVisibility(View.VISIBLE);
            textViewNoreminder.setVisibility(View.GONE);
        }

        //if the size is 0, then making the NoReminder textViewvisible

        //getting permissions to write the calendar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToWriteCalendar();
        }
        //initializing Views
        initViews(view);
        //returning view
        return view;
    }


    private void initViews(View view) {

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //init toolbar items
        imageViewBack = (ImageView) toolbar.findViewById(R.id.imageViewBack);
        //setting onCLick listener
        imageViewBack.setOnClickListener(this);

        //hiding the previous toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //setting the new toolbar with a blank title
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewTime = (TextView) view.findViewById(R.id.textViewTime);

        editTextTitle = (EditText) view.findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);

        buttonRemindme = (Button) view.findViewById(R.id.buttonRemindme);

        //setting OnClick listeners
        textViewDate.setOnClickListener(this);
        textViewTime.setOnClickListener(this);
        buttonRemindme.setOnClickListener(this);

        //settin up the recyclerViewReminder
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewReminders.setLayoutManager(layoutManager);
        //initializing the recyclerView Adapter
        reminderAdapter = new ReminderAdapter(getActivity(),reminderArrayList);
        //setting adapter to the recyclerView
        recyclerViewReminders.setAdapter(reminderAdapter);

        if(reminderArrayList.size()==0){
            textViewNoreminder.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        if (v == imageViewBack) {
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment());
            ft.commit();
        } else if (v == textViewDate) {

            selectDate();

        } else if (v == textViewTime) {

            selectTime();
        } else if (v == buttonRemindme) {
            checkAndsetReminder();

        }

    }

    private void checkAndsetReminder() {

        //checking if the time has been set or not
        if(textViewTime.getText().toString().charAt(0)!='S'){

            //checking if date has been set or not
            if(textViewDate.getText().toString().charAt(0)!='S'){
                //checking if title has been set or not
                if(!editTextTitle.getText().toString().isEmpty()){

                    //checking if description has been set or not
                    if(!editTextDescription.getText().toString().isEmpty()){
                        //when everything is A-Okay, set the reminder
                        if(!previous_time.equals(textViewTime.getText().toString())){
                            setReminder();    
                        }else{
                            Toast.makeText(getActivity(), "Reminder has already been set to this time", Toast.LENGTH_SHORT).show();
                        }
                        

                    }else{
                        Toast.makeText(getActivity(), "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(), "Title cannot be empty.", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getActivity(), "Please select a valid date", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getActivity(), "Please select a valid time.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setReminder() throws IllegalArgumentException {

        previous_time = selectedTime;

        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        String reminder_time = selectedDate+ selectedTime;

        Calendar cal = Calendar.getInstance();
        cal.set(yr, mon, day, hour, min);
        Intent intent = new Intent(getActivity(), Alarm.class);
        PendingIntent alarmIntent;
        int requestCode = Integer.parseInt(KeyValueDb.get(getActivity(), Config.REMINDER_COUNTER,"1"));

        alarmIntent = PendingIntent.getBroadcast(getActivity(), requestCode , intent, 0);

        Log.d("requestCode",""+ requestCode);
        alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);

        //saving the data for the reminder
        Reminder rem = new Reminder(requestCode,reminder_time.trim(),title,description);
        Log.d("reminder_time",reminder_time);
        reminderArrayList.add(rem);
        reminderAdapter.notifyDataSetChanged();
        if(requestCode >=1){
            textViewNoreminder.setVisibility(View.GONE);
            recyclerViewReminders.setVisibility(View.VISIBLE);
        }
        //incrementing the requestCode by the value of 1
        KeyValueDb.set(getActivity(),Config.REMINDER_COUNTER, String.valueOf(++requestCode),1);

        saveReminderArrayList();

        Toast.makeText(getActivity(), "Alarm has been set up.", Toast.LENGTH_SHORT).show();
    }




        public  void saveReminderArrayList(){
            Type listOfObjects = new TypeToken<List<Reminder>>(){}.getType();
            Gson gson = new Gson();
            String json = gson.toJson(reminderArrayList,listOfObjects);
            Log.d("listString",json);
            KeyValueDb.set(getActivity(),Config.REMINDER_ARRAYLIST,json,1);
    }

        public  ArrayList<Reminder> getReminderArrayList(){
            String listString = KeyValueDb.get(getActivity(), Config.REMINDER_ARRAYLIST,"");
            Type listOfObjects = new TypeToken<List<Reminder>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(listString, listOfObjects);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToWriteCalendar() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_CALENDAR)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    READ_CALENDAR_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CALENDAR_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), " permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void selectTime() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        hour = hourOfDay;
                        min = minute;
                        Calendar cal = Calendar.getInstance();
                        Toast.makeText(getActivity(), "" + hourOfDay + minute, Toast.LENGTH_SHORT).show();

                        String hourstr, minstr;

                        if(hourOfDay<10){
                            hourstr = "0"+hourOfDay;
                        }else{
                            hourstr = String.valueOf(hourOfDay);
                        }

                        if (minute < 10) {
                            minstr = "0"+minute;
                        }else{
                            minstr = String.valueOf(minute);
                        }

                        selectedTime = hourstr + ":"+minstr;

                        textViewTime.setText(selectedTime);

                    }
                }, mHour, mMinute, true);

        timePickerDialog.show();

    }

    private void selectDate() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // TODO Auto-generated method stub
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String showFormat = "yyyy-MM-dd"; //In which you need put here
                        String saveFormat = "yyyyMMdd"; //In which you need put here
                        SimpleDateFormat showdf = new SimpleDateFormat(showFormat);
                        SimpleDateFormat savedf = new SimpleDateFormat(saveFormat);
                        //setting date to the textView
                        textViewDate.setText(showdf.format(myCalendar.getTime()));
                        //gettintime in diff format to be used to as primary key for the reminder
                        selectedDate =  savedf.format(myCalendar.getTime());

                        day = dayOfMonth;
                        mon = monthOfYear;
                        yr = year;

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
