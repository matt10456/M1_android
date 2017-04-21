package fr.unice.visitcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CreateNewCardOrEditActivity extends AppCompatActivity {
    private TextView editTextName;
    private String editTextNameValue;
    private Database db ;
    static String editMode = "EDIT MODE";
    static String createMode = "CREATE MODE";
    static String editContent = "EDIT CONTENT";
    static String createContent = "EDIT CONTENT";
    Spinner s1, s2, s3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);

        db = new Database(this);

        editTextName = (TextView) findViewById(R.id.textViewName);

        s1=(Spinner)findViewById(R.id.spinner1);
        String subjects1[] ={"Phone number","Address","Email"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects1);
        s1.setAdapter(adapter1);

        s2=(Spinner)findViewById(R.id.spinner2);
        String subjects2[] ={"Phone number","Address","Email"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects2);
        s2.setAdapter(adapter2);

        s3=(Spinner)findViewById(R.id.spinner3);
        String subjects3[] ={"Phone number","Address","Email"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,subjects3);
        s3.setAdapter(adapter3);

        Intent i = getIntent();
        if (i.getBooleanExtra(editMode,false)) {
            // code to edit the user card
            String[] cardContents = i.getStringArrayExtra(editContent);
            editTextName.append("" + cardContents[0]);
        }

        if (i.getBooleanExtra(createMode,false)) {
            // code to create new card for a contact
            String[] cardContents = i.getStringArrayExtra(createContent);
            editTextName.append("" + cardContents[0]);
        }

        Button confirmButton = (Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptyField = "";

                editTextNameValue = editTextName.getText().length() == 0 ? emptyField : editTextName.getText().toString();

                //Log.d("TEST", "1 " + editTextNameValue + " 2 " + editTextSurnameValue + " 3 " + editTextJobValue + " 4 " +
                //        editTextPhoneValue + " 5 " + editTextMailValue + " 6 " + editTextWebsiteValue);

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
                MainActivity.state = MainActivity.userCard;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}