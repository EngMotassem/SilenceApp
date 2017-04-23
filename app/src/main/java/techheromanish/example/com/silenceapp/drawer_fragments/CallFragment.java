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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.CallAdapter;
import techheromanish.example.com.silenceapp.adapters.MessageAdapter;
import techheromanish.example.com.silenceapp.broadcast.CallReceiver;
import techheromanish.example.com.silenceapp.broadcast.SmsReceiver;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Call;
import techheromanish.example.com.silenceapp.models.Message;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

import static techheromanish.example.com.silenceapp.activities.HomeActivity.navigationView;

/**
 * Created by Manish on 3/26/2017.
 */

public class CallFragment extends Fragment implements View.OnClickListener {

    TextView textViewDate, textViewTime;
    EditText editTextMobile;
    ImageView imageViewBack;
    Button buttonScheduleCall;
    Toolbar toolbar;
    private int MAKE_CALL_PERMISSIONS_REQUEST = 123;
    public static RecyclerView recyclerViewCalls;
    public static TextView textViewNoCalls;
    ArrayList<Call> callList;
    LinearLayoutManager layoutManager;
    String selectedDate, selectedTime, previous_time = "";
    int day, mon, yr, hour, min;
    public static AlarmManager alarms;
    private static CallAdapter callAdapter;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_schedule,container,false);

        //asking for sms permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToMakeCalls();
        }

        //initializing recyclerView
        recyclerViewCalls = (RecyclerView) view.findViewById(R.id.recyclerViewCalls);
        //initializing textViewNoreminder textView
        textViewNoCalls = (TextView) view.findViewById(R.id.textViewNocalls);
        //initializing messageList
        callList = getCallList();
        //checking if the messageList is null
        if(callList==null){
            callList = new ArrayList<Call>();
        }else{
              if(callList.size()!=0) {
                  recyclerViewCalls.setVisibility(View.VISIBLE);
                  textViewNoCalls.setVisibility(View.GONE);
              }
        }


        //initializing Views
        initViews(view);

        return view;
    }

    private ArrayList<Call> getCallList() {
        String listString = KeyValueDb.get(getActivity(), Config.CALL_ARRAYLIST,"");
        Type listOfObjects = new TypeToken<List<Call>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);

    }

    private void initViews(View view) {

        //initializing views
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

        editTextMobile = (EditText) view.findViewById(R.id.editTextMobile);

        buttonScheduleCall = (Button) view.findViewById(R.id.buttonScheduleCall);

        //setting OnClick listener
        textViewDate.setOnClickListener(this);
        textViewTime.setOnClickListener(this);
        buttonScheduleCall.setOnClickListener(this);

        //setting up the recyclerView
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewCalls.setLayoutManager(layoutManager);
        //initializing the recyclerView Adapter
        callAdapter = new CallAdapter(getActivity(), callList);
        recyclerViewCalls.setAdapter(callAdapter);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToMakeCalls() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CALL_PHONE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    MAKE_CALL_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == MAKE_CALL_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), " permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

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
        } else if (v == buttonScheduleCall) {

            checkAndScheduleCall();

        }

    }

    private void checkAndScheduleCall() {

        //checking if the time has been set or not
        if(textViewTime.getText().toString().charAt(0)!='S'){

            //checking if date has been set or not
            if(textViewDate.getText().toString().charAt(0)!='S'){
                //checking if mobile has been set or not
                if(!editTextMobile.getText().toString().isEmpty()){

                        if(!previous_time.equals(textViewTime.getText().toString())){
                            scheduleCall();
                        }else{
                            Toast.makeText(getActivity(), "Call has already been scheduled to this time", Toast.LENGTH_SHORT).show();
                        }
                } else{
                    Toast.makeText(getActivity(), "Mobile cannot be empty.", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getActivity(), "Please select a valid date", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getActivity(), "Please select a valid time.", Toast.LENGTH_SHORT).show();
        }


    }

    private void scheduleCall() {

        previous_time = selectedTime;

        String mobile = editTextMobile.getText().toString();

        String call_time = selectedDate+ selectedTime;

        Calendar cal = Calendar.getInstance();
        cal.set(yr, mon, day, hour, min);
        Intent intent = new Intent(getActivity(), CallReceiver.class);
        PendingIntent alarmIntent;
        int requestCode = Integer.parseInt(KeyValueDb.get(getActivity(), Config.CALL_COUNTER,"1"));

        alarmIntent = PendingIntent.getBroadcast(getActivity(), requestCode , intent, 0);

        Log.d("requestCode",""+ requestCode);
        alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);

        //saving the data for the call
        Call call = new Call(mobile,call_time,requestCode);

        Log.d("reminder_time",call_time);
        callList.add(call);
        callAdapter.notifyDataSetChanged();

        if(requestCode >=1){
            textViewNoCalls.setVisibility(View.GONE);
            recyclerViewCalls.setVisibility(View.VISIBLE);
        }
        //incrementing the requestCode by the value of 1
        KeyValueDb.set(getActivity(),Config.CALL_COUNTER, String.valueOf(++requestCode),1);

        saveCallArrayList();

        Toast.makeText(getActivity(), "Call has been scheduled at " + call_time, Toast.LENGTH_SHORT).show();

    }

    private void saveCallArrayList() {
        Type listOfObjects = new TypeToken<List<Call>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(callList,listOfObjects);
        Log.d("listString",json);
        KeyValueDb.set(getActivity(),Config.CALL_ARRAYLIST,json,1);
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
}
