package techheromanish.example.com.silenceapp.drawer_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.activities.HomeActivity;
import techheromanish.example.com.silenceapp.adapters.ContactAdapter;
import techheromanish.example.com.silenceapp.adapters.SelectedContactAdapter;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 3/26/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

   public static ArrayList<Contact> contactArrayList;
   public static ArrayList<Contact> contactArrayList2;
    public TextView textViewNoContacts;

    public  static SelectedContactAdapter selectedContactAdapter;
    Toolbar toolbar;
    ListView listViewContacts;
    ImageView imageViewDash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //initializing Toolbar elements
        imageViewDash = (ImageView) view.findViewById(R.id.imageViewDash);
        textViewNoContacts = (TextView) view.findViewById(R.id.textViewNoContacts);
        imageViewDash.setOnClickListener(this);

        //hiding the previous toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //setting the new toolbar with a blank title
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        listViewContacts = (ListView) view.findViewById(R.id.listViewSelectedContacts);
        contactArrayList = new ArrayList<Contact>();



        String listString = KeyValueDb.get(getActivity(), Config.SELECTED_CONTACTS_ARRAYLIST,"");


        contactArrayList = getContactArrayList(listString);

        if(contactArrayList!=null){
            selectedContactAdapter = new SelectedContactAdapter(getActivity(),contactArrayList);
            listViewContacts.setAdapter(selectedContactAdapter);

            if(contactArrayList.size() == 0){
                listViewContacts.setVisibility(View.GONE);
                textViewNoContacts.setVisibility(View.VISIBLE);
            }
            if(contactArrayList.size() > 0){

                listViewContacts.setVisibility(View.VISIBLE);
                textViewNoContacts.setVisibility(View.GONE);

            }
        } else{
            Toast.makeText(getActivity(), "contactArrayList is null.", Toast.LENGTH_SHORT).show();
        }

    }

    //this method converts arraylist into a String
    public ArrayList<Contact> getContactArrayList(String listString){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);
    }

    @Override
    public void onClick(View v) {

        if(v==imageViewDash){
            HomeActivity.drawer.openDrawer(Gravity.LEFT);
        }

    }
}
