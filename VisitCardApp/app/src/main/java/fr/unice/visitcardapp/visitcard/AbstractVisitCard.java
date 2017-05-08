package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractVisitCard implements IVisitCard {
    final static public String NUM_VIEW_HEADER = "Phone Number : ";
    final static public String MAIL_VIEW_HEADER = "Email : ";
    final static public String ADD_VIEW_HEADER = "Address : ";
    final static public String NUM_SPINNER_CHOICE = "Phone Number";
    final static public String MAIL_SPINNER_CHOICE = "Email";
    final static public String ADD_SPINNER_CHOICE = "Address";
    final public static String USER_CARD = "USER CARD";
    final public static String CONTACT_CARD = "CONTACT CARD";
    private String fullName;
    private String phoneNumber;
    private String address;
    private String email;
    private int firstUserChoice;
    private int secondUserChoice;

    public AbstractVisitCard(String fullName, String phoneNumber, String address, String email) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        firstUserChoice = 1;
        secondUserChoice = 2;
    }

    /*
    * The display user function takes as input the results of a database
    * request and extracts if present the necessary information that
    * will be displayed later on
    * */
    @Override
    public ArrayList<String> displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails) {
        ArrayList<String> displayInfo = new ArrayList<>();

        if (phones.size() != 0 && phones.get(0) != null) {
            displayInfo.add(phones.get(0));
        } else {
            displayInfo.add(null);
        }
        if (addresses.size() != 0 && addresses.get(0) != null) {
            displayInfo.add(addresses.get(0));
        } else {
            displayInfo.add(null);
        }
        if (emails.size() != 0 && emails.get(0) != null) {
            displayInfo.add(emails.get(0));
        } else {
            displayInfo.add(null);
        }

        return displayInfo;

    }

    /*
    * The display contact function takes as input the contacts' infos
    * and chooses the right ones to display according to the default
    * choices or that the user made after editing the card, if present
    * in the database
    * */
    @Override
    public ArrayList<String> displayContact(ArrayList<String> contactInfo) {
        String displayName = "" + contactInfo.get(0);
        String displayNumber = "\n" + contactInfo.get(1);
        String displayEmail = "\n" + contactInfo.get(2);
        String displayAdr = "\n" + contactInfo.get(3);

        if(displayName.equals("null")) { displayName = " "; }
        if(displayNumber.equals("\nnull")) { displayNumber = " "; }
        if(displayEmail.equals("\nnull")) { displayEmail = " "; }
        if(displayAdr.equals("\nnull")) { displayAdr = " "; }

        return new ArrayList<>(Arrays.asList(displayName,displayNumber,displayEmail,displayAdr));
    }

    /*
    * The edit function takes as input the selection of the user
    * and delivers a result that will be saved later on according
    * to the selection
    * */
    @Override
    public ArrayList<Integer> edit(String choice1, String choice2) {
        ArrayList<Integer> newChoices = new ArrayList<>();

        switch(choice1) {
            case NUM_SPINNER_CHOICE:
                newChoices.add(1); break;
            case ADD_SPINNER_CHOICE:
                newChoices.add(2); break;
            case MAIL_SPINNER_CHOICE:
                newChoices.add(3); break;
            default :
                newChoices.add(1); break;
        }

        switch(choice2) {
            case NUM_SPINNER_CHOICE:
                newChoices.add(1); break;
            case ADD_SPINNER_CHOICE:
                newChoices.add(2); break;
            case MAIL_SPINNER_CHOICE:
                newChoices.add(3); break;
            default :
                newChoices.add(1); break;
        }

        return newChoices;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFirstUserChoice() {
        return firstUserChoice;
    }

    public void setFirstUserChoice(int firstUserChoice) {
        this.firstUserChoice = firstUserChoice;
    }

    public int getSecondUserChoice() {
        return secondUserChoice;
    }

    public void setSecondUserChoice(int secondUserChoice) {
        this.secondUserChoice = secondUserChoice;
    }

}
