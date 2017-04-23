package techheromanish.example.com.silenceapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import techheromanish.example.com.silenceapp.R;
import techheromanish.example.com.silenceapp.helper.Config;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;

/**
 * Created by Manish on 3/26/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Context context;
    ArrayList<Contact> contactArrayList;
    public ViewHolder holder;

    private String previous_number = "";
    boolean flag = false;
    private SparseBooleanArray mCheckedItems = new SparseBooleanArray();
   public static ArrayList<Contact> selectedContacts;
    ArrayList<Contact> unChangedContactList;
    //this would be used to store the selections made by the user despite the filter imposed by the user
 public    ArrayList<Contact> contactArrayListUncleared;
    int current_position = -1;
    int[] colors = new int[]{R.color.yellow2,R.color.fuchsia,R.color.red,R.color.silver,R.color.gray2,R.color.olive,
                             R.color.purple,R.color.maroon,R.color.aqua, R.color.lime,R.color.teal, R.color.green,
                             R.color.blue, R.color.navy,R.color.black,R.color.Gold,R.color.Pink,R.color.LightPink,
                             R.color.Orange,R.color.LightSalmon,R.color.DarkOrange,R.color.Coral,R.color.HotPink,R.color.Tomato,
                             R.color.LightGoldenrodYellow,R.color.Linen};


    public ContactAdapter(Context context, ArrayList<Contact> contactArrayList){

        this.context = context;
        this.contactArrayList = contactArrayList;
        unChangedContactList = new ArrayList<Contact>();
        this.unChangedContactList.addAll(contactArrayList);
        contactArrayListUncleared = new ArrayList<Contact>();
        contactArrayListUncleared = unChangedContactList;
        assignPositions(contactArrayListUncleared);

    }


    //this method converts arraylist into a String
    public ArrayList<Contact> getContactArrayList(String listString){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Contact> contactArrayList = gson.fromJson(listString, listOfObjects);



        return contactArrayList;
    }

    private void assignPositions(ArrayList<Contact> contactArrayList) {

        for (int i=0;i < contactArrayList.size(); i++){
            Contact contact = contactArrayList.get(i);
            contact.setPosition(i);
        }
        Log.d("unclearedlist",getStringfromArrayList(contactArrayListUncleared));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_contact_row_layout,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Log.d("onBinderHolder", "Justexecuted");
        //initializing holder to be used in the filter method



        Contact contact = contactArrayList.get(position);
        String name = contact.getContactName();
        holder.textViewContactnumber.setText(contact.getPhoneno());
        holder.textViewContactname.setText(name);
        int colorindex = getColorIndex(name);


        //in some cases, it will prevent unwanted situations
        holder.checkBoxSelect.setOnCheckedChangeListener(null);
        holder.checkBoxCameraSelect.setOnCheckedChangeListener(null);
        holder.checkBoxSilence.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBoxSelect.setChecked(contact.isSelected());
        holder.checkBoxCameraSelect.setChecked(contact.isCamera());
        holder.checkBoxSilence.setChecked(contact.isSilenceexception());

        if(holder.checkBoxSelect.isChecked()){

            holder.viewFirstline.setVisibility(View.VISIBLE);
            holder.horizontalScrollView.setVisibility(View.VISIBLE);

        }else{
            holder.viewFirstline.setVisibility(View.GONE);
            holder.horizontalScrollView.setVisibility(View.GONE);

        }

        //setting the background color of the circle
        GradientDrawable bgShape = (GradientDrawable)holder.textViewFirstletter.getBackground();
        bgShape.setColor(colors[colorindex]);

        if(!String.valueOf(name.charAt(0)).isEmpty()) {
            holder.textViewFirstletter.setText(String.valueOf(name.charAt(0)));
        }else{
            holder.textViewFirstletter.setText("#");
        }




        //setUp listener for checkBoxSelect
        setUpCheckedboxSelectlistener(holder, contact, position);
        //setUp listener for cameraSelect
        setUpCameraChecklistener(holder,contact,  position);
        //setUp listener for silenceSelect
        setUpSilenceChecklistener(holder,contact, position);

    }

    public String getStringfromArrayList(ArrayList<Contact> arraylist){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(arraylist,listOfObjects);
        return json;
    }




    private void setUpSilenceChecklistener(final ViewHolder holder, final Contact contact, final int position) {

        holder.checkBoxSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    contact.setSilenceexception(true);
                    //updating the current object to the selected contacts list
                    contactArrayListUncleared.set(contact.getPosition(), contact);
                    //adding the current object to the selectedContactList
                 //   selectedContacts.set(current_position, contact);

                }else{
                    contact.setSilenceexception(false);
                    //updating the current object to the selected contacts list
                    contactArrayListUncleared.set(contact.getPosition(), contact);
                    //adding the current object to the selectedContactList
                  //  selectedContacts.set(current_position, contact);
                }

            }
        });


    }






    private void setUpCameraChecklistener(final ViewHolder holder, final Contact contact, final int position) {

        holder.checkBoxCameraSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //if the current selected phone number is not equal is not equal to the previously selected phone number

                if(isChecked){
                    contact.setCamera(true);
                    //updating the current object to the selected contacts list
                    contactArrayListUncleared.set(contact.getPosition(), contact);
                    //adding the current object to the selectedContactList
                  //  selectedContacts.set(current_position, contact);

                }else{
                    contact.setCamera(false);
                    //updating the current object to the selected contacts list
                    contactArrayListUncleared.set(contact.getPosition(), contact);
                    //adding the current object to the selectedContactList
                 //   selectedContacts.set(current_position, contact);

                }


            }
        });

    }

    private void setUpCheckedboxSelectlistener(final ViewHolder holder, final Contact contact, final int position) {

        //setting checkBoxSelect onchecked listener
        holder.checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                //if the current selected phone number is not equal is not equal to the previously selected phone number
//                if(!previous_number.equals(contact.getPhoneno())) {
//
//                    previous_number = contact.getPhoneno();

                    TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);

                    if (holder.checkBoxSelect.isChecked()) {
                        //setting the current status
                        contact.setSelected(true);


                        //updating the current object to the selected contacts list
                        contactArrayListUncleared.set(contact.getPosition(), contact);

                        holder.viewFirstline.setVisibility(View.VISIBLE);
                        holder.horizontalScrollView.setVisibility(View.VISIBLE);
                    } else {
                        contact.setSelected(false);

                        //updating the current object to the selected contacts list
                        contactArrayListUncleared.set(contact.getPosition(), contact);

                        holder.viewFirstline.setVisibility(View.GONE);
                        holder.horizontalScrollView.setVisibility(View.GONE);
                    }


                Log.d("unclearedlist",getStringfromArrayList(contactArrayListUncleared));

            }
        });

    }





    private int  getColorIndex(String name) {

        int colorindex=0;
        switch (name.charAt(0)){

            case 'a':
            case 'A':colorindex =0;
                break;
            case 'b':
            case 'B':colorindex =1;
                break;

            case 'c':
            case 'C':colorindex =2;
                break;

            case 'd':
            case 'D':colorindex =3;
                break;

            case 'e':
            case 'E':colorindex =4;
                break;
            case 'f':
            case 'F':colorindex =5;
                break;

            case 'g':
            case 'G':colorindex =6;
                break;

            case 'h':
            case 'H':colorindex =7;
                break;

            case 'i':
            case 'I':colorindex =8;
                break;

            case 'j':
            case 'J':colorindex =9;
                break;

            case 'k':
            case 'K':colorindex =10;
                break;

            case 'l':
            case 'L':colorindex =11;
                break;

            case 'm':
            case 'M':colorindex =12;
                break;

            case 'n':
            case 'N':colorindex =13;
                break;

            case 'o':
            case 'O':colorindex =14;
                break;

            case 'p':
            case 'P':colorindex =15;
                break;
            case 'q':
            case 'Q':colorindex =16;
                break;

            case 'r':
            case 'R':colorindex =17;
                break;

            case 's':
            case 'S':colorindex =18;
                break;

            case 't':
            case 'T':colorindex =19;
                break;

            case 'u':
            case 'U':colorindex =20;
                break;

            case 'v':
            case 'V':colorindex =21;
                break;

            case 'w':
            case 'W':colorindex =22;
                break;

            case 'x':
            case 'X':colorindex =23;
                break;

            case 'y':
            case 'Y':colorindex =24;
                break;

            case 'z':
            case 'Z':colorindex =25;
                break;
        }

        return colorindex;

    }


    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View viewFirstline;
        TextView textViewFirstletter, textViewContactname, textViewContactnumber;
        CheckBox checkBoxSelect, checkBoxCameraSelect, checkBoxSmsSelect, checkBoxCallSelect, checkBoxSilence, checkBoxVideocall;
        HorizontalScrollView horizontalScrollView;


        public ViewHolder(View itemView) {
            super(itemView);

            viewFirstline = itemView.findViewById(R.id.viewStartline);

            textViewFirstletter = (TextView) itemView.findViewById(R.id.textViewFirst);
            textViewContactname = (TextView) itemView.findViewById(R.id.textViewContactname);
            textViewContactnumber = (TextView) itemView.findViewById(R.id.textViewMobile);

            checkBoxSelect = (CheckBox) itemView.findViewById(R.id.checkBoxSelect);
            checkBoxCameraSelect = (CheckBox) itemView.findViewById(R.id.checkBoxCamera);
            checkBoxSmsSelect = (CheckBox) itemView.findViewById(R.id.checkBoxSms);
            checkBoxCallSelect = (CheckBox) itemView.findViewById(R.id.checkBoxCall);
            checkBoxSilence = (CheckBox) itemView.findViewById(R.id.checkBoxSilence);
            checkBoxVideocall = (CheckBox) itemView.findViewById(R.id.checkBoxVideo);

            horizontalScrollView = (HorizontalScrollView) itemView.findViewById(R.id.horizontalScrollView);

            //setting onClick listener for itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked  = checkBoxSelect.isChecked();
                    checkBoxSelect.setChecked(!isChecked);
                }
            });

        }
    }



    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactArrayList.clear();

        if (charText.length() == 0) {
            contactArrayList.addAll(contactArrayListUncleared);
        }
        else {

            for (Contact contact : contactArrayListUncleared) {

                if ((contact.getContactName()+contact.getPhoneno()).toLowerCase(Locale.getDefault()).contains(charText)) {

                    contactArrayList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
        Log.d("clearedlist", getStringfromArrayList(contactArrayList));
    }
}
