package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;

interface IVisitCard {

    void displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails);

    void displayContact(ArrayList<String> contactInfo);

    void edit(String choice1, String choice2);

}
