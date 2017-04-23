package techheromanish.example.com.silenceapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.ContactFetcher;
import techheromanish.example.com.silenceapp.helper.FontManager;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

public class SplashActivity extends AppCompatActivity {

    TextView  textViewAppname;
    ImageView imageViewMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //initializing UI elements
        textViewAppname = (TextView) findViewById(R.id.textViewAppname);
        imageViewMobile = (ImageView) findViewById(R.id.imageViewMobile);

        //importing fonts
        Typeface typefaceBold = FontManager.getInstance(this).typefaceBold;
        textViewAppname.setTypeface(typefaceBold);

        startAnimations();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //fading the imageView on the transition
                Animation anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alphainverse);
                anim.reset();
                imageViewMobile.clearAnimation();
                imageViewMobile.startAnimation(anim);

                Intent intent = null;

                int login_state = Integer.parseInt(KeyValueDb.get(SplashActivity.this, Config.LOGIN_STATE,"0"));
                int avatar_state = Integer.parseInt(KeyValueDb.get(SplashActivity.this, Config.AVATAR_SELECTION_STATE,"0"));
                int select_contact_state = Integer.parseInt(KeyValueDb.get(SplashActivity.this, Config.CONTACT_SELECTION_STATE,"0"));
                int came_through_signin = Integer.parseInt(KeyValueDb.get(getApplicationContext(), Config.CAME_THROUGH_SIGNIN,"0"));
                //if login is complete
                if(login_state==1) {

                    if(came_through_signin==0){
                    //if avatar selection is complete
                    if(avatar_state==1) {
                        //if contacts have also been selected
                        if (select_contact_state == 1) {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                        }
                        //if contacts have not been selected
                        else {
                            intent = new Intent(SplashActivity.this, SelectContactActivity.class);
                        }
                    }else{
                        intent = new Intent(SplashActivity.this, SelectAvatarActivity.class);
                        }
                    }else{
                        //if contacts have also been selected
                        if (select_contact_state == 1) {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                        }
                        //if contacts have not been selected
                        else {
                            intent = new Intent(SplashActivity.this, SelectContactActivity.class);
                        }
                    }
                }
                //if login is not done
                else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                SplashActivity.this.finish();
            }
        };


        new android.os.Handler().postDelayed(runnable,3000);


    }





    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        textViewAppname.clearAnimation();
        textViewAppname.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        imageViewMobile.clearAnimation();
        imageViewMobile.startAnimation(anim);

    }



}
