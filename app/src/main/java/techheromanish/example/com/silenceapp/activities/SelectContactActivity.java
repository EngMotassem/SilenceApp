package techheromanish.example.com.silenceapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.ContactAdapter;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class SelectContactActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    RecyclerView recyclerViewContacts;
    LinearLayoutManager layoutManager;
    ArrayList<Contact> contactArrayList;
    ArrayList<Contact> selectedContactList;
    ContactAdapter contactAdapter;
    Toolbar toolbar;
    TextView textViewTitle, textViewDone;
    RelativeLayout relativeLayoutProgress;
    EditText editTextSearch;
    private int RECORD_AUDIO_REQUEST_CODE = 2023;
    private int CAMERA_REQUEST_CODE = 4993;
   // EditText editTextSearch;
    boolean flag = false, noItemSelected = true;
    private Timer mTimer;
  //  private MyTimerTask mMyTimerTask;
    private int currentScrollY = 0;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        //getting permission to record video
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordVideo();
        }


        initViews();
    }

    private void initViews() {


        //intializing toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        textViewTitle = (TextView) toolbar.findViewById(R.id.textViewTitle);
        textViewDone = (TextView) toolbar.findViewById(R.id.textViewDone);
        textViewTitle.setText("Select Contacts");

        //init EditText Search
//        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
//        //setting onText Changed Listener
//        editTextSearch.addTextChangedListener(this);

        //setting toolbar
        setSupportActionBar(toolbar);


        //setting onClick listener to textViewDone
        textViewDone.setOnClickListener(this);

        recyclerViewContacts = (RecyclerView) findViewById(R.id.recyclerViewContacts);
        relativeLayoutProgress = (RelativeLayout) findViewById(R.id.relativeProgressbar);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewContacts.setLayoutManager(layoutManager);
        recyclerViewContacts.setHasFixedSize(true);
        recyclerViewContacts.setVerticalScrollBarEnabled(true);
        recyclerViewContacts.setNestedScrollingEnabled(true);

        //adding textChanged listener to the editTextSearch
        editTextSearch.addTextChangedListener(this);

        contactArrayList = new ArrayList<Contact>();
        selectedContactList = new ArrayList<Contact>();

        String listString = KeyValueDb.get(this, Config.CONTACTS_ARRAYLIST,"");

        contactArrayList = getContactArrayList(listString);
        //set up data for recyclerView
        setUpData();
    }

    private void setUpData() {

        for(int i=0; i<contactArrayList.size();i++){
            if(contactArrayList.get(i).isSelected()){
                contactArrayList.remove(i);
            }
        }

        if(contactArrayList!=null){
            contactAdapter = new ContactAdapter(getApplicationContext(),contactArrayList);
            recyclerViewContacts.setAdapter(contactAdapter);

            //sorting the contact list
            Collections.sort(contactArrayList, new Comparator<Contact>() {
                @Override
                public int compare(Contact a, Contact b) {
                    return a.getContactName().compareTo(b.getContactName());
                }
            });
            contactAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, "contactArrayList is null.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordVideo() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE);



        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){

                Toast.makeText(this, "Record video permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }



            }
        }




    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
       // mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }


    //this method converts arraylist into a String
    public ArrayList<Contact> getContactArrayList(String listString){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Contact> contactArrayList = gson.fromJson(listString, listOfObjects);
        return contactArrayList;
    }



    @Override
    public void onClick(View v) {

        if(v==textViewDone) {
            //saving the all the selections of the user
            if(!flag) {
                Log.d("!flag","Hell Yeah");
                saveSelectedContacts();//-->saveContactsonServer()-->/-->saveContactsArrayList()
                if(noItemSelected){
                    Log.d("noItemSelected","Hell Yeah");
                    Toast.makeText(this, "Please select at least one contact.", Toast.LENGTH_SHORT).show();
                }
            }else{
                saveSelectedContactsOnServer();
            }
        }

    }

    private void saveSelectedContactsOnServer() {
        Log.d("saveSelectedContactsOn","Hell Yeah");

        relativeLayoutProgress.setVisibility(View.VISIBLE);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.STORE_EXP_USERS_URL,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            relativeLayoutProgress.setVisibility(View.GONE);
                            Log.d("response",response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String message = jsonObject.getString("message");
                                String avatar = jsonObject.getString("avatar_url");
                                Log.d("avatar",avatar);
                                Toast.makeText(SelectContactActivity.this, message , Toast.LENGTH_SHORT).show();
                                //saving the avatar url
                             //   KeyValueDb.set(getApplicationContext(), Config.AVATAR_URL,avatar,1);
                                //saving the contact selection state
                                KeyValueDb.set(getApplicationContext(),Config.CONTACT_SELECTION_STATE,"1",1);


                                saveContactsArrayList();


                                //clearing all the previous activities
                                finishAffinity();
                                //making the transition to the next activity
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            relativeLayoutProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Bad Network Connection.", Toast.LENGTH_SHORT).show();
                        }
                    }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();

                    //fetching data to be stored
                    String listString  = KeyValueDb.get(getApplicationContext(), Config.SELECTED_CONTACTS_ARRAYLIST,"");
                    String username = KeyValueDb.get(getApplicationContext(), Config.USERNAME,"");
                    String fcmtoken = KeyValueDb.get(getApplicationContext(), Config.TAG_TOKEN,"");
                    Log.d("listString",listString);
                    params.put("exp_users",listString);
                    params.put("username",username);
                    params.put("fcmtoken", fcmtoken);
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

            MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void saveSelectedContacts() {
        Log.d("saveSelectedContacts","Hell Yeah");

        int size = contactAdapter.contactArrayListUncleared.size();

        for(int i=0;i<size;i++){


            Contact contact = contactAdapter.contactArrayListUncleared.get(i);

            if(contact.isSelected()){
                flag = true;
                noItemSelected = false;
                //setting the primary mobile number
                Log.d("selectedContact",contact.getContactName());
                String primary_mobile = KeyValueDb.get(SelectContactActivity.this,Config.PRIMARY_MOBILE,"");
                Log.d("primary_mobile", primary_mobile);
                contact.setPrimary_mobile(primary_mobile);
                //adding to the list
                selectedContactList.add(contact);
            }

        }
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        //saving the arraylist with repeatations
        String json = gson.toJson(selectedContactList,listOfObjects);
        KeyValueDb.set(getApplicationContext(),Config.SELECTED_CONTACTS_ARRAYLIST,json,1);

        if(flag) {
            //saving the contacts on the server
            saveSelectedContactsOnServer();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        //getPermissionToRecordVideo();
    }


    public void saveContactsArrayList(){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(contactAdapter.contactArrayListUncleared,listOfObjects);
        KeyValueDb.set(getApplicationContext(),Config.CONTACTS_ARRAYLIST,json,1);

    }



    @Override
    public void onBackPressed() {

        //if the progressbar is being shown
        if(relativeLayoutProgress.getVisibility() == View.VISIBLE){
            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        contactAdapter.filter(String.valueOf(s));

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
