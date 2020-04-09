package com.example.android.myswitchingscreenwithobjectsapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;

import static com.example.android.myswitchingscreenwithobjectsapp.Constants.READ_CONTACTS_PERMISSION_CODE;
import static com.example.android.myswitchingscreenwithobjectsapp.Constants.REQUEST_CODE_PICK_CONTACTS;
import static com.example.android.myswitchingscreenwithobjectsapp.Constants.REQUEST_CODE_SECOND_SCREEN;

public class MainActivity extends AppCompatActivity {
    TextView bombNameTextView;
    ImageButton contactImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch destroyTheWorldSwitch = findViewById(R.id.destroyTheWorldSwitch);
        final TextView messageView = findViewById(R.id.messageTextView);
        bombNameTextView = findViewById(R.id.bombNameTextView);

        destroyTheWorldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    messageView.setText("mmm...Maybe Later");
                } else {
                    messageView.setText("You have saved the world");
                }
            }
        });

        /**
         * THIS IS TO MAKE THE CONTACTS BUTTON WORK
         * For this we have to access the contact database in the phone, so a special permission is needed:
         * add to your AndroidManifest.xml this:
         *      <uses-permission android:name="android.permission.READ_CONTACTS" />
         * If you test it in your emulator or phone you will get an error as if you didn't granted this permission to the app.
         * and it's true, remember that you grant permissions to the app when you install it, and as we are testing it, the
         * installation step is skipped.
         **/
        contactImageButton = findViewById(R.id.contactImageButton);

        contactImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasReadContactsPermission()){
                    showRequestPermissionsInfoAlertDialog();
                }else
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
    }


    public void buildBomb(View view) {
        //build a bomb, ask to build a new activity(with screen) and send the bomb
        Bomb bomb = new Bomb();

        if (bombNameTextView.getText().toString().trim().isEmpty()) {
            //using a Snackbar, similar to Toast it shows a message to the user, but the former one can be dismissed by the user.
            //it's recommended to use Snackbar.
            Snackbar.make(view, "Please name your bomb", Snackbar.LENGTH_SHORT).show();
            return;
        }

        bomb.setName(bombNameTextView.getText().toString());

        Intent nextScreen = new Intent(this, SecondActivity.class);

        nextScreen.putExtra(Bomb.BOMB_KEY, bomb);

        startActivityForResult(nextScreen, REQUEST_CODE_SECOND_SCREEN);

    }

    // to convert svg files into xml
    // http://inloop.github.io/svg2android/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SECOND_SCREEN) {
            if (resultCode == RESULT_OK) { //if this is true, the secondActivity closed properly, it did not crashed.
                ImageView imageView = findViewById(R.id.currentBombImageView);
                Bomb bomb = (Bomb) data.getSerializableExtra(Bomb.BOMB_KEY);

                imageView.setImageResource(bomb.getResId());

                Switch destroyTheWorldSwitch = findViewById(R.id.destroyTheWorldSwitch);
                destroyTheWorldSwitch.setEnabled(true);

            } else if (resultCode == RESULT_CANCELED) { // the app secondActivity crashed or you forgot to use setResult() method before calling finish()
                Toast.makeText(this, "maybe you forgot to set the data back, check your child setResult() ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_PICK_CONTACTS) {
            if (resultCode == RESULT_OK) {
                Uri contact = data.getData();
                String name = retrieveContactName(contact);
                bombNameTextView.setText(name);
                retrieveContactNumber(contact);
                // find more info here:
                // https://developer.android.com/reference/android/content/Intent#ACTION_VIEW
            }
        } else {
            Toast.makeText(this, "wrong request code come back? requestCode: " + requestCode, Toast.LENGTH_SHORT).show();
        }

    }


    private boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

    }

    private void showRequestPermissionsInfoAlertDialog() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.permission_alert_dialog_title);
            builder.setMessage(R.string.permission_dialog_message);
            builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestReadContactsPermission();
                }
            });
            builder.show();
        }else{
            // No explanation needed; request the permission
            requestReadContactsPermission();
        }
    }

    private void requestReadContactsPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_CODE);
    }

    private String retrieveContactName(Uri uriContact) {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

//        Log.d(TAG, "Contact Name: " + contactName);
        return contactName;
    }

    private String retrieveContactNumber(Uri uriContact) {
        String contactID = null;
        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d("ABC", "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d("ABC", "Contact Phone Number: " + contactNumber);
        return contactID;
    }

    private void retrieveContactPhoto(String contactID) {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);

                /***
                 * Here uncomment these 2 lines and place an imageView in your xml file, to see the picture of the contact :)
                 */
//                ImageView imageView = (ImageView) findViewById(R.id.img_contact);
//                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
