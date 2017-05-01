package fr.unice.visitcardapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import static fr.unice.visitcardapp.MainActivity.contactCard;
import static fr.unice.visitcardapp.MainActivity.userCard;

public class CreateNewCardOrEditActivity extends AppCompatActivity {
    private TextView displayTextName;
    private String displayTextNameValue;
    private RelativeLayout relativeLayout;
    private Database db ;
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
        db = new Database(this);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            this.phone += extras.getString("number");
            this.address += extras.getString("address");
            this.email += extras.getString("email");

            if(extras.getString("user") != null)
            {
                Log.d("t", (extras.getString("user")));
                if(extras.getString("user").equals("user")) { this.userEdit = true; }
                else { this.userEdit = false; }
            }
        }

        s1=(Spinner)findViewById(R.id.spinner1);
        String subjects1[] ={"Phone number","Address","Email"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects1);
        s1.setAdapter(adapter1);

        s2=(Spinner)findViewById(R.id.spinner2);
        String subjects2[] ={"Phone number","Address","Email"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects2);
        s2.setAdapter(adapter2);

        final Intent i = getIntent();
        if (i.getBooleanExtra(editMode,false)) {
            // code to edit the user card
            String[] cardContents = i.getStringArrayExtra(editContent);
            displayTextName.append("" + cardContents[0]);
        }

        if (i.getBooleanExtra(createMode,false)) {
            // code to create new card for a contact
            String[] cardContents = i.getStringArrayExtra(createContent);
            displayTextName.append("" + cardContents[0]);
            // MainActivity.state = MainActivity.contactCard;
        }

        Button confirmButton = (Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTextNameValue = displayTextName.getText().length() == 0 ? "" : displayTextName.getText().toString();
                String selected1 = s1.getSelectedItem().toString();
                String selected2 = s2.getSelectedItem().toString();
                String s1, s2;

                switch(selected1) {
                    case "Phone number":
                        s1 = "1"; break;
                    case "Address" :
                        s1 = "2"; break;
                    case "Email" :
                        s1  ="3"; break;
                    default :
                        s1 = "1"; break;
                }

                switch(selected2) {
                    case "Phone number":
                        s2 = "1"; break;
                    case "Address" :
                        s2 = "2"; break;
                    case "Email" :
                        s2  ="3"; break;
                    default :
                        s2 = "1"; break;
                }

                if(s1.equals(s2)) {
                    // Même affichage = erreur
                    Snackbar snackbar = Snackbar.make(relativeLayout, R.string.sameDisplay, Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    // DB insertion
                    if (db.insertContact(displayTextNameValue, s1, s2)) {
                        // Insertion ok

                        // Récupération des paramètres
                        if(!userEdit) {
                            MainActivity.state = contactCard;
                        }
                        else
                        {
                            MainActivity.state = userCard;
                        }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("address", address);
                        i.putExtra("number", phone);
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
                MainActivity.state = userCard;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}