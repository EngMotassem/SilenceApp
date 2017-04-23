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
import techheromanish.example.com.silenceapp.broadcast.CallReceiver;
import techheromanish.example.com.silenceapp.broadcast.SmsReceiver;
import techheromanish.example.com.silenceapp.drawer_fragments.CallFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.SmsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Call;
import techheromanish.example.com.silenceapp.models.Message;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 4/1/2017.
 */

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {

    ArrayList<Call> callList;
    Context context;

    public CallAdapter(Context context, ArrayList<Call> callList) {
        this.callList = callList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_row_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Call call = callList.get(position);

        holder.textViewMobile.setText(call.getMobile());
        String call_time = call.getTime();
        call_time = call_time.substring(0, 4) + "-" + call_time.substring(4, 6) + "-" + call_time.substring(6, 8) + " " + call_time.substring(8, 13);
        holder.textViewTime.setText(call_time);

        //setting onClick to the imageViewCancel
        holder.imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removing the reminder object from the list
                Call call1 = callList.get(position);
                callList.remove(position);
                notifyDataSetChanged();
                //making Ui changes

                if (callList!= null) {
                    if (callList.size() == 0) {
                        CallFragment.textViewNoCalls.setVisibility(View.VISIBLE);
                        CallFragment.recyclerViewCalls.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(context, "CallList is null.", Toast.LENGTH_SHORT).show();
                    CallFragment.textViewNoCalls.setVisibility(View.VISIBLE);
                    CallFragment.recyclerViewCalls.setVisibility(View.GONE);
                }
                //updating the changes in the sqlite
                updateCallList();
                //cancelling the alarm
                cancelAlarm(call1);
            }
        });
    }

    private void cancelAlarm(Call call) {
        Intent intent = new Intent(context, CallReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, call.getId(), intent, 0);
        CallFragment.alarms.cancel(alarmIntent);

    }


    public void updateCallList() {
        Type listOfObjects = new TypeToken<List<Call>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(callList, listOfObjects);
        Log.d("listString", json);
        KeyValueDb.set(context, Config.CALL_ARRAYLIST, json, 1);
    }


    @Override
    public int getItemCount() {
        return callList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMobile, textViewTime;
        ImageView imageViewCancel;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewMobile = (TextView) itemView.findViewById(R.id.textViewMobile);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            imageViewCancel = (ImageView) itemView.findViewById(R.id.imageViewCancel);

        }
    }
}