package com.example.SystemHealth_Android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Data source for Contacts in Contact Tracking
 *
 * Created by emou on 7/7/14.
 */
public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context){
        dbHelper = new ContactDBHelper(context);
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

/*    public ArrayList<Contact> getAllContacts(String sortField, String sortOrder){
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String query = "SELECT * FROM contacts ORDER BY " + sortField + " " + sortOrder;
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Contact newContact = new Contact(cursor.getString(1));
            newContact.mContactId = cursor.getInt(0);
            newContact.mDateCreated = cursor.getInt(2);
            newContact.mStatus = cursor.getString(3);
            newContact.mRequestCode = cursor.getInt(4);
            contacts.add(newContact);
            cursor.moveToNext();
        }
        cursor.close();

        return contacts;
    }*/

    //returns ordered ArrayList of Contacts associated with the given request code
    public ArrayList<Contact> getContactsByRequestCode(int requestCode, String sortField, String sortOrder){
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String query = "SELECT * FROM contacts WHERE requestCode = " + requestCode +
                " ORDER BY " + sortField + " " + sortOrder;
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Contact newContact = new Contact(cursor.getString(1));
            newContact.mContactId = cursor.getInt(0);
            newContact.mDateCreated = cursor.getInt(2);
            newContact.mStatus = cursor.getString(3);
            newContact.mRequestCode = cursor.getInt(4);
            contacts.add(newContact);
            cursor.moveToNext();
        }
        cursor.close();
        return contacts;
    }

/*    public ArrayList<Contact> getContactsByDate(long startDate, long endDate){
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String query = "SELECT * FROM contacts WHERE dateCreated > " + startDate + " AND dateCreated < " + endDate +
                " ORDER BY dateCreated";
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Contact newContact = new Contact(cursor.getString(1));
            newContact.mContactId = cursor.getInt(0);
            newContact.mDateCreated = cursor.getInt(2);
            newContact.mStatus = cursor.getString(3);
            newContact.mRequestCode = cursor.getInt(4);
            contacts.add(newContact);
            cursor.moveToNext();
        }
        cursor.close();
        return contacts;
    }*/


    public void clearContactsDB(){
        dbHelper.onUpgrade(database,0,1);
    }

    //add given list of contacts to SQLite database
    public int addToContactList(ArrayList<Contact> contacts){
        int count=0;
        int numberOfContacts = contacts.size();
        Contact contact;
        ContentValues values;

        for(int i=0;i<numberOfContacts;i++){
            contact = contacts.get(i);

            values = new ContentValues();
            values.put("contactId",contact.mContactId);
            values.put("name",contact.mName);
            values.put("dateCreated",contact.mDateCreated);
            values.put("status",contact.mStatus);
            values.put("requestCode",contact.mRequestCode);

            if(database.insert("contacts",null,values)>0){
                count++;
            }
        }
        return count;
    }
}
