import 'dart:convert';
import 'package:flutter/material.dart';
import '../../../data/models/contacts_model.dart';
import '../../../utils/constants.dart';
import '../../../utils/utils_app.dart';

class ProviderContactsList extends ChangeNotifier {
  List<ContactsModel> _contactsModelList = [];
  int _indexList = -1;

  List<ContactsModel> get contactsModelListGet => _contactsModelList;

  int get indexListGet => _indexList;

  void setContactsModelList(List<ContactsModel> contactsModelList) {
    _contactsModelList = contactsModelList;
    notifyListeners();
  }

  void deleteContactFromList() {
    contactsModelListGet.removeAt(indexListGet);
    notifyListeners();
  }

  void setIndexList(int indexList) {
    _indexList = indexList;
  }

  void getContactsFromPhone() {
    UtilsApp.platform.invokeMethod(Constants.HANDLE_GET_CONTACTS);
  }

  void deleteContact(String phoneNumber) {
    UtilsApp.platform
        .invokeMethod(Constants.HANDLE_DELETE_CONTACT, phoneNumber);
  }

  List<ContactsModel> convertStringToListContactsModel(var value) {
    try {
      var parsed = json.decode(value) as List<dynamic>;
      List<ContactsModel> contactsModelList =
          parsed.map((i) => ContactsModel.fromJson(i)).toList();

      return contactsModelList;
    } catch (err) {
      print(err);

      return [];
    }
  }
}
