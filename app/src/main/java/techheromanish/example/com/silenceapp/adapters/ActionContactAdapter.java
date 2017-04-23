package techheromanish.example.com.silenceapp.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.drawer_fragments.ActionContactsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sinch.LoginActivity;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 4/8/2017.
 */

public class ActionContactAdapter extends RecyclerView.Adapter<ActionContactAdapter.ViewHolder> {

    Context context;
    ArrayList<Contact> arrayListActionContacts;
    LayoutInflater layoutInflater;

    public  ActionContactAdapter(){

    }

    public ActionContactAdapter( Context context, ArrayList<Contact> arrayListActionContacts){

        this.context = context;
        this.arrayListActionContacts = arrayListActionContacts;
        layoutInflater = LayoutInflater.from(context);
    }

    public ActionContactAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.action_contact_list_row_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Contact contact = arrayListActionContacts.get(position);

        //setUp data to the UI
        setUIData(contact,holder);

        //setting OnClick listeners


    }

    private void setUIData(final Contact contact, ViewHolder holder) {

        //this end user's mobile
        final String mobile = KeyValueDb.get(context, Config.PRIMARY_MOBILE,"");


        holder.textViewName.setText(contact.getPrimary_mobile());

        if(contact.isCamera()){
            holder.imageViewCamera.setImageResource(R.drawable.ic_camera_selectedx100);
            holder.imageViewCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //other end user's mobile
                    String primary_mobile = contact.getPrimary_mobile();
                 //   Toast.makeText(context, primary_mobile, Toast.LENGTH_SHORT).show();
                    //defining action for the request
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("primary_mobile", primary_mobile);
                    context.startActivity(intent);
                    //putting the request forward
                    /** if the below request succeeds, then the call would be placed in the method below itself**/
                   // executeRequest(mobile, primary_mobile, action);


                }
            });
        }else{
            holder.imageViewCamera.setClickable(false);
        }

        if(contact.isSmsreminder()){
            holder.imageViewSms.setImageResource(R.drawable.ic_sms_selectedx100);
            holder.imageViewSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Call with Sms exception initiated", Toast.LENGTH_SHORT).show();
                    //other end user's mobile
                    String  primary_mobile = contact.getPrimary_mobile();
                    //defining action for the request
                    int action = 1;

                    //putting the request forward
                    /** if the below request succeeds, then the call would be placed in the method below itself**/
                    executeRequest(mobile, primary_mobile, action);
                }
            });
        }else{
            holder.imageViewSms.setClickable(false);
        }


        if(contact.isCallreminder()){

            holder.imageViewCall.setImageResource(R.drawable.ic_call_selectedx100);
            holder.imageViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //other end user's mobile
                    String  primary_mobile = contact.getPrimary_mobile();
                    //defining action for the request
                    int action = 2;

                    //putting the request forward
                    /** if the below request succeeds, then the call would be placed in the method below itself**/
                    executeRequest(mobile, primary_mobile, action);
                }
            });
        }else{
            holder.imageViewCall.setClickable(false);
        }

        if(contact.isSilenceexception()){
            holder.imageViewSilence.setImageResource(R.drawable.ic_silence_selectedx100);
            holder.imageViewSilence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //other end user's mobile
                    String  primary_mobile = contact.getPrimary_mobile();
                    //defining action for the request
                    int action = 3;

                    //putting the request forward
                    /** if the below request succeeds, then the call would be placed in the method below itself**/
                    executeRequest(mobile, primary_mobile, action);

                }
            });
        }else{
            holder.imageViewSilence.setClickable(false);
        }

        if(contact.isVideocall()){

            holder.imageViewVideo.setImageResource(R.drawable.ic_video_selectedx100);
            holder.imageViewVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //other end user's mobile
                    String  primary_mobile = contact.getPrimary_mobile();
                    //defining action for the request
                    int action = 4;


                    //putting the request forward
                    /** if the below request succeeds, then the call would be placed in the method below itself**/
                    executeRequest(mobile, primary_mobile, action);
                }
            });
        }else{
            holder.imageViewVideo.setClickable(false);
        }
    }

    public void executeRequest(final String mobile, final String primary_mobile, final int action) {


        if(action == 3) {
            ActionContactsFragment.relativeLayoutProgress.setVisibility(View.VISIBLE);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.SEND_REQUEST_URL,


                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(action == 3) {
                            ActionContactsFragment.relativeLayoutProgress.setVisibility(View.GONE);
                        }
                        Log.d("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");

                            if(!error){

                                boolean action_granted = jsonObject.getBoolean("action_granted");
                                if(action_granted){
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    //when request has been sent, then it's time to place the call to the number.
                                    if(action==3) {
                                        placeCall(primary_mobile);
                                    }
                                }else{
                                    Toast.makeText(context, "Action Denied", Toast.LENGTH_SHORT).show();
                                }

                            }else{

                                Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(action == 3) {
                            ActionContactsFragment.relativeLayoutProgress.setVisibility(View.GONE);
                        }
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();



                params.put("mobile",mobile);
                params.put("primary_mobile", primary_mobile);
                params.put("action", String.valueOf(action));
                //returning parameters
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Auth", Config.AUTH_KEY);
                return headers;
            }

        };

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);

    }


    public void placeCall(String primary_mobile) {

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + primary_mobile));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return arrayListActionContacts.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        TextView textViewName;
        ImageView imageViewCamera, imageViewSms, imageViewCall, imageViewSilence, imageViewVideo;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);

            imageViewCamera = (ImageView) itemView.findViewById(R.id.imageViewCamera);
            imageViewSms = (ImageView) itemView.findViewById(R.id.imageViewSms);
            imageViewCall = (ImageView) itemView.findViewById(R.id.imageViewCall);
            imageViewSilence = (ImageView) itemView.findViewById(R.id.imageViewSilence);
            imageViewVideo = (ImageView) itemView.findViewById(R.id.imageViewVideo);
        }
    }
}
