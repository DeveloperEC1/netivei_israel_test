import 'package:flutter/material.dart';
import 'package:netivei_israel_test/data/models/contacts_model.dart';
import 'package:netivei_israel_test/presentation/pages/page_info_contact.dart';

class ShowerPages {
  static final ShowerPages _singleton = ShowerPages._internal();

  factory ShowerPages() {
    return _singleton;
  }

  ShowerPages._internal();

  static void pushPageInfoContact(
      BuildContext context, ContactsModel contactItemVar) {
    Navigator.of(context).push(MaterialPageRoute(
        builder: (context) => PageInfoContact(contactItemVar: contactItemVar)));
  }
}
