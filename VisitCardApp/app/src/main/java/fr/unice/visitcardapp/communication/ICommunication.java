package fr.unice.visitcardapp.communication;

import java.util.ArrayList;

interface ICommunication {

    String send(String qrString, String infoToSend);

    ArrayList<String> receive(String smsBody);

}
