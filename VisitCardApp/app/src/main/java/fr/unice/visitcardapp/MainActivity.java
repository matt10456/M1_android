package fr.unice.visitcardapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.telephony.SmsManager;
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
import android.content.CursorLoader;
import android.content.Loader;
import android.app.LoaderManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.unice.visitcardapp.communication.AbstractCommunication;
import fr.unice.visitcardapp.communication.AndroidCommunication;
import fr.unice.visitcardapp.database.Database;
import fr.unice.visitcardapp.visitcard.AbstractVisitCard;
import fr.unice.visitcardapp.visitcard.AndroidVisitCard;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CITY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;
import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.STREET;
import static fr.unice.visitcardapp.database.Database.CONTACTS_COLUMN_1;
import static fr.unice.visitcardapp.database.Database.CONTACTS_COLUMN_2;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String USER_CARD = AbstractVisitCard.USER_CARD;
    public static final String CONTACT_CARD = AbstractVisitCard.CONTACT_CARD;
    public int DISPLAY_CONTACT_REQUEST = 1;
    public int PICK_CONTACT_REQUEST = 2;
    public final static int QR_CODE_WIDTH = 500;
    public static AndroidVisitCard userCard = new AndroidVisitCard(true);
    public static AndroidVisitCard contactCard = new AndroidVisitCard(false);
    public static AndroidVisitCard staticCard = new AndroidVisitCard();
    AndroidCommunication com = new AndroidCommunication();
    static String state = USER_CARD;
    private ZXingScannerView mScannerView;
    private RelativeLayout relativeLayout;
    boolean profileCreation = false;
    TextView tName, tView1, tView2, tView3;
    Button editButton, sendButton, profileButton;
    String displayName = "";
    String displayNumber = "";
    String displayEmail = "";
    String displayAdr = "";
    String firstDisplay, secondDisplay;
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
        editButton = (Button)findViewById(R.id.button_edit);
        sendButton = (Button)findViewById(R.id.button_send);
        profileButton = (Button)findViewById(R.id.button_user_profile);
        final Button othersButton = (Button)findViewById(R.id.button_others);
        tView1 = (TextView)findViewById(R.id.textView1);
        tView2 = (TextView)findViewById(R.id.textView2);
        tView3 = (TextView)findViewById(R.id.textView3);
        tName = (TextView)findViewById(R.id.textViewName);

        db = new Database(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED  ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS},
                    2);

            tName.append("Give the rights and click \n Back to my card to restart.");
        } else {
            if (state.equals(USER_CARD)) {
                sendButton.setVisibility(View.VISIBLE);
                othersButton.setVisibility(View.VISIBLE);
                profileButton.setVisibility(View.GONE);
                getLoaderManager().initLoader(0, null, this);
                Uri uri = ContactsContract.Profile.CONTENT_URI;
                String[] projection = {ContactsContract.Profile.DISPLAY_NAME};
                Cursor cursor = managedQuery(uri, projection, null, null, null);

                if (cursor.moveToFirst()) {
                    displayName = "" + cursor.getString(cursor.getColumnIndex(projection[0]));
                    userCard.setFullName(displayName);
                    Cursor rs = db.getDataByName(displayName);
                    rs.moveToFirst();
                    if (rs.getCount() > 0) {
                        userCard.setFirstUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_1))));
                        Log.d("test", rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_1)));
                        userCard.setSecondUserChoice(Integer.parseInt(rs.getString(rs.getColumnIndex(CONTACTS_COLUMN_2))));
                    }
                }
            } else if (state.equals(CONTACT_CARD)) {
                othersButton.setVisibility(View.GONE);
                sendButton.setVisibility(View.GONE);
                profileButton.setVisibility(View.GONE);

                // Display last database value
                Bundle extras = this.getIntent().getExtras();
                if (extras != null) {
                    textValue = AndroidCommunication.ACCEPTED_PREFIX + extras.getString("number");
                    createQR(textValue);
                    ArrayList<String> contactDisplay = contactCard.displayContactInfo(new ArrayList<>(Arrays.asList(extras.getString("name"), extras.getString("number"),
                            extras.getString("address"), extras.getString("email"))), db);

                    displayName = "" + extras.getString("name");
                    firstDisplay = contactDisplay.get(0);
                    secondDisplay = contactDisplay.get(1);
                }

                if (firstDisplay == null) {
                    firstDisplay = "";
                }
                if (secondDisplay == null) {
                    secondDisplay = "";
                }

                tView1.append(firstDisplay);
                tView2.append(secondDisplay);
                tName.append(displayName);
            }

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), InfoChoiceActivity.class);
                    i.putExtra(InfoChoiceActivity.editMode, true);
                    if (state.equals(USER_CARD)) {
                        // Signal that we want to edit the user card.
                        i.putExtra("user", "user");
                    } else {
                        i.putExtra("user", "contact");
                    }
                    if (state.equals(USER_CARD)) {
                        i.putExtra("number", userCard.getPhoneNumber());
                        i.putExtra("address", userCard.getAddress());
                        i.putExtra("email", userCard.getEmail());
                    } else if (state.equals(CONTACT_CARD)) {
                        i.putExtra("number", contactCard.getPhoneNumber());
                        i.putExtra("address", contactCard.getAddress());
                        i.putExtra("email", contactCard.getEmail());
                    }
                    i.putExtra(InfoChoiceActivity.editContent, new String[]{displayName});
                    startActivity(i);
                }
            });

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (displayName.equals("") || userCard.getPhoneNumber() == null || userCard.getPhoneNumber().equals("")) {
                        Snackbar snackbar = Snackbar.make(relativeLayout, R.string.sending_card_error, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        QrScanner(relativeLayout);
                    }
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
                                    startActivityForResult(intent1, PICK_CONTACT_REQUEST);
                                    return true;
                                case R.id.display:
                                    Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                    startActivityForResult(intent2, DISPLAY_CONTACT_REQUEST);
                                    return true;
                                default:
                                    return true;
                            }
                        }
                    });
                    popup.show();
                }
            });

            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("InvalidQR", false)) {
                Snackbar snackbar = Snackbar.make(relativeLayout, R.string.invalid_QR, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
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

            Uri ADDRESS_CONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            String ADDRESS_CONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
            String ADR = "";

            Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

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

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();

                        // We have to look for the name of the contact in our database
                        Cursor rs = db.getDataByName(name);

                        rs.moveToFirst();

                        if (rs.getCount() > 0) {
                            // Case 1 : contact is found
                            state = CONTACT_CARD;
                            Intent i = new Intent(getApplicationContext(), this.getClass());
                            i.putExtra("name", name);
                            i.putExtra("number", phoneNumber);
                            i.putExtra("address", ADR.trim());
                            i.putExtra("email", email);
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

            Uri ADDRESS_CONTENT_URI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            String ADDRESS_CONTACT_ID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
            String ADR = "";

            Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

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
                        // This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);
                        }
                        phoneCursor.close();

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

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();

                        state = USER_CARD;
                        Intent i = new Intent(getApplicationContext(), InfoChoiceActivity.class);
                        i.putExtra("name", name);
                        i.putExtra("number", phoneNumber);
                        i.putExtra("address", ADR.trim());
                        i.putExtra("email", email);
                        i.putExtra(InfoChoiceActivity.createMode, true);
                        i.putExtra(InfoChoiceActivity.createContent, new String[]{name});
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
                state = USER_CARD;
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

    public void createProfile(View view) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
        if (launchIntent != null) {
            profileCreation = true;
            startActivity(launchIntent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        if (mScannerView != null) mScannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileCreation) {
            profileCreation = false;
            state = USER_CARD;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScannerView != null) mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        state = USER_CARD;
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = com.sendSMS(this, rawResult, AndroidCommunication.SENT_PREFIX +";"+ userCard.getFirstUserChoice()+";"+
                userCard.getSecondUserChoice()+";"+tName.getText().toString()+";"+userCard.getPhoneNumber()+";"+userCard.getAddress()+";"+userCard.getEmail());
        mScannerView.stopCamera();
        startActivity(intent);
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
        ArrayList<String> emails = new ArrayList<>();
        ArrayList<String> phones = new ArrayList<>();
        ArrayList<String> addresses = new ArrayList<>();
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
                    addresses.add(cursor.getString(ProfileQuery.ADDRESS));
                    break;
                default: break;
            }
            cursor.moveToNext();
        }
        cursor.close();

        userCard.displayUserInfo(phones, addresses, emails, tView1, tView2);

        // If the user doesn't have his or her profile set up yet
        if (displayName.equals("") || userCard.getPhoneNumber() == null || userCard.getPhoneNumber().equals("")) {
            tName.append("To use your card, enter\nyour username and number");
            profileButton.setVisibility(View.VISIBLE);
        } else {
            tName.append(displayName);
        }

        // Sets the QR code with the adapted text for communication
        textValue = AndroidCommunication.ACCEPTED_PREFIX + userCard.getPhoneNumber();
        createQR(textValue);
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
                    QR_CODE_WIDTH, QR_CODE_WIDTH, null);
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

        bitmap.setPixels(pixels, 0, QR_CODE_WIDTH, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}