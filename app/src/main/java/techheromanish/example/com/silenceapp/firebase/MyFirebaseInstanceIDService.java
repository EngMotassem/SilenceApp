package techheromanish.example.com.silenceapp.firebase;

/**
 * Created by Manish on 11/22/2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;


/**
 * Created by Belal on 03/11/16.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static String refreshedToken;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d("FirebaseToken", "Refreshed token: " + refreshedToken);
        //calling the method store token and passing token
        storeToken(refreshedToken);

    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
        KeyValueDb.set(getApplicationContext(), Config.TAG_TOKEN, token, 1);

    }
}