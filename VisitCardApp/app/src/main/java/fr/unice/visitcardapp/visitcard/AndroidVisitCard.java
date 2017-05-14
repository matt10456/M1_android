package fr.unice.visitcardapp.visitcard;

import android.database.Cursor;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import fr.unice.visitcardapp.database.SQLDatabase;

import static fr.unice.visitcardapp.database.SQLDatabase.CONTACTS_COLUMN_1;
import static fr.unice.visitcardapp.database.SQLDatabase.CONTACTS_COLUMN_2;
import static fr.unice.visitcardapp.database.SQLDatabase.CONTACTS_COLUMN_3;

public class AndroidVisitCard extends AbstractVisitCard {
    public AndroidVisitCard() {
        super();
    }

    public AndroidVisitCard(boolean user) {
        super(null, null, null, null, user);
    }

    public String displayUserInfo(ArrayList<String> phones, ArrayList<String> addresses, ArrayList<String> emails, TextView t1, TextView t2, TextView t3) {
        if (isUserCard() && getFullName() != null) {
            displayUser(phones, addresses, emails);

            if(getFirstUserChoice() == 1) {
                t1.append(NUM_VIEW_HEADER + "\n" + getPhoneNumber());
            } else if(getSecondUserChoice() == 1){
                t2.append(NUM_VIEW_HEADER + "\n" + getPhoneNumber());
            } else if(getThirdUserChoice() == 1) {
                t3.append(NUM_VIEW_HEADER + "\n" + getPhoneNumber());
            }

            if(getFirstUserChoice() == 2) {
                t1.append(ADD_VIEW_HEADER + "\n" + getAddress());
            } else if(getSecondUserChoice() == 2) {
                t2.append(ADD_VIEW_HEADER + "\n" + getAddress());
            } else if(getThirdUserChoice() == 2) {
                t3.append(ADD_VIEW_HEADER + "\n" + getAddress());
            }

            if(getFirstUserChoice() == 3) {
                t1.append(MAIL_VIEW_HEADER + "\n" + getEmail());
            } else if(getSecondUserChoice() == 3) {
                t2.append(MAIL_VIEW_HEADER + "\n" + getEmail());
            } else if(getThirdUserChoice() == 3) {
                t3.append(MAIL_VIEW_HEADER + "\n" + getEmail());
            }
        }

        return getPhoneNumber();

    }

    public ArrayList<String> displayContactInfo(ArrayList<String> contactInfo, SQLDatabase db) {
        // Calls for the superclass method to register the fields properly
        displayContact(contactInfo);

        String firstDisplay = null;
        String secondDisplay = null;
        String thirdDisplay = null;

        Cursor rs = db.getDataByName(getFullName());
        rs.moveToFirst();
        if (rs.getCount() > 0) {
            setFirstUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_1))));
            setSecondUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_2))));
            setThirdUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_3))));
            // Display first
            switch (getFirstUserChoice()) {
                case 1:
                    firstDisplay = NUM_VIEW_HEADER + "\n" + getPhoneNumber();
                    break;
                case 2:
                    firstDisplay = ADD_VIEW_HEADER + "\n" + getAddress();
                    break;
                case 3:
                    firstDisplay = MAIL_VIEW_HEADER + "\n" + getEmail();
                    break;
                default:
                    firstDisplay = NUM_VIEW_HEADER + "\n" + getAddress();
                    break;
            }
            // Display second
            switch (getSecondUserChoice()) {
                case 1:
                    secondDisplay = NUM_VIEW_HEADER + "\n" + getPhoneNumber();
                    break;
                case 2:
                    secondDisplay = ADD_VIEW_HEADER + "\n" + getAddress();
                    break;
                case 3:
                    secondDisplay = MAIL_VIEW_HEADER + "\n" + getEmail();
                    break;
                default:
                    secondDisplay = NUM_VIEW_HEADER + "\n" + getPhoneNumber();
                    break;
            }
            // Display third
            switch (getThirdUserChoice()) {
                case 1:
                    thirdDisplay = NUM_VIEW_HEADER + "\n" + getPhoneNumber();
                    break;
                case 2:
                    thirdDisplay = ADD_VIEW_HEADER + "\n" + getAddress();
                    break;
                case 3:
                    thirdDisplay = MAIL_VIEW_HEADER + "\n" + getEmail();
                    break;
                default:
                    thirdDisplay = NUM_VIEW_HEADER + "\n" + getPhoneNumber();
                    break;
            }
        }

        return new ArrayList<>(Arrays.asList(firstDisplay, secondDisplay, thirdDisplay));
    }

    public void editCard(Spinner s1, Spinner s2, Spinner s3) {
        String selected1 = s1.getSelectedItem().toString();
        String selected2 = s2.getSelectedItem().toString();
        String selected3 = s3.getSelectedItem().toString();

        edit(selected1, selected2, selected3);
    }
}
