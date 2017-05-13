package fr.unice.visitcardapp.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    AndroidCommunication com = new AndroidCommunication();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(AndroidCommunication.RECEIVING_INTENT)) {
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
                    com.receiveSMS(context, body);
                }
            }
        }
    }
}