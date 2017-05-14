package fr.unice.visitcardapp.database;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;


public interface IReadData {



    ArrayList<String> read(ContentResolver contentResolver, Uri dataUri);
}
