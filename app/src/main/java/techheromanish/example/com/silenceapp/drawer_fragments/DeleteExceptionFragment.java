package techheromanish.example.com.silenceapp.drawer_fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.adapters.DeleteSelectedContactAdapter;
import techheromanish.example.com.silenceapp.adapters.SelectedContactAdapter;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.helper.MyVolley;
import techheromanish.example.com.silenceapp.helper.URL;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

import static techheromanish.example.com.silenceapp.activities.HomeActivity.navigationView;

/**
 * Created by Manish on 3/26/2017.
 */

public class DeleteExceptionFragment extends Fragment implements View.OnClickListener {

    public static ArrayList<Contact> selectContactList;
    public static ArrayList<Contact> freshselectedContactList;
    public static ArrayList<Contact> tobeDeletedContactList;
    public static ArrayList<Contact> mainContactList;
    DeleteSelectedContactAdapter contactAdapter;
    Toolbar toolbar;
    ListView listViewContacts;
    ImageView imageViewBack;
    TextView textViewDone;
    private boolean flag = false, noItemSelected = true;
    public static RelativeLayout relativeLayoutProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_exception,container,false);
        //arraylist initializations
        freshselectedContactList = new ArrayList<Contact>();
        tobeDeletedContactList = new ArrayList<Contact>();
        //progressbar initializations
        relativeLayoutProgress = (RelativeLayout) view.findViewById(R.id.relativeProgressbar);

        //initializing Views
        initViews(view);

        return view;
    }

    private void initViews(View view) {

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //initializing Toolbar elements
        imageViewBack = (ImageView) view.findViewById(R.id.imageViewBack);
        textViewDone = (TextView) view.findViewById(R.id.textViewDone);
        imageViewBack.setOnClickListener(this);
        textViewDone.setOnClickListener(this);

        //hiding the previous toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //setting the new toolbar with a blank title
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        listViewContacts = (ListView) view.findViewById(R.id.listViewSelectedContacts);
        selectContactList = new ArrayList<Contact>();



        String listString = KeyValueDb.get(getActivity(), Config.SELECTED_CONTACTS_ARRAYLIST,"");


        selectContactList = getContactArrayList(listString);

        if(selectContactList!=null){
            contactAdapter = new DeleteSelectedContactAdapter(getActivity(), selectContactList);
            listViewContacts.setAdapter(contactAdapter);
        }else{
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

        if (v == imageViewBack) {
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment());
            ft.commit();
        }
        if(v== textViewDone){
            Log.d("OnClicktextViewDone","Hell Yeah!");
            if(!flag) {
                saveChangesoffline();
                if(noItemSelected){
                    Toast.makeText(getActivity(), "Please select at least one contact.", Toast.LENGTH_SHORT).show();
                }
            }else{
                saveChanesOnline();
            }
        }


    }

    //this includes-->1.overwriting the selectedList with a fresh contactList, creating a fresh to be deletedList
    //             -->2.adding the tobeDeletedObjects to the main Contacts Arraylist
    //             -->3. sending the deleteList to the server and thereby making the changes on the db there
    private void saveChangesoffline() {

        Log.d("gotintosavechangesoff","Hell Yeah!");
        for(int i=0;i<selectContactList.size();i++){

            if(selectContactList.get(i).isToBedeleted()){
                flag = true;
                noItemSelected = false;
             tobeDeletedContactList.add(selectContactList.get(i));
            }else{
                freshselectedContactList.add(selectContactList.get(i));
            }

        }

        if(flag){
            saveChanesOnline();
        }

    }

    public void updateSelectedContactList(){

        //saving the data for the seleceted ones for offline usage
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(freshselectedContactList,listOfObjects);
        KeyValueDb.set(getActivity(),Config.SELECTED_CONTACTS_ARRAYLIST,json,1);
    }

    public void updateMainContactList(){
        mainContactList = getoMainContactArrayList();
        if(tobeDeletedContactList!=null){

            for(int i=0;i<tobeDeletedContactList.size();i++){

                //marking the object unselected so that it shows up in the add users list
                tobeDeletedContactList.get(i).setSelected(false);
                tobeDeletedContactList.get(i).setCamera(false);
                tobeDeletedContactList.get(i).setSilenceexception(false);
                mainContactList.add(tobeDeletedContactList.get(i));
                Log.d("addedcontact",tobeDeletedContactList.get(i).getContactName());
            }
        }
        //saving the data for the seleceted ones for offline usage
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(mainContactList,listOfObjects);
        KeyValueDb.set(getActivity(),Config.CONTACTS_ARRAYLIST,json,1);


    }

    //this method converts arraylist into a String
    public ArrayList<Contact> getoMainContactArrayList(){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String listString = KeyValueDb.get(getActivity(),Config.CONTACTS_ARRAYLIST,"");
        return gson.fromJson(listString, listOfObjects);
    }

    private void saveChanesOnline() {

        Log.d("gotintosaveChangesOnli","Hell Yeah!");
       relativeLayoutProgress.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.DELETE_EXP_USERS_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayoutProgress.setVisibility(View.GONE);
                        Log.d("response",response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message , Toast.LENGTH_SHORT).show();

                            //executing the offline changes
                            updateMainContactList();
                            updateSelectedContactList();
                            //making the transition to the HomeFragment
                            navigationView.getMenu().getItem(0).setChecked(true);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, new HomeFragment());
                            ft.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        relativeLayoutProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Bad Network Connection.", Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();

                //fetching data to be stored
                Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
                Gson gson = new Gson();
                String listString = gson.toJson(tobeDeletedContactList, listOfObjects);

                Log.d("deletelistString",listString);
                params.put("exp_users",listString);
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

        MyVolley.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }
}

