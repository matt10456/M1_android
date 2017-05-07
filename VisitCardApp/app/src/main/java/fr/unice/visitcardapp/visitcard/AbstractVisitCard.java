package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;

public abstract class AbstractVisitCard implements IVisitCard {
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
    * The display function takes as input the result of a database request
    * and extracts if present the necessary information that will be
    * displayed later on
    * */
    @Override
    public ArrayList<String> displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails) {
        ArrayList<String> displayInfo = new ArrayList<>();

        if (phones.size() != 0 && phones.get(0) != null) {
            displayInfo.add(phones.get(0));
        }
        if (addresses.size() != 0 && addresses.get(0) != null) {
            displayInfo.add(addresses.get(0));
        }
        if (emails.size() != 0 && emails.get(0) != null) {
            displayInfo.add(emails.get(0));
        }

        return displayInfo;

    }

    @Override
    public void displayContact() {

    }

    @Override
    public void edit() {

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
