package fr.unice.visitcardapp;

abstract class AbstractCommunication implements ICommunication {
    final static private String ACCEPTED_PREFIX = "QRAPP:";
    final static private String SENT_PREFIX = "##VCA##";
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
        String sendInfo;

        if (qrString.length() >= ACCEPTED_MIN_SIZE) {
            resultPrefix = qrString.substring(0,ACCEPTED_MIN_SIZE);
        }

        if (resultPrefix != null && resultPrefix.equals(ACCEPTED_PREFIX)) {
            setDestinationNum(qrString.substring(ACCEPTED_MIN_SIZE, qrString.length()));
            sendInfo = SENT_PREFIX + infoToSend;
        } else {
            sendInfo = ERROR_QR;
        }

        return sendInfo;
    }

    @Override
    public void receive() {

    }

    String getDestinationNum() {
        return destinationNum;
    }

    void setDestinationNum(String destinationNum) {
        this.destinationNum = destinationNum;
    }
}
