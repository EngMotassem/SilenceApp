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
import techheromanish.example.com.silenceapp.broadcast.SmsReceiver;
import techheromanish.example.com.silenceapp.drawer_fragments.AddReminderFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.SmsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Message;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 4/1/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> messageList;
    Context context;

    public MessageAdapter(Context context, ArrayList<Message> messageList) {
        this.messageList = messageList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_row_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Message message = messageList.get(position);

        holder.textViewMessage.setText(message.getMessage());
        holder.textViewMobile.setText(message.getMobile());
        String reminder_time = message.getTime();
        reminder_time = reminder_time.substring(0, 4) + "-" + reminder_time.substring(4, 6) + "-" + reminder_time.substring(6, 8) + " " + reminder_time.substring(8, 13);
        holder.textViewTime.setText(reminder_time);

        //setting onClick to the imageViewCancel
        holder.imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removing the reminder object from the list
                Message message1 = messageList.get(position);
                messageList.remove(position);
                notifyDataSetChanged();
                //making Ui changes

                if (messageList!= null) {
                    if (messageList.size() == 0) {
                        SmsFragment.textViewNosms.setVisibility(View.VISIBLE);
                        SmsFragment.recyclerViewMessages.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(context, "reminderList is null.", Toast.LENGTH_SHORT).show();
                    SmsFragment.textViewNosms.setVisibility(View.VISIBLE);
                    SmsFragment.recyclerViewMessages.setVisibility(View.GONE);
                }
                //updating the changes in the sqlite
                updateMessageList();
                //cancelling the alarm
                cancelAlarm(message1);
            }
        });
    }

    private void cancelAlarm(Message message) {
        Intent intent = new Intent(context, SmsReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, message.getId(), intent, 0);
        SmsFragment.alarms.cancel(alarmIntent);

    }


    public void updateMessageList() {
        Type listOfObjects = new TypeToken<List<Message>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(messageList, listOfObjects);
        Log.d("listString", json);
        KeyValueDb.set(context, Config.MESSAGE_ARRAYLIST, json, 1);
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMobile, textViewMessage, textViewTime;
        ImageView imageViewCancel;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewMobile = (TextView) itemView.findViewById(R.id.textViewMobile);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            imageViewCancel = (ImageView) itemView.findViewById(R.id.imageViewCancel);

        }
    }
}