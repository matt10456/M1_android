package fr.unice.visitcardapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import fr.unice.visitcardapp.database.SQLDatabase;
import fr.unice.visitcardapp.visitcard.AndroidVisitCard;

import static fr.unice.visitcardapp.MainActivity.CONTACT_CARD;
import static fr.unice.visitcardapp.MainActivity.USER_CARD;
import static fr.unice.visitcardapp.MainActivity.staticCard;

public class InfoChoiceActivity extends AppCompatActivity {
    private TextView displayTextName;
    private String displayTextNameValue;
    private RelativeLayout relativeLayout;
    private SQLDatabase db ;
    static String editMode = "EDIT MODE";
    static String createMode = "CREATE MODE";
    static String editContent = "EDIT CONTENT";
    static String createContent = "EDIT CONTENT";
    Spinner s1, s2, s3;
    String address = "";
    String phone = "";
    String email = "";
    boolean userEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_create_new);
        displayTextName = (TextView) findViewById(R.id.textViewName);
        db = new SQLDatabase(this);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            this.phone += extras.getString("number");
            this.address += extras.getString("address");
            this.email += extras.getString("email");

            if(extras.getString("user") != null) this.userEdit = extras.getString("user").equals("user");
        }

        s1=(Spinner)findViewById(R.id.spinner1);
        String subjects1[] ={AndroidVisitCard.NUM_SPINNER_CHOICE,AndroidVisitCard.ADD_SPINNER_CHOICE,AndroidVisitCard.MAIL_SPINNER_CHOICE};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects1);
        s1.setAdapter(adapter1);

        s2=(Spinner)findViewById(R.id.spinner2);
        String subjects2[] ={AndroidVisitCard.NUM_SPINNER_CHOICE,AndroidVisitCard.ADD_SPINNER_CHOICE,AndroidVisitCard.MAIL_SPINNER_CHOICE};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects2);
        s2.setAdapter(adapter2);

        s3=(Spinner)findViewById(R.id.spinner3);
        String subjects3[] ={AndroidVisitCard.NUM_SPINNER_CHOICE,AndroidVisitCard.ADD_SPINNER_CHOICE,AndroidVisitCard.MAIL_SPINNER_CHOICE};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects3);
        s3.setAdapter(adapter3);

        final Intent i = getIntent();
        if (i.getBooleanExtra(editMode,false)) {
            String[] cardContents = i.getStringArrayExtra(editContent);
            displayTextName.append("" + cardContents[0]);
        }

        if (i.getBooleanExtra(createMode,false)) {
            String[] cardContents = i.getStringArrayExtra(createContent);
            displayTextName.append("" + cardContents[0]);
        }

        Button confirmButton = (Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTextNameValue = displayTextName.getText().length() == 0 ? "" : displayTextName.getText().toString();
                // Static card is used here because the change can apply
                // to both user and contact card
                staticCard.editCard(s1,s2,s3);
                String sVal1 = Integer.toString(staticCard.getFirstUserChoice());
                String sVal2 = Integer.toString(staticCard.getSecondUserChoice());
                String sVal3 = Integer.toString(staticCard.getThirdUserChoice());

                if(sVal1.equals(sVal2) || sVal1.equals(sVal3) || sVal2.equals(sVal3)) {
                    // Same display = error
                    Snackbar snackbar = Snackbar.make(relativeLayout, R.string.sameDisplay, Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    // DB insertion
                    if (db.insertContact(displayTextNameValue, sVal1, sVal2, sVal3)) {
                        // Parameter retrieval
                        if(!userEdit) {
                            MainActivity.state = CONTACT_CARD;
                        } else {
                            MainActivity.state = USER_CARD;
                        }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("number", phone);
                        i.putExtra("address", address);
                        i.putExtra("email", email);
                        i.putExtra("name", displayTextNameValue);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back_menu:
                MainActivity.state = USER_CARD;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}