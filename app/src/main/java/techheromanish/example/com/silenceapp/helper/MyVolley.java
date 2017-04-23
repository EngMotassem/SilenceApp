package techheromanish.example.com.silenceapp.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Manish on 11/24/2016.
 */

public class MyVolley {

    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;


    public MyVolley(Context context) {

        this.mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context) {


        if (mInstance == null) {
            mInstance = new MyVolley(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {

            //
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);

    }
}
