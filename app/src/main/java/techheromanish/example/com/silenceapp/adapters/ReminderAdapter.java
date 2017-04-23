package techheromanish.example.com.silenceapp.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.broadcast.Alarm;
import techheromanish.example.com.silenceapp.drawer_fragments.AddReminderFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 3/31/2017.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    ArrayList<Reminder> reminderList;
    Context context;

    public ReminderAdapter(Context context, ArrayList<Reminder> reminderList){
        this.reminderList = reminderList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_row_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Reminder reminder = reminderList.get(position);

        holder.textViewTitle.setText(reminder.getTitle());
        String reminder_time = reminder.getTime();
        reminder_time = reminder_time.substring(0,4)+"-"+reminder_time.substring(4,6)+"-"+reminder_time.substring(6,8)+" "+ reminder_time.substring(8,13);
        holder.textViewTime.setText(reminder_time);

        //setting onClick to the imageViewCancel
        holder.imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removing the reminder object from the list
                Reminder rem = reminderList.get(position);
                reminderList.remove(position);
                notifyDataSetChanged();
                //making Ui changes

                if(reminderList!=null) {
                    if (reminderList.size() == 0) {
                        AddReminderFragment.textViewNoreminder.setVisibility(View.VISIBLE);
                        AddReminderFragment.recyclerViewReminders.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(context, "reminderList is null.", Toast.LENGTH_SHORT).show();
                    AddReminderFragment.textViewNoreminder.setVisibility(View.VISIBLE);
                    AddReminderFragment.recyclerViewReminders.setVisibility(View.GONE);
                }
                //updating the changes in the sqlite
                updateReminderArrayList();
                //cancelling the alarm
                cancelAlarm(rem);
            }
        });
    }

    private void cancelAlarm(Reminder reminder) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, reminder.getId() , intent, 0);
        AddReminderFragment.alarms.cancel(alarmIntent);

    }


    public  void updateReminderArrayList(){
        Type listOfObjects = new TypeToken<List<Reminder>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList,listOfObjects);
        Log.d("listString",json);
        KeyValueDb.set(context, Config.REMINDER_ARRAYLIST,json,1);
    }


    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        TextView textViewTitle, textViewTime;
         ImageView imageViewCancel;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = (TextView) itemView.findViewById(R.id.textViewRemTitle);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            imageViewCancel = (ImageView) itemView.findViewById(R.id.imageViewCancel);

        }
    }
}
