package fr.unice.visitcardapp.visitcard;

import android.database.Cursor;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import fr.unice.visitcardapp.database.Database;

import static fr.unice.visitcardapp.database.Database.CONTACTS_COLUMN_1;
import static fr.unice.visitcardapp.database.Database.CONTACTS_COLUMN_2;

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
        if (isUserCard() && getFullName() != null) {
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
        }

        return getPhoneNumber();

    }

    public ArrayList<String> displayContactInfo(ArrayList<String> contactInfo, Database db) {
        // Calls for the superclass method to register the fields properly
        displayContact(contactInfo);

        String firstDisplay = null;
        String secondDisplay = null;

        Cursor rs = db.getDataByName(getFullName());
        rs.moveToFirst();
        if (rs.getCount() > 0) {
            setFirstUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_1))));
            setSecondUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_2))));
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
