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
    ImageView imageView;
    Bitmap bitmap;
    String textValue;
    Cursor rs;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);
        imageView = (ImageView)findViewById(R.id.imageView);

        db = new Database(this);

        // Display last database value.
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            String extraName = extras.getString("name");
            // Found a visit card to display.
             rs = db.getDataByName(extraName);
        } else {
            rs = db.getLastContact();
        }

        rs.moveToFirst();

        final String name = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_NAME));
        final String surname = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_SURNAME));
        final String job = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_JOB));
        final String phone = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_PHONE));
        final String mail = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_MAIL));
        final String website = rs.getString(rs.getColumnIndex(Database.CONTACTS_COLUMN_WEBSITE));

        TextView tName = (TextView)findViewById(R.id.textViewName);
        tName.append(name);

        TextView tSurname = (TextView)findViewById(R.id.textViewSurname);
        tSurname.append(" " + surname);

        TextView tJob = (TextView)findViewById(R.id.textViewJob);
        tJob.append("Job : " + job);

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

        Button editButton = (Button)findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateNewCardOrEditActivity.class);
                i.putExtra(CreateNewCardOrEditActivity.editMode,true);
                i.putExtra(CreateNewCardOrEditActivity.editContent, new String[]{name,surname,job,phone,mail,website});
                startActivity(i);
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

