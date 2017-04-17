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

        Button confirmButton = (Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptyField = "None";

                editTextNameValue = editTextName.getText().length() == 0 ? "" : editTextName.getText().toString();
                editTextSurnameValue = editTextSurname.getText().length() == 0 ? "" : editTextSurname.getText().toString();
                editTextJobValue = editTextJob.getText().length() == 0 ? "" : editTextJob.getText().toString();
                editTextPhoneValue = editTextPhone.getText().length() == 0 ? emptyField : editTextPhone.getText().toString();
                editTextMailValue = editTextMail.getText().length() == 0 ? emptyField : editTextMail.getText().toString();
                editTextWebsiteValue = editTextWebsite.getText().length() == 0 ? emptyField : editTextWebsite.getText().toString();

                Log.d("TEST", "1 " + editTextNameValue + " 2 " + editTextSurnameValue + " 3 " + editTextJobValue + " 4 " +
                        editTextPhoneValue + " 5 " + editTextMailValue + " 6 " + editTextWebsiteValue);

                // DB INSERTION
                if (db.insertContact(editTextNameValue, editTextSurnameValue,
                        editTextJobValue, editTextPhoneValue,
                        editTextMailValue, editTextWebsiteValue)) {
                    // Rechercher si le nom existe deja si oui erreur
                    Intent i = new Intent(getApplicationContext(), CardCreatedActivity.class);
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
                Intent intent = new Intent(CreateNewCardOrEditActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
