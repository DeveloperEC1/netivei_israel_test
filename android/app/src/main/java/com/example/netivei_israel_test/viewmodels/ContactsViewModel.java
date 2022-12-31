package com.example.netivei_israel_test.viewmodels;

import static com.example.netivei_israel_test.core.Constants.CHANNEL;
import static com.example.netivei_israel_test.core.Constants.DELETE_CONTACT_TYPE;
import static com.example.netivei_israel_test.core.Constants.DELETE_CONTACT_TO_FLUTTER;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_READ_CONTACTS;
import static com.example.netivei_israel_test.core.Constants.GET_CONTACTS_TO_FLUTTER;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_WRITE_CONTACTS;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class ContactsViewModel {

    private final Activity activity;
    private FlutterEngine flutterEngine;
    private String callArgumentsStr, contactsStr, typeWriteContacts;

    public ContactsViewModel(Activity activity) {
        this.activity = activity;
    }

    public FlutterEngine getFlutterEngine() {
        return flutterEngine;
    }

    public void setFlutterEngine(FlutterEngine flutterEngine) {
        this.flutterEngine = flutterEngine;
    }

    public String getCallArgumentsStr() {
        return callArgumentsStr;
    }

    public void setCallArgumentsStr(String callArgumentsStr) {
        this.callArgumentsStr = callArgumentsStr;
    }

    public String getContactsStr() {
        return contactsStr;
    }

    public void setContactsStr(String contactsStr) {
        this.contactsStr = contactsStr;
    }

    public String getTypeWriteContacts() {
        return typeWriteContacts;
    }

    public void setTypeWriteContacts(String typeWriteContacts) {
        this.typeWriteContacts = typeWriteContacts;
    }

    public void handleGetContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_READ_CONTACTS);
            }
        } else {
            getContacts();
        }
    }

    public void getContacts() {
        setContactsStr(getContactsDataList());
        getContactsToFlutter();
    }

    public void handleDeleteContact() {
        setTypeWriteContacts(DELETE_CONTACT_TYPE);

        if (isWriteContactsGranted()) {
            if (deleteContact(getCallArgumentsStr())) {
                deleteContactToFlutter();
            }
        }
    }

    private boolean isWriteContactsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_WRITE_CONTACTS);

                return false;
            }
        }

        return true;
    }

    private void getContactsToFlutter() {
        MethodChannel methodChannel = new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL);
        methodChannel.invokeMethod(GET_CONTACTS_TO_FLUTTER, getContactsStr());
    }

    private void deleteContactToFlutter() {
        MethodChannel methodChannel = new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL);
        methodChannel.invokeMethod(DELETE_CONTACT_TO_FLUTTER, null);
    }

    private String getContactsDataList() {
        StringBuilder contactsModelString = new StringBuilder("[");

        ContentResolver cr = activity.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                if (phone != null && !phone.equals("")) {
                    contactsModelString
                            .append("{\"id\":\"").append(id).append("\",")
                            .append("\"name\":\"").append(name).append("\",")
                            .append("\"phone\":\"").append(phone).append("\"},");
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contactsModelString.substring(0, contactsModelString.length() - 1) + "]";
    }

    public void updateContact(String contactId, String newNumber) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
        String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)};

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .build());
        activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }

    public boolean deleteContact(String phoneNumber) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cur = activity.getContentResolver().query(contactUri, null, null, null, null);

        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    activity.getContentResolver().delete(uri, null, null);
                } while (cur.moveToNext());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
