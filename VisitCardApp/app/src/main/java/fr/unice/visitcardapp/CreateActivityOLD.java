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

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CreateActivityOLD extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public int PICK_CONTACT_REQUEST = 1;
    private RelativeLayout relativeLayout;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_old);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_create);
        db = new Database(this);

        Button myCardButton = (Button) findViewById(R.id.button_create_2);
        myCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            // Verify if contact exist in Database.
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
                    // Search in Database.
                    Cursor rs = db.getDataByName(formattedName);

                    rs.moveToFirst();

                    // Case 1 : contact already has a visit card
                    if (rs.getCount() > 0) {
                        Snackbar snackbar = Snackbar.make(relativeLayout, R.string.card_already_exists, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    // Case 2 : We have to create visit card for the contact
                    // else {
                    //
                    // }
                }
            }
        }
    }

    public void QrScanner(View view){
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        // Register ourselves as a handler for scan results
        mScannerView.setResultHandler(this);
        // Start camera
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        if (mScannerView != null) mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        // Prints scan results
        Log.e("handler", rawResult.getText());
        // Prints the scan format (qrcode)
        Log.e("handler", rawResult.getBarcodeFormat().toString());

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

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
                Intent intent = new Intent(CreateActivityOLD.this, MainActivityOLD.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
