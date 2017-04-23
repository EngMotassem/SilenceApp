package techheromanish.example.com.silenceapp.helper;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import techheromanish.example.com.silenceapp.activities.MainActivity;
import techheromanish.example.com.silenceapp.models.Contact;
import techheromanish.example.com.silenceapp.sqlite.KeyValueDb;


/**
 * Created by Manish on 12/18/2016.
 */

public class ContactFetcher {

    public ArrayList<Contact> phoneContacts;
    public Context context;
    String phoneNumber;


    public ContactFetcher(Context context) {
        this.context = context;
        phoneContacts = new ArrayList<Contact>();
    }


    //method to init the arraylist of phoneContacts
    public boolean initphoneContacts() {


        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }
//--</ get all Contacts >--

//----< Check: hasContacts >----
        //----< has Contacts >----
//----< @Loop: all Contacts >----
        if (cursor_Android_Contacts.getCount() > 0) {
            while (cursor_Android_Contacts.moveToNext()) {
//< init >
                Contact contact = new Contact();
                String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String contact_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


//----< set >----
                contact.setContactName(contact_name);


//----< get phone number >----
                int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Log.d("hasPhoneNumber", "Hell Yeah!");

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        Log.d("whileLoop", "i m here");
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replace(" ", "");

                        //   Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

//        int i =0;
//        while (cursor.moveToNext()) {
//
//            Contact contact = new Contact();

//            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//
//            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            contact.setContactName(contactName);


                        // Log.d("rawPhoneNumber", String.valueOf(++i) + phoneNumber);

                        /**if the chatAt(0) == '+', then simply set it to the contact **/
                        /** if the contact's length is 10, then concat it with the country code.**/
                        if (phoneNumber.length() == 8) {
                            //concat it with the user's country code as very few have different
                            String countrycode = KeyValueDb.get(context, Config.USER_COUNTRY_CODE, "+965");
                            phoneNumber = countrycode + phoneNumber;

                        }/**if the contact's length is 11 and it begins with zero, then remove zero and then add the country code **/
                        else if (phoneNumber.length() == 9 && phoneNumber.charAt(0) == '0') {

                            //getting the country code
                            String countrycode = KeyValueDb.get(context, Config.USER_COUNTRY_CODE, "+965");
                            //getting the number excluding the 0 in the begining
                            phoneNumber = phoneNumber.substring(1, 9);
                            //adding country code to the phonenumber
                            phoneNumber = countrycode + phoneNumber;

                        }
                        /**setting the phone number to the contact object **/
                        if (phoneNumber.length() >= 8) {

                            phoneNumber = phoneNumber.replace(" ", "");
                            contact.setPhoneno(phoneNumber);
                        }

                    }
                }

                if (phoneNumber != null) {

                    if (phoneNumber.length() >= 8) {
                        phoneContacts.add(contact);
                        Log.d("phoneNumber", phoneNumber);
                        phoneNumber = "";
                    }
                }


            }
        }

        //saving the read phoneContacts
        saveContactsArrayList();

        return true;
    }

    public void saveContactsArrayList(){
        Type listOfObjects = new TypeToken<List<Contact>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(phoneContacts,listOfObjects);
        KeyValueDb.set(context,Config.CONTACTS_ARRAYLIST,json,1);

    }


}
