package techheromanish.example.com.silenceapp.drawer_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.activities.HomeActivity;
import techheromanish.example.com.silenceapp.activities.SelectContactActivity;
import techheromanish.example.com.silenceapp.adapters.ContactAdapter;
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

public class AddExceptionFragment extends Fragment implements View.OnClickListener, TextWatcher {

    RecyclerView recyclerViewContacts;
    LinearLayoutManager linearLayoutManager;
    ContactAdapter contactAdapter;
    ArrayList<Contact> contactArrayList;
    ArrayList<Contact> toshowcontactArrayList;
    ArrayList<Contact> selectedContactsList, offlineSelectedContactlist;
    public static RelativeLayout relativeLayoutProgress;
    Toolbar toolbar;
    ImageView imageViewBack;
    TextView textViewDone;
    EditText editTextSearch;
    View view;
    boolean flag = false, noItemSelected = true;
    //to show only the unselected contacts
    private ArrayList<Contact> contactArrayList2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_exception,container,false);
        this.view  = view;

        contactArrayList2 = new ArrayList<Contact>();

        //initializing Views
        initViews(view);

        return view;
    }



    private void initViews(View view) {

        //initlizing new ArrayList
        selectedContactsList = new ArrayList<Contact>();
        toshowcontactArrayList = new ArrayList<Contact>();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //init toolbar items
        imageViewBack = (ImageView) toolbar.findViewById(R.id.imageViewBack);
        textViewDone = (TextView) toolbar.findViewById(R.id.textViewDone);
        editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
        //setting onCLick listener
        imageViewBack.setOnClickListener(this);
        textViewDone.setOnClickListener(this);

        //hiding the previous toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //setting the new toolbar with a blank title
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //adding OnTextChanged listener to the editText
        editTextSearch.addTextChangedListener(this);


        relativeLayoutProgress = (RelativeLayout) view.findViewById(R.id.relativeProgressbar);
        recyclerViewContacts = (RecyclerView) view.findViewById(R.id.recyclerViewContacts);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //setting layout manager to the recyclerView
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        //getting the contacts Arraylist
        contactArrayList = getContactArrayList();
        //getting the not selected contacts only
        setUpData();

    }


    private void setUpData() {

        for(int i=0; i<contactArrayList.size();i++){
            if(contactArrayList.get(i).isSelected()){
                Log.d("selected",contactArrayList.get(i).getContactName());
            }else{
                contactArrayList2.add(contactArrayList.get(i));
            }
        }

        contactAdapter = new ContactAdapter(getActivity(),contactArrayList2);
        recyclerViewContacts.setAdapter(contactAdapter);

        //sorting the contact list
        Collections.sort(contactArrayList2, new Comparator<Contact>() {
            @Override
            public int compare(Contact a, Contact b) {
                return a.getContactName().compareTo(b.getContactName());
            }
        });
        contactAdapter.notifyDataSetChanged();
    }

    //this method converts arraylist into a String
    public ArrayList<Contact> getContactArrayList(){

        String listString = KeyValueDb.get(getActivity(), Config.CONTACTS_ARRAYLIST,"");
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(listString, listOfObjects);
    }


    private void saveSelectedContactsOnServer() {

        relativeLayoutProgress.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.ADD_EXP_USERS_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayoutProgress.setVisibility(View.GONE);
                        Log.d("response",response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message , Toast.LENGTH_SHORT).show();

                            saveOfflineSelectedList();
                            saveUnSelectedContactsList();
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
                String listString = gson.toJson(selectedContactsList, listOfObjects);

                Log.d("listString",listString);
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

    private void saveSelectedContacts() {


        //adding it to the current offline list in HomeFragment
        offlineSelectedContactlist = getofflinesSelectdContactArrayList();

        int size = contactAdapter.contactArrayListUncleared.size();

        for(int i=0;i< size;i++){

            Contact contact = contactAdapter.contactArrayListUncleared.get(i);

            if(contact.isSelected()){

                Log.d("savingselected",contact.getContactName());
                //setting flag true would confirm at least one item has been selected
                flag = true;
                //setting flag true would confirm at least one item has been selected
                noItemSelected = false;
                //setting the primary mobile number
                contact.setPrimary_mobile(KeyValueDb.get(getActivity(),Config.PRIMARY_MOBILE,""));
                //adding to the list to store it on the web without any duplication of data
                selectedContactsList.add(contact);
                //adding the contacts to the offline list
                offlineSelectedContactlist.add(contact);
            }

        }

        if(!noItemSelected) {
            saveSelectedContactsOnServer();
        }
    }

    private void saveOfflineSelectedList() {
        //saving the data for the seleceted ones for offline usage
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(offlineSelectedContactlist,listOfObjects);
        KeyValueDb.set(getActivity(),Config.SELECTED_CONTACTS_ARRAYLIST,json,1);

    }

    private void saveUnSelectedContactsList() {
        //saving the data for the seleceted ones for offline usage
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(contactAdapter.contactArrayListUncleared,listOfObjects);
        KeyValueDb.set(getActivity(),Config.CONTACTS_ARRAYLIST,json,1);

    }

    //this method converts arraylist into a String
    public ArrayList<Contact> getofflinesSelectdContactArrayList(){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String listString = KeyValueDb.get(getActivity(),Config.SELECTED_CONTACTS_ARRAYLIST,"");
        return gson.fromJson(listString, listOfObjects);
    }


    @Override
    public void onClick(View v) {

        if(v==imageViewBack){
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment());
            ft.commit();
        }
        if(v==textViewDone){

                //i.e. if once the data has been updated offline but could not updated online
                if(!flag){
                    saveSelectedContacts();
                    if(noItemSelected){
                        Toast.makeText(getActivity(), "Please select at least one Contact", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    saveSelectedContactsOnServer();
                }

        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        contactAdapter.filter(String.valueOf(s));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
