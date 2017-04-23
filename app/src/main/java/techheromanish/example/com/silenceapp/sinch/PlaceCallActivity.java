package techheromanish.example.com.silenceapp.sinch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

import techheromanish.example.com.silenceapp.R;

public class PlaceCallActivity extends BaseActivity {

    private Button mCallButton;
    private EditText mCallName;
    Handler handler;
    private ProgressDialog mSpinner;
    LinearLayout linearLayoutProgress;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},100);

        mCallName = (EditText) findViewById(R.id.callName);
        mCallButton = (Button) findViewById(R.id.callButton);
        linearLayoutProgress = (LinearLayout) findViewById(R.id.linearLayoutProgress);
        mCallButton.setEnabled(false);


        showSpinner();

        handler = new Handler();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                callButtonClicked();
            }
        };

        Intent i = getIntent();
        int move_ahead = i.getIntExtra("move_ahead",0);

        if(move_ahead==1) {
            handler.postDelayed(runnable, 3000);
        }else{
            mSpinner.dismiss();

        }

    }

    @Override
    protected void onServiceConnected() {
        TextView userName = (TextView) findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());
        mCallButton.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        Intent i = getIntent();
        String userName = i.getStringExtra("mobile");
        Log.d("mobile", userName);
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUserVideo(userName);
        String callId = call.getCallId();

        mSpinner.dismiss();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("came_through_placecall",1);
        startActivity(callScreen);
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Setting things Up");
        mSpinner.setCancelable(false);
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
