package techheromanish.example.com.silenceapp.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikhaellopez.circularimageview.CircularImageView;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.drawer_fragments.ActionContactsFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.AddExceptionFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.AddReminderFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.CallFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.DeleteExceptionFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.HomeFragment;
import techheromanish.example.com.silenceapp.drawer_fragments.SmsFragment;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.sinch.BaseActivity;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
   public static NavigationView navigationView;
    public  static   DrawerLayout drawer;
    TextView textViewUsername;
    CircularImageView circularImageView;
    private int TRACE_CALLS_REQUEST_CODE = 100;
    private int RECORD_AUDIO_REQUEST_CODE = 457;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordVideo();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


       navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setting the navigation item background
        navigationView.setItemBackground(getResources().getDrawable(android.R.drawable.screen_background_light_transparent));
//        //setting navigation view background
//        navigationView.setBackgroundColor(getResources().getColor(R.color.gray));
        //configuring the navigation header
        configureHeader();

        //displaying home fragment by default
        displaySelectedScreen(R.id.nav_home);
        navigationView.getMenu().getItem(0).setChecked(true);

        //starting the service to detect the video calls
//        String username = KeyValueDb.get(getApplicationContext(), Config.PRIMARY_MOBILE, "");
//        SinchService service = new SinchService(this);
//        service.start(username);

        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Toast.makeText(HomeActivity.this, "onBackStackCalled", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void configureHeader() {

        View view = navigationView.getHeaderView(0);
        textViewUsername = (TextView) view.findViewById(R.id.textViewUsername);
        circularImageView = (CircularImageView) view.findViewById(R.id.circularImageView);

        String username = KeyValueDb.get(getApplicationContext(), Config.USERNAME,"");
        String avatar = KeyValueDb.get(getApplicationContext(), Config.AVATAR_URL,"");
        Log.d("avatar",avatar);
        if(!avatar.isEmpty()){

            Glide.with(HomeActivity.this)
                    .load(avatar)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            circularImageView.setImageBitmap(resource);
                        }
                    });

        }
        textViewUsername.setText(username);
    }


    @Override
    public void onBackPressed() {




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(navigationView.getMenu().getItem(1).isChecked() ||
                navigationView.getMenu().getItem(2).isChecked() ||
                navigationView.getMenu().getItem(3).isChecked()
                ){
            gotoHomeFragment();

        } else if(navigationView.getMenu().getItem(4).isChecked()) {


            if (AddExceptionFragment.relativeLayoutProgress.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
            }else{
                gotoHomeFragment();
            }
        }

        else if(navigationView.getMenu().getItem(5).isChecked()){

            if(DeleteExceptionFragment.relativeLayoutProgress.getVisibility() == View.VISIBLE){

                Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
            }else{
                gotoHomeFragment();
            }
        }else if(navigationView.getMenu().getItem(6).isChecked()){

            if(ActionContactsFragment.relativeLayoutProgress.getVisibility() == View.VISIBLE){
                Toast.makeText(this, "Please wait for a while.", Toast.LENGTH_SHORT).show();
            }else{
                gotoHomeFragment();
            }
        }
        else {

            super.onBackPressed();
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

    public void gotoHomeFragment(){
        navigationView.getMenu().getItem(0).setChecked(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,new HomeFragment());
        ft.commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedScreen(int id) {


        Fragment fragment = null;

        switch (id){

            case R.id.nav_home:
                    fragment = new HomeFragment();
                    break;
            case R.id.nav_reminder:
                    fragment = new AddReminderFragment();
                    break;
            case R.id.nav_call_schedule:
                    fragment = new CallFragment();
                    break;
            case R.id.nav_sms_schedule:
                    fragment = new SmsFragment();
                    break;

            case R.id.nav_add_exception:
                    fragment = new AddExceptionFragment();
                    break;
            case R.id.nav_delete_exception:
                    fragment = new DeleteExceptionFragment();
                    break;
            case R.id.nav_action_contacts:
                fragment = new ActionContactsFragment();
                break;
        }


        if(fragment!=null){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();

        }

    }


    }



