package techheromanish.example.com.silenceapp.sinch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.sinch.android.rtc.SinchError;
import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.ActionContactAdapter;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    Handler handler;
    String primary_mobile = "";
    int move_ahead = 0;
    //to place a request less time is being alloted
    int wait_time = 1500;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Intent intent = getIntent();
        //this end user's mobile number
        String mobile = KeyValueDb.get(getApplicationContext(), Config.PRIMARY_MOBILE,"");
        //other end user's mobile number
        String primary_mobile = intent.getStringExtra("primary_mobile");

        //it means the request has been recieved to place the call, hence the primary_mobileis null which was to be recievd from the LoginActivity
        if(primary_mobile == null){
            move_ahead = 1;
            wait_time = 3000;
           // Toast.makeText(this, "primary_mobile is null", Toast.LENGTH_SHORT).show();
        }else{
            //sending request
            ActionContactAdapter actionContactAdapter = new ActionContactAdapter(LoginActivity.this);
            actionContactAdapter.executeRequest(mobile,primary_mobile,0);
            Log.d("this end phone",mobile);
            Log.d("other end phone", primary_mobile);
        }




        handler = new Handler();


        mLoginName = (EditText) findViewById(R.id.loginName);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);

        showSpinner();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                loginClicked();
            }
        };

        handler.postDelayed(runnable,wait_time);


    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() {

        String username = KeyValueDb.get(getApplicationContext(), Config.PRIMARY_MOBILE, "");
     //   Toast.makeText(this, username , Toast.LENGTH_SHORT).show();
        Log.d("primary_mobile", username);

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(username);
        } else {
            openPlaceCallActivity();
        }
    }



    private void openPlaceCallActivity() {

        mSpinner.dismiss();

        Intent i = getIntent();
        String mobile = i.getStringExtra("mobile");
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        mainActivity.putExtra("mobile", mobile);
        mainActivity.putExtra("move_ahead", move_ahead);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Loading");
        mSpinner.setCancelable(false);
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
