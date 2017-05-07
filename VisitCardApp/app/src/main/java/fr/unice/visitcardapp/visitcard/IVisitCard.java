package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;

interface IVisitCard {

    ArrayList<String> displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails);

    void displayContact();

    void edit();

}
