package fr.unice.visitcardapp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    public int PICK_CONTACT_REQUEST = 1;
    static String cardCreated = "CREATED CARD CHECK";
    private RelativeLayout relativeLayout;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

        Button createButton = (Button)findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateActivity.class);
                startActivity(i);
            }
        });

        Button displayButton = (Button)findViewById(R.id.button_display);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                startActivity(i);
            }
        });

        Button sendButton = (Button)findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,PICK_CONTACT_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        int notCreated = i.getIntExtra(cardCreated, 0);
        if (notCreated == 1) {
            Snackbar snackbar = Snackbar.make(relativeLayout, R.string.create_card_first, Snackbar.LENGTH_LONG);
            snackbar.show();
            getIntent().removeExtra(cardCreated);
        }
    }

}
