package com.example.SystemHealth_Android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by emou on 6/27/14.
 */
public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context){
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public ArrayList<Contact> getContacts(String sortField, String sortOrder){
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        try {
            String query = "SELECT * FROM contact ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Contact newContact = new Contact(cursor.getString(1),cursor.getInt(2));
                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            contacts.clear();
            e.printStackTrace();
        }

        return contacts;
    }

    public ArrayList<Contact> getTopContacts(int limit){
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        try {
            String query = "SELECT * FROM contact ORDER BY amount DESC LIMIT " + limit;
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Contact newContact = new Contact(cursor.getString(1),cursor.getInt(2));
                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            contacts.clear();
            e.printStackTrace();
        }

        return contacts;
    }

    public void clearContactsDB(){
        dbHelper.onUpgrade(database,0,1);
    }

    public int addToContactList(ArrayList<Contact> contacts){
        int count=0;
        int numberOfContacts = contacts.size();
        Contact contact;
        ContentValues values;

        for(int i=0;i<numberOfContacts;i++){
            contact = contacts.get(i);

            values = new ContentValues();
            values.put("contactType", contact.mName);
            values.put("amount",contact.mAmount);

            if(database.insert("contact",null,values)>0){
                count++;
            }
        }

        return count;
    }
}
