package fr.unice.visitcardapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public int DISPLAY_CONTACT_REQUEST = 1;
    public int SEND_CONTACT_REQUEST = 2;
    public int PICK_CONTACT_REQUEST = 3;
    public final static int QRcodeWidth = 500;
    public static final String userCard = "USER CARD";
    public static final String contactCard = "CONTACT CARD";
    static String state = userCard;
    private ZXingScannerView mScannerView;
    private RelativeLayout relativeLayout;
    String displayName = "";
    String displayNumber = "";
    String displayEmail = "";
    ImageView imageView;
    Bitmap bitmap;
    String textValue;
    Cursor rs;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

        db = new Database(this);

        Log.d("STATE",state);
        if (state.equals(userCard)) {
            Uri uri = ContactsContract.Profile.CONTENT_URI;
            String[] projection = { ContactsContract.Profile.DISPLAY_NAME };

            Cursor cursor = managedQuery(uri, projection, null, null, null);

            if (cursor.moveToFirst ()) {
                displayName = cursor.getString (cursor.getColumnIndex (projection [0]));
            }
            displayNumber = "";
            displayEmail = "";
        } else if (state.equals(contactCard)) {
            // Display last database value.
            Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                displayName = "" + extras.getString("name");
                displayNumber = "\n" + extras.getString("number");
                displayEmail = "" + extras.getString("email");

                if(displayName.equals("null")) { displayName = "Name"; }
                if(displayNumber.equals("\n")) { displayNumber = " "; }
                if(displayEmail.equals("null")) { displayEmail = " "; }

                // Found a visit card to display.
                // rs = db.getDataByName(extraName);
            }
        }

        //rs.moveToFirst();

        //final String name = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_NAME));

        TextView tName = (TextView)findViewById(R.id.textViewName);
        tName.append(displayName);

        TextView tNum = (TextView)findViewById(R.id.textViewPhone);
        tNum.append(displayNumber);

        TextView tEmail = (TextView)findViewById(R.id.textViewMail);
        tEmail.append(displayEmail);

        textValue = "QRAPP:name="+"name"+",surname=surname,job=job,phone=phone,mail=mail,website=website";

        try {
            bitmap = TextToImageEncode(textValue);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Button editButton = (Button)findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                i.putExtra(CreateNewCardOrEditActivity.editMode,true);
                i.putExtra(CreateNewCardOrEditActivity.editContent, new String[]{displayName});
                startActivity(i);
            }
        });

        Button sendButton = (Button)findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,SEND_CONTACT_REQUEST);
            }
        });

        final Button othersButton = (Button)findViewById(R.id.button_display_others);
        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, othersButton);
                // Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.others_menu, popup.getMenu());

                // Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.create_from_contact:
                                Intent intent1 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent1,PICK_CONTACT_REQUEST);
                                return true;
                            case R.id.create_qr:
                                QrScanner(relativeLayout);
                                return true;
                            case R.id.display:
                                Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent2,DISPLAY_CONTACT_REQUEST);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {

        // Displays the contact's card if it exists
        if (resultCode == Activity.RESULT_OK && requestCode == DISPLAY_CONTACT_REQUEST) {
            String name = "";
            String email = "";

            String phoneNumber = null;
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            StringBuffer output;
            ContentResolver contentResolver = getContentResolver();
            Uri dataUri = data.getData();
            Cursor cursor = contentResolver.query(dataUri, null, null, null, null);
            // Iterate every contact in the phone
            if (cursor.getCount() > 0) {
                int counter = 0;
                while (cursor.moveToNext()) {
                    output = new StringBuffer();
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        output.append("\n First Name:" + name);
                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);
                        }
                        phoneCursor.close();
                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();
                    }
                }
            }
            // We have to look for the name of the contact in our database
            Cursor rs = db.getDataByName(name);

            rs.moveToFirst();

            if (rs.getCount() > 0) {
                // Case 1 : contact is found
                state = contactCard;
                Intent i = new Intent(getApplicationContext(), this.getClass());
                i.putExtra("name", name);
                i.putExtra("number", phoneNumber);
                i.putExtra("email",email);
                startActivity(i);
            } else {
                // Case 2 : contact is not found
                Snackbar snackbar = Snackbar.make(relativeLayout, R.string.card_not_found, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        // Sends SMS to selected contact
        if (resultCode == Activity.RESULT_OK && requestCode == SEND_CONTACT_REQUEST) {

        }

        // Opens creation window for selected contact
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
                    Log.d("TEST", formattedName);

                    Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                    i.putExtra(CreateNewCardOrEditActivity.createMode,true);
                    i.putExtra(CreateNewCardOrEditActivity.createContent, new String[]{formattedName});
                    startActivity(i);
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
                state = userCard;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        mScannerView.stopCamera();
        // Show the card that got taken by QR
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}