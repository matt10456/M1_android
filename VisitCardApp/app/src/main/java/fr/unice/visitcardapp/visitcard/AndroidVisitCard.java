package fr.unice.visitcardapp.visitcard;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AndroidVisitCard extends AbstractVisitCard {
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

    public ArrayList<String> displayContactInfo(ArrayList<String> contactInfo, boolean inDb) {
        // Calls for the superclass method to display the fields properly
        contactInfo = displayContact(contactInfo);

        String firstDisplay = null;
        String secondDisplay = null;
        if (inDb) {
            // Display first
            switch (getFirstUserChoice()) {
                case 1:
                    firstDisplay = NUM_VIEW_HEADER + contactInfo.get(0);
                    break;
                case 2:
                    firstDisplay = ADD_VIEW_HEADER + contactInfo.get(1);
                    break;
                case 3:
                    firstDisplay = MAIL_VIEW_HEADER + contactInfo.get(2);
                    break;
                default:
                    firstDisplay = NUM_VIEW_HEADER + contactInfo.get(3);
                    break;
            }
            // Display second
            switch (getSecondUserChoice()) {
                case 1:
                    secondDisplay = NUM_VIEW_HEADER + contactInfo.get(0);
                    break;
                case 2:
                    secondDisplay = ADD_VIEW_HEADER + contactInfo.get(1);
                    break;
                case 3:
                    secondDisplay = MAIL_VIEW_HEADER + contactInfo.get(2);
                    break;
                default:
                    secondDisplay = NUM_VIEW_HEADER + contactInfo.get(3);
                    break;
            }
        }

        return new ArrayList<>(Arrays.asList(firstDisplay,secondDisplay));
    }
}
