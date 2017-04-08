package fr.unice.visitcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Button myCardButton = (Button)findViewById(R.id.button_create_1);
        myCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                startActivity(i);
            }
        });

        Button otherCardActivity = (Button)findViewById(R.id.button_create_2);
        otherCardActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateFromQRActivity.class);
                startActivity(i);
            }
        });
    }

}
