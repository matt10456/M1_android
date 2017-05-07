package fr.unice.visitcardapp;

interface ICommunication {

    String send(String qrString, String infoToSend);

    void receive();

}
