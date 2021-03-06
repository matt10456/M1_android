package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;

public abstract class AbstractVisitCard implements IVisitCard {
    final static public String NUM_VIEW_HEADER = "Phone Number : ";
    final static public String MAIL_VIEW_HEADER = "Email : ";
    final static public String ADD_VIEW_HEADER = "Address : ";
    final static public String NUM_SPINNER_CHOICE = "Phone Number";
    final static public String MAIL_SPINNER_CHOICE = "Email";
    final static public String ADD_SPINNER_CHOICE = "Address";
    final public static String USER_CARD = "USER CARD";
    final public static String CONTACT_CARD = "CONTACT CARD";
    private boolean userCard;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String email;
    private int firstUserChoice;
    private int secondUserChoice;
    private int thirdUserChoice;

    AbstractVisitCard() {  }

    AbstractVisitCard(String fullName, String phoneNumber, String address, String email, boolean user) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        userCard = user;
        firstUserChoice = 1;
        secondUserChoice = 2;
        secondUserChoice = 3;
    }

    /*
    * The display user function takes as input the results of a database
    * request and extracts if present the necessary information that
    * will be displayed later on
    * */
    @Override
    public void displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails) {
        if (phones.size() != 0 && phones.get(0) != null) {
            phoneNumber = phones.get(0);
        } else {
            phoneNumber = "";
        }
        if (addresses.size() != 0 && addresses.get(0) != null) {
            address = addresses.get(0);
        } else {
            address = "";
        }
        if (emails.size() != 0 && emails.get(0) != null) {
            email = emails.get(0);
        } else {
            email = "";
        }

    }

    /*
    * The display contact function takes as input the contacts' infos
    * and chooses the right ones to display according to the default
    * choices or that the user made after editing the card, if present
    * in the database
    * */
    @Override
    public void displayContact(ArrayList<String> contactInfo) {
        setFullName("" + contactInfo.get(0));
        setPhoneNumber("" + contactInfo.get(1));
        setAddress("" + contactInfo.get(2));
        setEmail("" + contactInfo.get(3));

        if(fullName.equals("null")) { setFullName(" "); }
        if(phoneNumber.equals("null")) { setPhoneNumber(" "); }
        if(address.equals("null")) { setAddress(" "); }
        if(email.equals("null")) { setEmail(" "); }
    }

    /*
    * The edit function takes as input the selection of the user
    * and delivers a result that will be saved later on according
    * to the selection
    * */
    @Override
    public void edit(String choice1, String choice2, String choice3) {

        switch(choice1) {
            case NUM_SPINNER_CHOICE:
                firstUserChoice = 1; break;
            case ADD_SPINNER_CHOICE:
                firstUserChoice = 2; break;
            case MAIL_SPINNER_CHOICE:
                firstUserChoice = 3; break;
            default :
                firstUserChoice = 1; break;
        }

        switch(choice2) {
            case NUM_SPINNER_CHOICE:
                secondUserChoice = 1; break;
            case ADD_SPINNER_CHOICE:
                secondUserChoice = 2; break;
            case MAIL_SPINNER_CHOICE:
                secondUserChoice = 3; break;
            default :
                secondUserChoice = 1; break;
        }

        switch(choice3) {
            case NUM_SPINNER_CHOICE:
                thirdUserChoice = 1; break;
            case ADD_SPINNER_CHOICE:
                thirdUserChoice = 2; break;
            case MAIL_SPINNER_CHOICE:
                thirdUserChoice = 3; break;
            default :
                thirdUserChoice = 1; break;
        }
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

    public int getThirdUserChoice() {
        return thirdUserChoice;
    }

    public void setThirdUserChoice(int thirdUserChoice) {
        this.thirdUserChoice = thirdUserChoice;
    }


    public boolean isUserCard() {
        return userCard;
    }

    public void setUserCard(boolean userCard) {
        this.userCard = userCard;
    }

}
