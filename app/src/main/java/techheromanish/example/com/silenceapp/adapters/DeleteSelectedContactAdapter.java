package techheromanish.example.com.silenceapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.models.Contact;

/**
 * Created by Manish on 3/27/2017.
 */

public class DeleteSelectedContactAdapter extends BaseAdapter {

    Context context;
    ArrayList<Contact> contactArrayList;
    LayoutInflater layoutInflater;

    public DeleteSelectedContactAdapter(Context context, ArrayList<Contact> contactArrayList){
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
            view = layoutInflater.inflate(R.layout.delete_contact_listview_row_layout,parent,false);

            holder.textViewName = (TextView) view.findViewById(R.id.textViewName);
            holder.imageViewCamera = (ImageView) view.findViewById(R.id.imageViewCamera);
            holder.imageViewSms = (ImageView) view.findViewById(R.id.imageViewSms);
            holder.imageViewCall = (ImageView) view.findViewById(R.id.imageViewCall);
            holder.imageViewSilence = (ImageView) view.findViewById(R.id.imageViewSilence);
            holder.imageViewVideo = (ImageView) view.findViewById(R.id.imageViewVideo);
            holder.checkBoxDelete = (CheckBox) view.findViewById(R.id.checkBoxSelect);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Contact contact = contactArrayList.get(position);

        setUpdata(holder,contact, position);

        return view;
    }

    private void setUpdata(ViewHolder holder, final Contact contact, final int position) {

        //setting data
        holder.textViewName.setText(contact.getContactName());

        if(contact.isCamera()){
            holder.imageViewCamera.setImageResource(R.drawable.ic_camera_selectedx100);
        }


        if(contact.isSmsreminder()){
            holder.imageViewSms.setImageResource(R.drawable.ic_sms_selectedx100);
        }

        if(contact.isCallreminder()){
            holder.imageViewCall.setImageResource(R.drawable.ic_call_selectedx100);
        }

        if(contact.isSilenceexception()){
            holder.imageViewSilence.setImageResource(R.drawable.ic_silence_selectedx100);
        }

        if(contact.isVideocall()){
            holder.imageViewVideo.setImageResource(R.drawable.ic_video_selectedx100);
        }
        //setting OnClick listener to the deleteCheckBox
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    //setting the current status
                    contact.setToBedeleted(true);

                }else{
                    //setting the current status
                    contact.setToBedeleted(false);
                }

                //updating the current object to the selected contacts list
                contactArrayList.set(position,contact);

            }
        });
    }




    public class ViewHolder{
        TextView textViewName;
        ImageView imageViewCamera, imageViewSms, imageViewCall, imageViewSilence, imageViewVideo;
        CheckBox checkBoxDelete;

    }
}
