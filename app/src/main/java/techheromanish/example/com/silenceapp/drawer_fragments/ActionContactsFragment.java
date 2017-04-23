package techheromanish.example.com.silenceapp.drawer_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.ActionContactAdapter;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.models.Reminder;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

import static techheromanish.example.com.silenceapp.activities.HomeActivity.navigationView;

/**
 * Created by Manish on 4/8/2017.
 */

public class ActionContactsFragment extends Fragment implements View.OnClickListener {

    Toolbar toolbar;
    ImageView imageViewBack;
    RecyclerView recyclerViewContacts;
    ArrayList<Contact> arrayListActionContacts;
    LinearLayoutManager layoutManager;
    ActionContactAdapter actionAdapter;
    TextView textViewNoContacts;
    //use boolean flag to handle the back button action for the relativeLayout
    public static RelativeLayout relativeLayoutProgress;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_action_contacts, container, false);

        //initializing Views
        initViews(view);


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



        //initializing progressLayout
        relativeLayoutProgress = (RelativeLayout) view.findViewById(R.id.relativeProgressbar);
        //initializing recyclerView and setting Layout Manager
        recyclerViewContacts = (RecyclerView) view.findViewById(R.id.recyclerViewContacts);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewContacts.setLayoutManager(layoutManager);
        //initializing textViewNoreminder textView
        textViewNoContacts = (TextView) view.findViewById(R.id.textViewNoContacts);
        //initializing if the arraylists are null
        arrayListActionContacts = new ArrayList<Contact>();

        //getting Action Contacts from the server
        getActionContacts();



    }



    @Override
    public void onClick(View v) {

        if (v == imageViewBack) {
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment());
            ft.commit();
        }
    }

    private void getActionContacts() {

        relativeLayoutProgress.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.GET_ACTION_CONTACTS_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayoutProgress.setVisibility(View.GONE);

                        Log.d("response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray contactArray = jsonObject.getJSONArray("action_contacts");
                            Log.d("jsonArray",contactArray.toString());

                            int i;
                            for(i=0;i<contactArray.length();i++){

                                JSONObject obj = contactArray.getJSONObject(i);

                                String primary_mobile = obj.getString("primary_mobile");
                                int camera = obj.getInt("camera");
                                int sms = obj.getInt("sms");
                                int call = obj.getInt("call");
                                int silence = obj.getInt("silence");
                                int video = obj.getInt("video");

                                Contact contact = new Contact();
                                contact.setPrimary_mobile(primary_mobile);
                                contact.setCamera(camera==1);
                                contact.setSmsreminder(sms==1);
                                contact.setCallreminder(call==1);
                                contact.setSilenceexception(silence==1);
                                contact.setVideocall(video==1);

                                //adding the contact object into the arrayList
                                arrayListActionContacts.add(contact);


                            }

                            if(i!=0){
                                actionAdapter = new ActionContactAdapter(getActivity(), arrayListActionContacts);
                                recyclerViewContacts.setAdapter(actionAdapter);

                            }else{
                                recyclerViewContacts.setVisibility(View.GONE);
                                textViewNoContacts.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        relativeLayoutProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Bad Network Connection.", Toast.LENGTH_SHORT).show();

                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();

                //fetching data to be stored
                String mobile = KeyValueDb.get(getActivity(), Config.PRIMARY_MOBILE, "");


                params.put("mobile",mobile);
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

        MyVolley.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }


}
