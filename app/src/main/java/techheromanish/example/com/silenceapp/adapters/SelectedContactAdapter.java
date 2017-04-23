package techheromanish.example.com.silenceapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 3/27/2017.
 */

public class SelectedContactAdapter extends BaseAdapter {

    Context context;
    ArrayList<Contact> contactArrayList;
    LayoutInflater layoutInflater;

    public SelectedContactAdapter(Context context, ArrayList<Contact> contactArrayList){
        this.context = context;
        this.contactArrayList = contactArrayList;
        layoutInflater = LayoutInflater.from(context);
        Log.d("contactArraylistsize", String.valueOf(contactArrayList.size()));
    }



    @Override
    public int getCount() {
        return contactArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view==null){
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.selected_contact_listview_row_layout,parent,false);

            holder.textViewName = (TextView) view.findViewById(R.id.textViewName);
            holder.imageViewCamera = (ImageView) view.findViewById(R.id.imageViewCamera);
            holder.imageViewSms = (ImageView) view.findViewById(R.id.imageViewSms);
            holder.imageViewCall = (ImageView) view.findViewById(R.id.imageViewCall);
            holder.imageViewSilence = (ImageView) view.findViewById(R.id.imageViewSilence);
            holder.imageViewVideo = (ImageView) view.findViewById(R.id.imageViewVideo);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Contact contact = contactArrayList.get(position);

        setUpdata(holder,contact);

        return view;
    }

    private void setUpdata(ViewHolder holder, Contact contact) {

        //setting data
        holder.textViewName.setText(contact.getContactName());

        if(contact.isCamera()){
            holder.imageViewCamera.setImageResource(R.drawable.ic_camera_selectedx100);
        }else{
            holder.imageViewCamera.setImageResource(R.drawable.ic_camera_unselectedx100);
        }


        if(contact.isSmsreminder()){
            holder.imageViewSms.setImageResource(R.drawable.ic_sms_selectedx100);
        }else{
            holder.imageViewSms.setImageResource(R.drawable.ic_sms_unselectedx100);
        }

        if(contact.isCallreminder()){
            holder.imageViewCall.setImageResource(R.drawable.ic_call_selectedx100);
        }else{
            holder.imageViewCall.setImageResource(R.drawable.ic_call_unselectedx100);
        }

        if(contact.isSilenceexception()){
            holder.imageViewSilence.setImageResource(R.drawable.ic_silence_selectedx100);
        }else{
            holder.imageViewSilence.setImageResource(R.drawable.ic_silence_unselectedx100);
        }

        if(contact.isVideocall()){
            holder.imageViewVideo.setImageResource(R.drawable.ic_video_selectedx100);
        }else{
            holder.imageViewVideo.setImageResource(R.drawable.ic_video_unselectedx100);
        }
    }




    public class ViewHolder{
        TextView textViewName;
        ImageView imageViewCamera, imageViewSms, imageViewCall, imageViewSilence, imageViewVideo;

    }
}
