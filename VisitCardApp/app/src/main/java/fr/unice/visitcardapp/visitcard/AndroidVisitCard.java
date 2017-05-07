package fr.unice.visitcardapp.visitcard;

import android.widget.TextView;

import java.util.ArrayList;

public class AndroidVisitCard extends AbstractVisitCard {
    final static public String NUM_VIEW_HEADER = "Phone Number : ";
    final static public String MAIL_VIEW_HEADER = "Email : ";
    final static public String ADD_VIEW_HEADER = "Address : ";

    AndroidVisitCard(String fullName, String phoneNumber, String address, String email) {
        super(fullName, phoneNumber, address, email);
    }

    public String displayUserInfo(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails, TextView t1, TextView t2) {
        ArrayList<String> userInfo = displayUser(phones, addresses, emails);

        if(getFirstUserChoice() == 1) {
            t1.append(NUM_VIEW_HEADER + "\n" + userInfo.get(0));
        } else if(getSecondUserChoice() == 1){
            t2.append(NUM_VIEW_HEADER + "\n" + userInfo.get(0));
        }

        if(getFirstUserChoice() == 2) {
            t1.append(ADD_VIEW_HEADER + "\n" + userInfo.get(1));
        } else if(getSecondUserChoice() == 2) {
            t2.append(ADD_VIEW_HEADER + "\n" + userInfo.get(1));
        }

        if(getFirstUserChoice() == 3) {
            t1.append(MAIL_VIEW_HEADER + "\n" + userInfo.get(2));
        } else if(getSecondUserChoice() == 3) {
            t2.append(MAIL_VIEW_HEADER + "\n" + userInfo.get(2));
        }

        return userInfo.get(0);

    }

}
