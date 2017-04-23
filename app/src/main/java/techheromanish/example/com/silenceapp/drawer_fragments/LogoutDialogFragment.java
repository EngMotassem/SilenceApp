package techheromanish.example.com.silenceapp.drawer_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.activities.HomeActivity;
import techheromanish.example.com.silenceapp.activities.MainActivity;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;


/**
 * Created by Manish on 2/11/2017.
 */

public class LogoutDialogFragment extends DialogFragment {

    Dialog dialog;
    Activity activity = getActivity();
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.logout_alert_dialog_layout, container, false);
        dialog = getDialog();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //initializing UI elements
        TextView textViewLogout = (TextView) rootView.findViewById(R.id.textViewLogout);
        TextView textViewSure = (TextView) rootView.findViewById(R.id.textViewSure);
        Button buttonYes = (Button) rootView.findViewById(R.id.buttonYes);
        Button buttonNo = (Button) rootView.findViewById(R.id.buttonNo);

//        //setting fonts to UI elements
//        Typeface typefaceBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ProximaNovaSoft-Bold.otf");
//        Typeface typefaceSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaSoft-Semibold.otf");
//        textViewLogout.setTypeface(typefaceBold);
//        textViewSure.setTypeface(typefaceSemiBold);
//        buttonYes.setTypeface(typefaceBold);
//        buttonNo.setTypeface(typefaceBold);

        //setting OnClick listeners to the buttons
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                //marking the home item checked after dismissal of dialog box
                HomeActivity.navigationView.getMenu().getItem(0).setChecked(true);


            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //making transition to the next activity
                getActivity().finish();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                //saving the login state
                KeyValueDb.set(getActivity(), Config.LOGIN_STATE,"0",1);

                dialog.dismiss();



            }
        });


        return rootView;
    }


}
