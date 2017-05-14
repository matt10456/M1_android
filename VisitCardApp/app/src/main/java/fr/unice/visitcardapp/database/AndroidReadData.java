package fr.unice.visitcardapp.database;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CITY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.STREET;

/**
 * Created by Matthieu on 14/05/2017.
 */

public abstract class AndroidReadData {

    public static ArrayList<String> read(ContentResolver contentResolver, Uri dataUri) {
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri ADDRESS_CONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String ADDRESS_CONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;

        Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        String name = "";
        String email = "";

        String phoneNumber = null;
        String ADR = "";
        StringBuffer output = new StringBuffer();
        ArrayList<String> read = new ArrayList<String>();
        Cursor cursor = contentResolver.query(dataUri, null, null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                output = new StringBuffer();

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    output.append("\n First Name:" + name);
                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);
                    }
                    phoneCursor.close();

                    // Read one address of the contact.
                    int max = 1; // Number of addresses.
                    Cursor AddressCursor = contentResolver.query(ADDRESS_CONTENT_URI, null, ADDRESS_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (AddressCursor.moveToNext() && max == 1) {
                        ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(STREET));
                        ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(CITY));
                        ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(POSTCODE));
                        ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(COUNTRY));
                        max++;
                    }
                    AddressCursor.close();
                    ADR = ADR.replace("null", "");

                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        output.append("\n Email:" + email);
                    }
                    emailCursor.close();
                }
            }
        }
        read.add(name);
        read.add(ADR);
        read.add(email);
        read.add(phoneNumber);
        read.add(output.toString());

        return read;

    }
}

