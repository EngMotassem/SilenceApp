package techheromanish.example.com.silenceapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.drawer_fragments.ActionContactsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.ContactFetcher;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 100 ;
    EditText editTextUsername, editTextMobile, editTextEmail, editTextPassword;
    Button buttonSignUp, buttonSignin;
  public  static RelativeLayout relativeLayoutProgress;
    AwesomeValidation awesomeValidation;
    TextView textViewEnter;
    ContactFetcher contactFetcher;
    private int TRACE_CALLS_REQUEST_CODE = 200;
    CountryCodePicker countryCodePicker;
    private int RECORD_AUDIO_REQUEST_CODE = 2323;
    private boolean fetchingContactsisComplete = false;
    ProgressDialog progressDialog;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = getWindow().getDecorView().getHandler();
        //initializing awesomeValidation object
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        progressDialog = new ProgressDialog(this);

        //initializing Views
        initViews();

        //getting Permission to read Contacts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToReadUserContacts();
        }




    }

    private void initViews() {

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewEnter = (TextView) findViewById(R.id.textViewEnter);

        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSignin = (Button) findViewById(R.id.buttonSignIn);

        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);



        //setting OnClick listener to the buttons
        buttonSignin.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        relativeLayoutProgress = (RelativeLayout) findViewById(R.id.relativeProgressbar);


        //adding validations
        awesomeValidation.addValidation(MainActivity.this,R.id.editTextUsername,RegexTemplate.NOT_EMPTY,R.string.usernameerror);
        awesomeValidation.addValidation(MainActivity.this,R.id.editTextMobile,RegexTemplate.NOT_EMPTY,R.string.mobileerror);
        awesomeValidation.addValidation(MainActivity.this,R.id.editTextEmail, Patterns.EMAIL_ADDRESS,R.string.emailerror);
        awesomeValidation.addValidation(MainActivity.this,R.id.editTextPassword, RegexTemplate.NOT_EMPTY,R.string.passworderror);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        if(v==buttonSignUp){
            //if the button text is Sign up that means it's the state when user has not registered
            String buttonText = buttonSignUp.getText().toString();

            if(buttonText.charAt(0)=='S') {
                if (awesomeValidation.validate()) {

                        registerUser();
                }
            }
//            //if it's not sign up that means it's continue, i.e mail has been sent.
//            else if(buttonText.charAt(0)=='C'){
//                checkAccountStatus();
//            }
        }
        if(v==buttonSignin){
            //making the transition to the SignIn Activity
            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
        }

    }

//    private void checkAccountStatus() {
//        relativeLayoutProgress.setVisibility(View.VISIBLE);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.CHECK_ACCOUNT_STATUS_URL,
//
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        relativeLayoutProgress.setVisibility(View.GONE);
//                        Log.d("response",response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            boolean account_activated = jsonObject.getBoolean("account_activated");
//
//                            if(account_activated){
//
//                            }else{
//                                Toast.makeText(MainActivity.this, "Please verify your Email to continue", Toast.LENGTH_SHORT).show();
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        relativeLayoutProgress.setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }){
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new Hashtable<String, String>();
//
//                //fetching data to be stored
//                String username = editTextUsername.getText().toString();
//
//                params.put("username",username);
//
//
//                //returning parameters
//                return params;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Auth", Config.AUTH_KEY);
//                return headers;
//            }
//
//        };
//
//        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//    }




    private void registerUser() {
            relativeLayoutProgress.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.REGISTER_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayoutProgress.setVisibility(View.GONE);


                       // Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean user_registered = jsonObject.getBoolean("user_registered");

                            if(user_registered){
                                Toast.makeText(MainActivity.this,"This username is already in use.", Toast.LENGTH_SHORT).show();
                            }else{

                                boolean email_registered = jsonObject.getBoolean("email_registered");

                                //if the email is registered, then send it to sign in Activity
                                if(email_registered){
                                    Toast.makeText(MainActivity.this, "This mobile is already registered. Please Sign in.", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                }else{
                                        AfterRegisterAlertDialog afterRegisterAlertDialog = new AfterRegisterAlertDialog();
                                        afterRegisterAlertDialog.showDialog(MainActivity.this);
//                                    Toast.makeText(MainActivity.this,"Please check your Email" , Toast.LENGTH_SHORT).show();
//                                    textViewEnter.setText("Email Verificatiom mail has been sent. Please activate your account and then tap continue");
//                                    buttonSignUp.setText("Continue");

                                }



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
                String countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
                String mobile = countrycode.concat(editTextMobile.getText().toString());
                //saving the primary mobile number
                KeyValueDb.set(MainActivity.this,Config.PRIMARY_MOBILE,mobile,1);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String fcmtoken = KeyValueDb.get(MainActivity.this,Config.TAG_TOKEN,"");

                params.put("username",username);
                params.put("mobile",mobile);
                params.put("email",email);
                params.put("password",password);

                Log.d("fcmtoken",fcmtoken);

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
        MyVolley.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }


    //Alert Dialog to ascertain whether user wants the an avatar for his/her account or not
    public class AfterRegisterAlertDialog {

        Dialog dialog ;
        public void showDialog(Activity activity){
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.after_register_alert_dialog_layout);


            //initializing UI elements
            Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
            Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo);


            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //saving certain details
                    saveloginData();
                    //clearing the previous activities
                    finishAffinity();
                    //making the transition to the next activity
                    startActivity(new Intent(getApplicationContext(),SelectAvatarActivity.class));

                    dialog.dismiss();
                }
            });

            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //saving certain details
                    saveloginData();
                    //saving that avatar has been selected
                    KeyValueDb.set(getApplicationContext(), Config.AVATAR_SELECTION_STATE,"1",1);
                    //initializing Contacts
                    initializePhoneContacts();
                    dialog.dismiss();
                }
            });





            dialog.show();

        }

    }

    public void initializePhoneContacts(){

        progressDialog.setMessage("Initializing data...Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //reading and saving the user accounts
                contactFetcher = new ContactFetcher(getApplicationContext());
                fetchingContactsisComplete = contactFetcher.initphoneContacts();
                KeyValueDb.set(getApplicationContext(), Config.FETCHING_CONTACTS_DONE,"1",1);

                if(fetchingContactsisComplete){
                    progressDialog.dismiss();

                    finishAffinity();
                    //making the transition to the next activity
                    startActivity(new Intent(getApplicationContext(), SelectContactActivity.class));

                }

            }
        };
        new Thread(runnable).start();
    }



    public void saveloginData(){

        //saving the login state
        KeyValueDb.set(MainActivity.this,Config.LOGIN_STATE,"1",1);

        String countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
        String mobile = countrycode.concat(editTextMobile.getText().toString());
        //saving the primary mobile number
        KeyValueDb.set(MainActivity.this,Config.PRIMARY_MOBILE,mobile,1);

        //saving user's country code
        KeyValueDb.set(getApplicationContext(), Config.USER_COUNTRY_CODE, countryCodePicker.getSelectedCountryCodeWithPlus(),1);
        //saving the username
        KeyValueDb.set(getApplicationContext(),Config.USERNAME,editTextUsername.getText().toString(),1);
        //clearing all the previous activities

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_CONTACTS_PERMISSIONS_REQUEST);



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
     //   getPermissionToReadUserContacts();
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){


            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();

            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        if(relativeLayoutProgress.getVisibility() == View.VISIBLE){
            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }
}
