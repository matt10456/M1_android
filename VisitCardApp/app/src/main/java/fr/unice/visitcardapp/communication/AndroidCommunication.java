package fr.unice.visitcardapp.communication;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;
import java.util.ArrayList;

import com.google.zxing.Result;

import fr.unice.visitcardapp.database.SQLDatabase;
import fr.unice.visitcardapp.MainActivity;
import fr.unice.visitcardapp.R;

public class AndroidCommunication extends AbstractCommunication {
    final static String RECEIVING_INTENT = "android.provider.Telephony.SMS_RECEIVED";

    public Intent sendSMS(Context context, Result qrResult, String info) {
        Intent i = new Intent(context, MainActivity.class);

        String toSend = super.send(qrResult.toString(), info);
        if (toSend.equals(ERROR_QR)) {
            i.putExtra(ERROR_QR, true);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(getDestinationNum(), null, toSend, null, null);
        }

        return i;
    }

    void receiveSMS(Context context, String smsBody) {
        SQLDatabase db = new SQLDatabase(context);

        if(smsBody.startsWith(SENT_PREFIX)) {
            ArrayList<String> contactData = receive(smsBody);

            Toast.makeText(context, context.getResources().getString(R.string.saving_card) + " \n"+contactData.get(3), Toast.LENGTH_SHORT).show();
            // Add contact in the android database.
            addContact(contactData.get(3), contactData.get(4), contactData.get(5), contactData.get(6), context);
            // Add contact in App database.
            db.insertContact(contactData.get(3), contactData.get(0), contactData.get(1),contactData.get(2));
        }
    }

    private void addContact(String DisplayName, String MobileNumber, String adr, String emailID, Context context) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        //------------------------------------------------------ Adresse
        if (adr != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, adr)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}