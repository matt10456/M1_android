package fr.unice.visitcardapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DisplayActivityOLD extends AppCompatActivity {
    public int PICK_CONTACT_REQUEST = 1;
    public boolean CONTACT_FOUND = false;
    private RelativeLayout relativeLayout;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_old);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_display);

        db = new Database(this);

        Button displayButton1 = (Button)findViewById(R.id.button_display_1);
        displayButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (CONTACT_FOUND) {
                //    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                //    startActivity(i);
                //} else {
                //    Intent i = new Intent(getApplicationContext(), MainActivityOLD.class);
                //    i.putExtra(MainActivityOLD.cardCreated, 1);
                //    startActivity(i);
                //}
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

                    // We have to look for the name of the contact in our database
                    Cursor rs = db.getDataByName(formattedName);

                    rs.moveToFirst();

                    if (rs.getCount() > 0) {
                        // Case 1 : contact is not found (default)
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("name", rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_NAME)));
                        startActivity(i);
                    } else {
                        // Case 2 : contact is found
                        Snackbar snackbar = Snackbar.make(relativeLayout, R.string.card_not_found, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        }
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
                Intent intent = new Intent(DisplayActivityOLD.this, MainActivityOLD.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
