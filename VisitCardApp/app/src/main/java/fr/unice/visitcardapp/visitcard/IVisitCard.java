package fr.unice.visitcardapp.visitcard;

import java.util.ArrayList;

interface IVisitCard {

    ArrayList<String> displayUser(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails);

    ArrayList<String> displayContact(ArrayList<String> contactInfo);

    ArrayList<Integer> edit(String choice1, String choice2);

}
