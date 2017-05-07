package fr.unice.visitcardapp;

import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import java.util.ArrayList;
import android.util.Log;

import com.google.zxing.Result;

public class AndroidCommunication extends AbstractCommunication {
    private final static String ERROR_TEXT = "InvalidQR";
    private final static String RECEIVING_INTENT = "android.provider.Telephony.SMS_RECEIVED";

    public Intent sendSMS(Context context, Result qrResult, String info) {
        Intent i = new Intent(context, MainActivity.class);

        String toSend = super.send(qrResult.toString(), info);
        if (toSend.equals(ERROR_TEXT)) {
            i.putExtra(ERROR_TEXT, true);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(getDestinationNum(), null, toSend, null, null);
        }

        return i;
    }

    private void receiveSMS(Context context, String smsBody) {
        Database db = new Database(context);

        if(smsBody.startsWith(SENT_PREFIX)) {
            ArrayList<String> contactData = receive(smsBody);

            Toast.makeText(context, context.getResources().getString(R.string.saving_card) + " \n"+contactData.get(2), Toast.LENGTH_SHORT).show();
            // Add contact in the android database.
            addContact(contactData.get(2), contactData.get(3), contactData.get(4), contactData.get(5), context);
            // Add contact in App database.
            db.insertContact(contactData.get(2), contactData.get(0), contactData.get(1));
        }
    }

    public class SmsReceiver extends BroadcastReceiver {
        Database db;

        @Override
        public void onReceive(Context context, Intent intent) {
            db = new Database(context);

            if (intent.getAction().equals(RECEIVING_INTENT)) {
                // Retrieve the sms
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs;

                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String body = "";
                    for (int i=0; i<msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        body =  msgs[i].getMessageBody();
                        receiveSMS(context, body);
                    }
                }
            }
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


    public class SmsReceiverOLD extends BroadcastReceiver {
        Database db;

        @Override
        public void onReceive(Context context, Intent intent) {
            db = new Database(context);

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                // Retrieve the sms
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs;

                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String body = "";
                    for (int i=0; i<msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        body =  msgs[i].getMessageBody();
                        if(body.startsWith("##VCA##")) {
                            // Receive the sms
                            String[] name = body.split(";");
                            String display1 = name[0];
                            String display2 = name[1];
                            String nameAdd = name[2];
                            String mobileAdd = name[3];
                            String adrAdd;
                            String emailAdd;
                            if(name.length < 5) {
                                adrAdd = null;
                            } else {
                                adrAdd = name[4];
                            } if(name.length < 6) {
                                emailAdd = null;
                            } else {
                                emailAdd = name[5];
                            }
                            Toast.makeText(context, context.getResources().getString(R.string.saving_card) + " \n"+nameAdd, Toast.LENGTH_SHORT).show();
                            // Add contact in the android database.
                            addContact(nameAdd, mobileAdd,adrAdd,emailAdd, context);
                            // Add contact in App database.
                            db.insertContact(nameAdd, display1, display2);
                        }
                    }
                }
            }
        }

        public void addContact(String DisplayName, String MobileNumber, String adr, String emailID, Context context) {
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

}