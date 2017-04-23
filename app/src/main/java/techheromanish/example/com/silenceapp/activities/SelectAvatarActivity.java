package techheromanish.example.com.silenceapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.ContactFetcher;
import techheromanish.example.com.silenceapp.helper.ImageFilePath;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.RequestHandler;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

import static android.R.attr.bitmap;
import static android.R.attr.data;

public class SelectAvatarActivity extends AppCompatActivity implements View.OnClickListener {

    CircularImageView circularImageView;
    Button buttonSetmeup;
    private Uri mCropImageUri;
    boolean fetchingContactsisComplete = false;
    boolean noImageSelected = true;
    RelativeLayout relativeLayoutProgress;
    String imageBase64;
    Bitmap bitmapAvatar;
    private int PICK_IMAGE_REQUEST = 123;
    Intent resultData;
    private Uri filePathUri;
    ContactFetcher contactFetcher;
    private int ACCESS_STORAGE_PERMISSIONS_REQUEST = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);

        if(KeyValueDb.get(this, Config.FETCHING_CONTACTS_DONE,"0").equals("0")){
            //initializing Contacts
            initializePhoneContacts();
        }else{
            fetchingContactsisComplete = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToAccessStorage();
        }

        //initializing Views
        initViews();

    }

    public void initializePhoneContacts(){


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //reading and saving the user accounts
                contactFetcher = new ContactFetcher(getApplicationContext());
                fetchingContactsisComplete = contactFetcher.initphoneContacts();
                KeyValueDb.set(getApplicationContext(), Config.FETCHING_CONTACTS_DONE,"1",1);

            }
        };
        new Thread(runnable).start();

    }

    private void initViews() {

        circularImageView = (CircularImageView) findViewById(R.id.circularImageView);
        buttonSetmeup = (Button) findViewById(R.id.buttonSetMeUp);
        relativeLayoutProgress = (RelativeLayout) findViewById(R.id.relativeProgressbar);
        //setting OnClick listeners
        circularImageView.setOnClickListener(this);
        buttonSetmeup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v==circularImageView){


            showFileChooser();
        }

        if(v==buttonSetmeup){

            if(noImageSelected){
                Toast.makeText(this, "Please select an avatar before moving on.", Toast.LENGTH_SHORT).show();
            }else{
                //setting progressbar visible
                relativeLayoutProgress.setVisibility(View.VISIBLE);
                //this method will upload the avatar to the server
                uploadImage();
            }


        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToAccessStorage() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                    ACCESS_STORAGE_PERMISSIONS_REQUEST);


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
        if (requestCode == ACCESS_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED ){

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
        //if the progressbar is being shown
        if(relativeLayoutProgress.getVisibility() == View.VISIBLE){
            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else if(!fetchingContactsisComplete){
            Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }

    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

             filePathUri = data.getData();
            noImageSelected = false;
            resultData = data;
            try {
                bitmapAvatar = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            circularImageView.setImageBitmap(bitmapAvatar);


        }
    }




    public void uploadImage(){

        relativeLayoutProgress.setVisibility(View.VISIBLE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String filePath = ImageFilePath.getPath(getApplicationContext(), filePathUri);
                File f  = new File(filePath);
                String content_type  = getMimeType(f.getPath());


                String file_path = f.getAbsolutePath();
                Log.d("imagepath",file_path);
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);



                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name",KeyValueDb.get(getApplicationContext(), Config.USERNAME,""))
                        .addFormDataPart("image",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                        .build();

                Log.d("username",KeyValueDb.get(getApplicationContext(), Config.USERNAME,""));
                Request request = new Request.Builder()
                        .url(URL.UPLOAD_AVATAR_URL)
                        .method("POST",RequestBody.create(null, new byte[0]))
                        .post(request_body)

                        .build();

                try {
                    final Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                relativeLayoutProgress.setVisibility(View.GONE);
                                Toast.makeText(SelectAvatarActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else{

                        String jsonData = response.body().string();
                        Log.d("jsonData",jsonData);
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String name = jsonObject.getString("name");
                        String avatar  = jsonObject.getString("avatar_url");

                        //saving the url
                        KeyValueDb.set(getApplicationContext(), Config.AVATAR_URL,avatar,1);
                        //saving that avatar has been selected
                        KeyValueDb.set(getApplicationContext(), Config.AVATAR_SELECTION_STATE,"1",1);
                        //clearing previous activities
                        finishAffinity();
                        //making transition to the next activity
                        startActivity(new Intent(getApplicationContext(), SelectContactActivity.class));

                        Log.d("responsename",name);
                        Log.d("reponseavatar",avatar);

                        //  progress.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("response",response.toString());
                                Toast.makeText(SelectAvatarActivity.this, "Upload Successful" , Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    //  progress.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            relativeLayoutProgress.setVisibility(View.GONE);

                        }
                    });

                } catch (final IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            relativeLayoutProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Bad Network Connection.", Toast.LENGTH_SHORT).show();

                        }
                    });
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();
    }



    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
