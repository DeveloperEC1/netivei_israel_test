import 'dart:convert';
import 'package:flutter/material.dart';
import '../../../data/models/contacts_model.dart';
import '../../../utils/constants.dart';
import '../../../utils/utils_app.dart';

class ProviderContactsList extends ChangeNotifier {
  List<ContactsModel> _contactsModelList = [];
  bool _receiveContactsList = true;
  int _indexList = -1;

  List<ContactsModel> get contactsModelListGet => _contactsModelList;

  int get indexListGet => _indexList;

  bool get receiveContactsListGet => _receiveContactsList;

  void setContactsModelList(List<ContactsModel> contactsModelList) {
    _contactsModelList = contactsModelList;
    notifyListeners();
  }

  void deleteContactFromList() {
    contactsModelListGet.removeAt(indexListGet);
    notifyListeners();
  }

  void setReceiveContactsList(bool receiveContactsList) {
    _receiveContactsList = receiveContactsList;
    notifyListeners();
  }

  void setIndexList(int indexList) {
    _indexList = indexList;
  }

  void getContactsFromPhone() {
    setReceiveContactsList(true);

    UtilsApp.platform.invokeMethod(Constants.HANDLE_GET_CONTACTS);
  }

  void deleteContact(String phone) {
    UtilsApp.platform.invokeMethod(
      Constants.HANDLE_DELETE_CONTACT,
      {"phone": phone},
    );
  }

  List<ContactsModel> convertStringToListContactsModel(var value) {
    try {
      var parsed = json.decode(value) as List<dynamic>;
      List<ContactsModel> contactsModelList =
          parsed.map((i) => ContactsModel.fromJson(i)).toList();

      return contactsModelList;
    } catch (err) {
      return [];
    }
  }
}
