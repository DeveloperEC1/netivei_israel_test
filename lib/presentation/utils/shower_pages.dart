import 'package:flutter/material.dart';
import '../../data/models/contacts_model.dart';
import '../pages/page_add_update_contact.dart';
import '../pages/page_info_contact.dart';

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

  static void pushPageAddEditContact(
      BuildContext context, bool isEditCase, ContactsModel contactItemVar) {
    Navigator.of(context).push(MaterialPageRoute(
        builder: (context) => PageAddUpdateContact(
            isEditCase: isEditCase, contactItemVar: contactItemVar)));
  }
}
