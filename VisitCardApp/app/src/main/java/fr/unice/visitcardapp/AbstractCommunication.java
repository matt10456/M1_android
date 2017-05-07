package fr.unice.visitcardapp;

import java.util.ArrayList;

abstract class AbstractCommunication implements ICommunication {
    final static String SENT_PREFIX = "##VCA##";
    final static private String ACCEPTED_PREFIX = "QRAPP:";
    final static private String ERROR_QR = "InvalidQR";
    final static private int ACCEPTED_MIN_SIZE = ACCEPTED_PREFIX.length();
    private String destinationNum;

    AbstractCommunication( ) {  }

    /*
    * The send function takes as input a string of information that's behind
    * a QR code and readies it before it is sent through a specialized
    * function, if the result contains a correct prefix and phone number
    * */
    @Override
    public String send(String qrString, String infoToSend) {
        String resultPrefix = null;
        String sendInfo = ERROR_QR;

        if (qrString.length() >= ACCEPTED_MIN_SIZE) {
            resultPrefix = qrString.substring(0, ACCEPTED_MIN_SIZE);
        }

        if (resultPrefix != null && resultPrefix.equals(ACCEPTED_PREFIX)) {
            String resultSuffix = qrString.substring(ACCEPTED_MIN_SIZE, qrString.length());
            // Checks that the suffix contains a potentially valid phone number, which
            // doesn't contain at least one letter
            if (!resultSuffix.matches(".*[a-zA-Z]+.*")) {
                setDestinationNum(resultSuffix);
                sendInfo = SENT_PREFIX + infoToSend;
            }
        }

        return sendInfo;
    }

    /*
     * The receive function takes the content of the sent message and extracts
     * the necessary info so that a new contact can be created, the sending part
     * of the whole process makes sure that the info being sent is valid
     * */
    @Override
    public ArrayList<String> receive(String smsBody) {
        ArrayList<String> contactData = new ArrayList<>();
        // Receive the sms
        String[] name = smsBody.split(";");
        // The first user choice for the card is added to the result
        contactData.add(name[0]);
        // The second user choice for the card is added to the result
        contactData.add(name[1]);
        // The username for the other card is added to the result
        contactData.add(name[2]);
        // The mobile number for the other card is added to the result
        contactData.add(name[3]);
        if (name.length < 5) {
            contactData.add(null);
        } else {
            // The address for the other card is added to the result
            contactData.add(name[4]);
        }
        if (name.length < 6) {
            contactData.add(null);
        } else {
            // The email for the other card is added to the result
            contactData.add(name[5]);
        }

        return contactData;
    }

    String getDestinationNum() {
        return destinationNum;
    }

    private void setDestinationNum(String destinationNum) {
        this.destinationNum = destinationNum;
    }
}
