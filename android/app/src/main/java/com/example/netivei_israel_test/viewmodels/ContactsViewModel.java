package com.example.netivei_israel_test.viewmodels;

import static com.example.netivei_israel_test.core.Constants.ADD_CONTACT_TYPE;
import static com.example.netivei_israel_test.core.Constants.BACK_TO_CONTACTS_LIST_TO_FLUTTER;
import static com.example.netivei_israel_test.core.Constants.CHANNEL;
import static com.example.netivei_israel_test.core.Constants.DELETE_CONTACT_TO_FLUTTER;
import static com.example.netivei_israel_test.core.Constants.DELETE_CONTACT_TYPE;
import static com.example.netivei_israel_test.core.Constants.GET_CONTACTS_TO_FLUTTER;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_READ_CONTACTS;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_WRITE_CONTACTS;
import static com.example.netivei_israel_test.core.Constants.UPDATE_CONTACT_TYPE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class ContactsViewModel {

    private final Activity activity;
    private FlutterEngine flutterEngine;
    private String contactsStr, typeWriteContacts, currentPhoneArgument, nameArgument, phoneArgument;

    public ContactsViewModel(Activity activity) {
        this.activity = activity;
    }

    public FlutterEngine getFlutterEngine() {
        return flutterEngine;
    }

    public void setFlutterEngine(FlutterEngine flutterEngine) {
        this.flutterEngine = flutterEngine;
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

    public String getCurrentPhoneArgument() {
        return currentPhoneArgument;
    }

    public void setCurrentPhoneArgument(String currentPhoneArgument) {
        this.currentPhoneArgument = currentPhoneArgument;
    }

    public String getNameArgument() {
        return nameArgument;
    }

    public void setNameArgument(String nameArgument) {
        this.nameArgument = nameArgument;
    }

    public String getPhoneArgument() {
        return phoneArgument;
    }

    public void setPhoneArgument(String phoneArgument) {
        this.phoneArgument = phoneArgument;
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

    public void handleAddContact() {
        setTypeWriteContacts(ADD_CONTACT_TYPE);

        if (isWriteContactsGranted()) {
            if (addContact(getNameArgument(), getPhoneArgument())) {
                backToContactsListToFlutter();
            }
        }
    }

    public void handleUpdateContact() {
        setTypeWriteContacts(UPDATE_CONTACT_TYPE);

        if (isWriteContactsGranted()) {
            if (updateContact(getCurrentPhoneArgument(), getNameArgument(), getPhoneArgument())) {
                backToContactsListToFlutter();
            }
        }
    }

    public void handleDeleteContact() {
        setTypeWriteContacts(DELETE_CONTACT_TYPE);

        if (isWriteContactsGranted()) {
            if (deleteContact(getPhoneArgument())) {
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

    private void backToContactsListToFlutter() {
        MethodChannel methodChannel = new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL);
        methodChannel.invokeMethod(BACK_TO_CONTACTS_LIST_TO_FLUTTER, null);
    }

    @SuppressLint("Range")
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

    public boolean addContact(String name, String phone) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            int rawContactInsertIndex = 0;

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build());

            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateContact(String currentPhone, String newName, String newPhone) {
        try {
            String[] DATA_COLS = {
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.CONTACT_ID
            };

            if (currentPhone == null || currentPhone.trim().isEmpty()) return false;
            if (newPhone != null && newPhone.trim().isEmpty()) newPhone = null;
            if (newPhone == null) return false;

            String contactId = getContactId(currentPhone);

            if (contactId == null) return false;

            String where = String.format(
                    "%s = '%s' AND %s = ?",
                    DATA_COLS[0],
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                    DATA_COLS[2]);

            String[] args = {contactId};

            ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            operations.add(
                    ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, args)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName)
                            .build());

            where = String.format(
                    "%s = '%s' AND %s = ?",
                    DATA_COLS[0],
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    DATA_COLS[1]);

            args[0] = currentPhone;

            operations.add(
                    ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(where, args)
                            .withValue(DATA_COLS[1], newPhone)
                            .build());

            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("Range")
    public String getContactId(String number) {
        Cursor cursor = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{number},
                null
        );

        if (cursor == null || cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

        cursor.close();

        return id;
    }

    @SuppressLint("Range")
    public boolean deleteContact(String phoneNumber) {
        try {
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            @SuppressLint("Recycle") Cursor cur = activity.getContentResolver().query(contactUri, null, null, null, null);

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
