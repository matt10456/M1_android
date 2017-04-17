package fr.unice.visitcardapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class DisplayVisitCardActivity extends AppCompatActivity {
    public final static int QRcodeWidth = 500;
    private Database db;
    ImageView imageView;
    Bitmap bitmap;
    // The following string should be created according to the previous activity's output
    String textValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);

        db = new Database(this);

        imageView = (ImageView)findViewById(R.id.imageView);

        Button editButton = (Button)findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                startActivity(i);
            }
        });

        // Cursor.
        Cursor rs;
        // Display last database value.
        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
            String Extraname = extras.getString("name");
            // Found a visit card to display.
             rs = db.getDataByName(Extraname);
        }
        else
        {
            // Show the last.
            rs = db.getLastContact();
        }

        rs.moveToFirst();

        String name = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_NAME));
        String surname = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_SURNAME));
        String job = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_JOB));
        String phone = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_PHONE));
        String mail = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_MAIL));
        String website = rs.getString(rs.getColumnIndex(db.CONTACTS_COLUMN_WEBSITE));

        TextView tName = (TextView)findViewById(R.id.textViewName);
        tName.append("\n" + name);

        TextView tSurname = (TextView)findViewById(R.id.textViewSurname);
        tSurname.append("\n" + surname);

        TextView tJob = (TextView)findViewById(R.id.textViewJob);
        tJob.append("\n" + job);

        TextView tPhone = (TextView)findViewById(R.id.textViewPhone);
        tPhone.append("\n" + phone);

        TextView tMail = (TextView)findViewById(R.id.textViewMail);
        tMail.append("\n" + mail);

        TextView tWebsite = (TextView)findViewById(R.id.textViewWebsite);
        tWebsite.append("\n" + website);


        textValue = "QRAPP:name="+name+",surname="+surname+",job="+job+",phone="+phone+",mail="+mail+",website="+website+"";

        try {
            bitmap = TextToImageEncode(textValue);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
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
                Intent intent = new Intent(DisplayVisitCardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

