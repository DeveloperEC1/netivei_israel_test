package com.example.netivei_israel_test.presentation.pages.activities;

import static com.example.netivei_israel_test.core.Constants.CHANNEL;
import static com.example.netivei_israel_test.core.Constants.DELETE_CONTACT_TYPE;
import static com.example.netivei_israel_test.core.Constants.HANDLE_DELETE_CONTACT;
import static com.example.netivei_israel_test.core.Constants.HANDLE_GET_CONTACTS;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_READ_CONTACTS;
import static com.example.netivei_israel_test.core.Constants.PERMISSIONS_WRITE_CONTACTS;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.netivei_israel_test.viewmodels.ContactsViewModel;

import org.jetbrains.annotations.NotNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private ContactsViewModel contactsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactsViewModel = new ContactsViewModel(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        GeneratedPluginRegistrant.registerWith(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                String callMethod = call.method;

                contactsViewModel.setFlutterEngine(flutterEngine);

                if (call.arguments != null) {
                    contactsViewModel.setCallArgumentsStr(call.arguments.toString());
                }

                if (callMethod.equals(HANDLE_GET_CONTACTS)) {
                    contactsViewModel.handleGetContacts();
                } else if (callMethod.equals(HANDLE_DELETE_CONTACT)) {
                    contactsViewModel.handleDeleteContact();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_READ_CONTACTS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactsViewModel.getContacts();
            }
        } else if (requestCode == PERMISSIONS_WRITE_CONTACTS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (contactsViewModel.getTypeWriteContacts().equals(DELETE_CONTACT_TYPE)) {
                    contactsViewModel.handleDeleteContact();
                }
            }
        }
    }

}
