package fr.unice.visitcardapp.visitcard;

import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AndroidVisitCard extends AbstractVisitCard {
    public AndroidVisitCard() {
        super();
    }

    public AndroidVisitCard(boolean user) {
        super(null, null, null, null, user);
    }

    public AndroidVisitCard(String fullName, String phoneNumber, String address, String email, boolean user) {
        super(fullName, phoneNumber, address, email, user);
    }

    public String displayUserInfo(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails, TextView t1, TextView t2) {
        displayUser(phones, addresses, emails);

        if(getFirstUserChoice() == 1) {
            t1.append(NUM_VIEW_HEADER + "\n" + getPhoneNumber());
        } else if(getSecondUserChoice() == 1){
            t2.append(NUM_VIEW_HEADER + "\n" + getPhoneNumber());
        }

        if(getFirstUserChoice() == 2) {
            t1.append(ADD_VIEW_HEADER + "\n" + getAddress());
        } else if(getSecondUserChoice() == 2) {
            t2.append(ADD_VIEW_HEADER + "\n" + getAddress());
        }

        if(getFirstUserChoice() == 3) {
            t1.append(MAIL_VIEW_HEADER + "\n" + getEmail());
        } else if(getSecondUserChoice() == 3) {
            t2.append(MAIL_VIEW_HEADER + "\n" + getEmail());
        }

        return getPhoneNumber();

    }

    public ArrayList<String> displayContactInfo(ArrayList<String> contactInfo, boolean inDb) {
        // Calls for the superclass method to display the fields properly
        displayContact(contactInfo);

        String firstDisplay = null;
        String secondDisplay = null;
        if (inDb) {
            // Display first
            switch (getFirstUserChoice()) {
                case 1:
                    firstDisplay = NUM_VIEW_HEADER + getPhoneNumber();
                    break;
                case 2:
                    firstDisplay = ADD_VIEW_HEADER + getAddress();
                    break;
                case 3:
                    firstDisplay = MAIL_VIEW_HEADER + getEmail();
                    break;
                default:
                    firstDisplay = NUM_VIEW_HEADER + getAddress();
                    break;
            }
            // Display second
            switch (getSecondUserChoice()) {
                case 1:
                    secondDisplay = NUM_VIEW_HEADER + getPhoneNumber();
                    break;
                case 2:
                    secondDisplay = ADD_VIEW_HEADER + getAddress();
                    break;
                case 3:
                    secondDisplay = MAIL_VIEW_HEADER + getEmail();
                    break;
                default:
                    secondDisplay = NUM_VIEW_HEADER + getPhoneNumber();
                    break;
            }
        }

        return new ArrayList<>(Arrays.asList(firstDisplay,secondDisplay));
    }

    public void editCard(Spinner s1, Spinner s2) {
        String selected1 = s1.getSelectedItem().toString();
        String selected2 = s2.getSelectedItem().toString();

        edit(selected1, selected2);
    }
}
