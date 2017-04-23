package techheromanish.example.com.silenceapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
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
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.ContactFetcher;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUsername, editTextPassword;
    Button buttonSignIn;
    AwesomeValidation awesomeValidation;
    RelativeLayout relativeLayoutProgress;
    String primary_number;
    ArrayList<Contact> selectedContactList;
    boolean fetchingContactsisComplete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //initializing arraylist
        selectedContactList = new ArrayList<Contact>();

        //init Views
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        relativeLayoutProgress = (RelativeLayout) findViewById(R.id.relativeProgressbar);

        //initializing awesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //adding validations
        awesomeValidation.addValidation(this,R.id.editTextUsername, RegexTemplate.NOT_EMPTY,R.string.usernameerror);
        awesomeValidation.addValidation(this,R.id.editTextPassword, RegexTemplate.NOT_EMPTY,R.string.passworderror);

        //setting OnClick listener to the buttonSignIn
        buttonSignIn.setOnClickListener(this);

        if(KeyValueDb.get(this, Config.FETCHING_CONTACTS_DONE,"0").equals("0")){
            //initializing Contacts
            initializePhoneContacts();
        }else{
            fetchingContactsisComplete = true;
        }




    }

    @Override
    public void onClick(View v) {

        //hiding the Keyboard on Click.
        hideSoftKeyboard(SignInActivity.this);

        if(awesomeValidation.validate()){

            if(fetchingContactsisComplete) {
                loginUser();
            }else{
                Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initializePhoneContacts(){


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //reading and saving the user accounts
                ContactFetcher contactFetcher = new ContactFetcher(getApplicationContext());
                fetchingContactsisComplete = contactFetcher.initphoneContacts();
                KeyValueDb.set(getApplicationContext(), Config.FETCHING_CONTACTS_DONE,"1",1);

            }
        };
        new Thread(runnable).start();

    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void loginUser() {
        relativeLayoutProgress.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.LOGIN_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean user_exists = jsonObject.getBoolean("user_exists");

                            if(user_exists){
                                    getPrimaryNumberAndUpdatetoken();

                            }else{
                                relativeLayoutProgress.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();

                //fetching data to be stored
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                params.put("username",username);
                params.put("password",password);


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

    private void getPrimaryNumberAndUpdatetoken() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.GET_PRIMARY_NUMBER_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayoutProgress.setVisibility(View.GONE);
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                             primary_number = jsonObject.getString("primary_number");
                            String avatar_url = jsonObject.getString("avatar_url");


                            Log.d("avatar_url",avatar_url);
                            KeyValueDb.set(getApplicationContext(), Config.AVATAR_URL,avatar_url,1);
                            KeyValueDb.set(getApplicationContext(),Config.PRIMARY_MOBILE,primary_number,1);


                            //saving the username
                            KeyValueDb.set(getApplicationContext(),Config.USERNAME,editTextUsername.getText().toString(),1);

                                    //saving the login state
                                    KeyValueDb.set(SignInActivity.this,Config.LOGIN_STATE,"1",1);
                                    //saving that signin was used
                                    KeyValueDb.set(getApplicationContext(), Config.CAME_THROUGH_SIGNIN, "1",1);
                                    //clearing all the previous activities
                                    finishAffinity();
                                    //making the transition to the next activity
                                    int select_contact_state = 0;
                                    select_contact_state = Integer.parseInt(KeyValueDb.get(getApplicationContext(), Config.CONTACT_SELECTION_STATE,"0"));
                                    if(select_contact_state==0){
                                        startActivity(new Intent(getApplicationContext(),SelectContactActivity.class));
                                    }else{
                                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
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
                        Toast.makeText(getApplicationContext(), "Bad Network Connection.", Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();

                //fetching data to be stored
                String username = editTextUsername.getText().toString();
                String token = KeyValueDb.get(getApplicationContext(), Config.TAG_TOKEN,"");
                Log.d("tokeninsignin",token);
                params.put("username",username);
                params.put("fcmtoken",token);
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

    @Override
    public void onBackPressed() {

        //if the progressbar is being shown
        if(relativeLayoutProgress.getVisibility() == View.VISIBLE){

            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else if(!fetchingContactsisComplete){
            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }
}
