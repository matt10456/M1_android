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
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.CursorLoader;
import android.content.Loader;
import android.app.LoaderManager;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CITY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.STREET;
import static fr.unice.visitcardapp.Database.CONTACTS_COLUMN_1;
import static fr.unice.visitcardapp.Database.CONTACTS_COLUMN_2;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, LoaderManager.LoaderCallbacks<Cursor> {
    public int DISPLAY_CONTACT_REQUEST = 1;
    public int PICK_CONTACT_REQUEST = 2;
    public final static int QRcodeWidth = 500;
    public static final String userCard = "USER CARD";
    public static final String contactCard = "CONTACT CARD";
    static String state = userCard;
    private ZXingScannerView mScannerView;
    private RelativeLayout relativeLayout;
    TextView tName;
    TextView tView1;
    TextView tView2;
    TextView tView3;
    String displayName = "";
    String displayNumber = "";
    String displayEmail = "";
    String displayAdr = "";

    String firstDisplay;
    String secondDisplay;
    String numViewHeader = "Phone Number : ";
    String mailViewHeader = "Email : ";
    String addViewHeader = "Address : ";
    ImageView imageView;
    Bitmap bitmap;
    String textValue;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        Button editButton = (Button)findViewById(R.id.button_edit);
        Button sendButton = (Button)findViewById(R.id.button_send);
        final Button othersButton = (Button)findViewById(R.id.button_others);

        db = new Database(this);

        if (state.equals(userCard)) {
            sendButton.setVisibility(View.VISIBLE);
            othersButton.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(0, null, this);
            Uri uri = ContactsContract.Profile.CONTENT_URI;
            String[] projection = { ContactsContract.Profile.DISPLAY_NAME };
            Cursor cursor = managedQuery(uri, projection, null, null, null);

            if (cursor.moveToFirst ()) displayName = "" + cursor.getString (cursor.getColumnIndex(projection [0]));

        } else if (state.equals(contactCard)) {
            sendButton.setVisibility(View.GONE);
            othersButton.setVisibility(View.GONE);

            // Display last database value
            Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                textValue = "QRAPP:" + extras.getString("number");
                createQR(textValue);

                displayName = "" + extras.getString("name");
                displayNumber = "\n" + extras.getString("number");
                displayEmail = "\n" + extras.getString("email");
                displayAdr = "\n" + extras.getString("address");

                if(displayName.equals("null")) { displayName = " "; }
                if(displayNumber.equals("\nnull")) { displayNumber = " "; }
                if(displayEmail.equals("\nnull")) { displayEmail = " "; }
                if(displayAdr.equals("\nnull")) { displayAdr = " "; }

                Cursor rs = db.getDataByName(displayName);
                rs.moveToFirst();
                if (rs.getCount() > 0) {
                    String toDisplay1 = rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_1));
                    String toDisplay2 = rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_2));

                    // Display first
                    switch(toDisplay1) {
                        case "1":
                            firstDisplay = numViewHeader + displayNumber; break;
                        case "2":
                            firstDisplay = addViewHeader + displayAdr; break;
                        case "3" :
                            firstDisplay = mailViewHeader + displayEmail; break;
                        default :
                            firstDisplay = numViewHeader + displayNumber; break;
                    }
                    // Display second
                    switch(toDisplay2) {
                        case "1":
                            secondDisplay = numViewHeader + displayNumber; break;
                        case "2":
                            secondDisplay = addViewHeader + displayAdr; break;
                        case "3" :
                            secondDisplay = mailViewHeader + displayEmail; break;
                        default :
                            secondDisplay = numViewHeader + displayNumber; break;
                    }
                }
            }
        }

        if(firstDisplay == null){firstDisplay = "";}
        if(secondDisplay == null){secondDisplay = "";}

        tName = (TextView)findViewById(R.id.textViewName);
        tName.append(displayName);

        tView1 = (TextView)findViewById(R.id.textView1);
        tView2 = (TextView)findViewById(R.id.textView2);
        tView3 = (TextView)findViewById(R.id.textView3);

        tView1.append(firstDisplay);
        tView2.append(secondDisplay);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                i.putExtra(CreateNewCardOrEditActivity.editMode,true);
                i.putExtra("address", displayAdr.replace("\n", ""));
                i.putExtra("number", displayNumber.replace("\n", ""));
                i.putExtra("email", displayEmail.replace("\n", ""));
                i.putExtra(CreateNewCardOrEditActivity.editContent, new String[]{displayName});
                startActivity(i);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                QrScanner(relativeLayout);
            }
        });

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

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("InvalidQR",false)) {
            Snackbar snackbar = Snackbar.make(relativeLayout, R.string.invalid_QR, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

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
            Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            Uri ADDRESS_CONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            String ADDRESS_CONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
            String ADR = "";

            StringBuffer output;
            ContentResolver contentResolver = getContentResolver();
            Uri dataUri = data.getData();
            Cursor cursor = contentResolver.query(dataUri, null, null, null, null);
            // Iterate every contact in the phone
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    output = new StringBuffer();

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        output.append("\n First Name:" + name);
                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);
                        }
                        phoneCursor.close();

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();

                        // Read one address of the contact.
                        int max = 1; // Number of addresses.
                        Cursor AddressCursor = contentResolver.query(ADDRESS_CONTENT_URI, null, ADDRESS_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (AddressCursor.moveToNext() && max == 1) {
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(STREET));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(CITY));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(POSTCODE));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(COUNTRY));
                            max++;
                        }
                        AddressCursor.close();
                        ADR = ADR.replace("null", "");

                        // We have to look for the name of the contact in our database
                        Cursor rs = db.getDataByName(name);

                        rs.moveToFirst();

                        if (rs.getCount() > 0) {
                            // Case 1 : contact is found
                            state = contactCard;
                            Intent i = new Intent(getApplicationContext(), this.getClass());
                            i.putExtra("name", name);
                            i.putExtra("number", phoneNumber);
                            i.putExtra("email", email);
                            i.putExtra("address", ADR.trim());
                            startActivity(i);
                        } else {
                            // Case 2 : contact is not found
                            Snackbar snackbar = Snackbar.make(relativeLayout, R.string.card_not_found, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } else {
                        // No phone number, display an error msg
                        Snackbar snackbar = Snackbar.make(relativeLayout, R.string.no_number, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        }

        // Opens creation window for selected contact
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            String name = "";
            String email = "";

            String phoneNumber = "";
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            Uri ADDRESS_CONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            String ADDRESS_CONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
            String ADR = "";

            StringBuffer output;
            ContentResolver contentResolver = getContentResolver();
            Uri dataUri = data.getData();
            Cursor cursor = contentResolver.query(dataUri, null, null, null, null);
            // Iterate every contact in the phone
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    output = new StringBuffer();

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        output.append("\n First Name:" + name);
                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);
                        }
                        phoneCursor.close();

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();

                        // Read one address of the contact.
                        int max = 1; // Number of addresses.
                        Cursor AddressCursor = contentResolver.query(ADDRESS_CONTENT_URI, null, ADDRESS_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (AddressCursor.moveToNext() && max == 1) {
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(STREET));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(CITY));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(POSTCODE));
                            ADR += " " + AddressCursor.getString(AddressCursor.getColumnIndex(COUNTRY));
                            max++;
                        }
                        AddressCursor.close();
                        ADR = ADR.replace("null", "");
                        
                        state = userCard;
                        Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                        i.putExtra("name", name);
                        i.putExtra("number", phoneNumber);
                        i.putExtra("email", email);
                        i.putExtra("address", ADR.trim());
                        i.putExtra(CreateNewCardOrEditActivity.createMode, true);
                        i.putExtra(CreateNewCardOrEditActivity.createContent, new String[]{name});
                        startActivity(i);

                    } else {
                        // No phone number, display an error msg
                        Snackbar snackbar = Snackbar.make(relativeLayout, R.string.no_number, Snackbar.LENGTH_LONG);
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
        // Prints scan results
        // Log.e("handler", rawResult.getText());
        // Prints the scan format (qrcode)
        // Log.e("handler", rawResult.getBarcodeFormat().toString());
        String resultPrefix = null;

        // Do something with the result here
        if (rawResult.getText().length() > 6) {
            resultPrefix = rawResult.getText().substring(0,6);
        }

        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        if (resultPrefix != null && resultPrefix.equals("QRAPP:")) {
            String phoneNumber = rawResult.getText().substring(6,rawResult.getText().length());
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Sending user info", null, null);
        } else {
            // Incorrect QR code
            i.putExtra("InvalidQR",true);
        }

        mScannerView.stopCamera();
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
        return new CursorLoader(this,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(
                    ContactsContract.Profile.CONTENT_URI,
                    ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
            ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                    + ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                    + ContactsContract.Contacts.Data.MIMETYPE + "=?",
            new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        List<String> address = new ArrayList<>();
        String mime_type;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mime_type = cursor.getString(ProfileQuery.MIME_TYPE);
            switch(mime_type) {
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                    emails.add(cursor.getString(ProfileQuery.EMAIL));
                    break;
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    phones.add(cursor.getString(ProfileQuery.PHONE_NUMBER));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                    address.add(cursor.getString(ProfileQuery.ADDRESS));
                    break;
                default: break;
            }
            cursor.moveToNext();
        }
        cursor.close();

        if (phones.size() != 0 && phones.get(0) != null) {
            // If phone number exists, displays it in the view
            tView1.append(numViewHeader + "\n" + phones.get(0));
            // And sets the QR code with the text QRAPP:phonenumber
            textValue = "QRAPP:" + phones.get(0);
            createQR(textValue);
        }

        if (address.size() != 0 && address.get(0) != null) tView2.append(addViewHeader + "\n" + address.get(0));

        if (emails.size() != 0 && emails.get(0) != null) tView3.append(mailViewHeader + "\n" + emails.get(0));
    }

    public void createQR(String textValue) {
        try {
            bitmap = TextToImageEncode(textValue);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {  }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.IS_PRIMARY,
                ContactsContract.Contacts.Data.MIMETYPE
        };

        int EMAIL = 0;
        int IS_PRIMARY_EMAIL = 1;
        int PHONE_NUMBER = 2;
        int IS_PRIMARY_PHONE_NUMBER = 3;
        int ADDRESS = 4;
        int IS_PRIMARY_ADDRESS = 5;
        int MIME_TYPE = 6;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null);
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