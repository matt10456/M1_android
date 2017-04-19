package fr.unice.visitcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateNewCardOrEditActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextJob;
    private EditText editTextPhone;
    private EditText editTextMail;
    private EditText editTextWebsite;
    private String editTextNameValue;
    private String editTextSurnameValue;
    private String editTextJobValue;
    private String editTextPhoneValue;
    private String editTextMailValue;
    private String editTextWebsiteValue;
    private Database db ;
    static String createMode = "CREATE MODE";
    static String editMode = "EDIT MODE";
    static String editContent = "EDIT CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);

        db = new Database(this);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextSurname = (EditText)findViewById(R.id.editTextSurname);
        editTextJob = (EditText)findViewById(R.id.editTextJob);
        editTextPhone = (EditText)findViewById(R.id.editTextPhone);
        editTextMail = (EditText)findViewById(R.id.editTextEmail);
        editTextWebsite = (EditText)findViewById(R.id.editTextWebsite);

        Intent i = getIntent();
        if (i.getBooleanExtra(editMode,false)) {
            String[] cardContents = i.getStringArrayExtra(editContent);
            editTextName.setText(cardContents[0], TextView.BufferType.EDITABLE);
            //editTextSurname.setText(cardContents[1], TextView.BufferType.EDITABLE);
        }

        if (i.getBooleanExtra(createMode,false)) {
            // code to create new card for a contact
        }

        Button confirmButton = (Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptyField = "";

                editTextNameValue = editTextName.getText().length() == 0 ? emptyField : editTextName.getText().toString();
                editTextSurnameValue = editTextSurname.getText().length() == 0 ? emptyField : editTextSurname.getText().toString();
                editTextJobValue = editTextJob.getText().length() == 0 ? emptyField : editTextJob.getText().toString();
                editTextPhoneValue = editTextPhone.getText().length() == 0 ? emptyField : editTextPhone.getText().toString();
                editTextMailValue = editTextMail.getText().length() == 0 ? emptyField : editTextMail.getText().toString();
                editTextWebsiteValue = editTextWebsite.getText().length() == 0 ? emptyField : editTextWebsite.getText().toString();

                Log.d("TEST", "1 " + editTextNameValue + " 2 " + editTextSurnameValue + " 3 " + editTextJobValue + " 4 " +
                        editTextPhoneValue + " 5 " + editTextMailValue + " 6 " + editTextWebsiteValue);

                // DB insertion
                if (db.insertContact(editTextNameValue, "1", "1")) {
                    // Rechercher si le nom existe deja si oui erreur
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}