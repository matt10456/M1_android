package fr.unice.visitcardapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CreateActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

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

        Intent i = new Intent(getApplicationContext(), CardCreatedActivity.class);
        startActivity(i);

        // show the scanner result into dialog box.
        // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("Scan Result");
        // builder.setMessage(rawResult.getText());
        // AlertDialog alert1 = builder.create();
        // alert1.show();

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

}
