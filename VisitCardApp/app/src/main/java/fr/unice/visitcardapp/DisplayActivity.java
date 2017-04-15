package fr.unice.visitcardapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DisplayActivity extends AppCompatActivity {
    public int PICK_CONTACT_REQUEST = 1;
    public boolean CONTACT_FOUND = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Button displayButton1 = (Button)findViewById(R.id.button_display_1);
        displayButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CONTACT_FOUND) {
                    Intent i = new Intent(getApplicationContext(), DisplayVisitCardActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(MainActivity.cardCreated, 1);
                    startActivity(i);
                }
            }
        });

        Button displayButton2 = (Button)findViewById(R.id.button_display_2);
        displayButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,PICK_CONTACT_REQUEST);
            }
        });

    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {

            ContentResolver cr = getContentResolver();
            Uri dataUri = data.getData();
            String[] projection = { ContactsContract.Contacts._ID };
            Cursor cursor = cr.query(dataUri, projection, null, null, null);
            if ( null != cursor && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] whereParameters = new String[]{id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
                if (null != nameCur && nameCur.moveToFirst()) {
                    // Retrieves the name of the selected contact
                    String formattedName = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    Log.d("TEST",formattedName);
                }
            }

        }
    }

}
