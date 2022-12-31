import 'package:flutter/material.dart';
import 'package:netivei_israel_test/data/models/contacts_model.dart';
import 'package:provider/provider.dart';
import '../../../utils/constants.dart';
import '../../../utils/utils_app.dart';
import 'provider_contacts_list.dart';

class ProviderAddUpdateContact extends ChangeNotifier {
  late TextEditingController _nameController = TextEditingController();
  late TextEditingController _phoneController = TextEditingController();

  TextEditingController get nameControllerGet => _nameController;

  TextEditingController get phoneControllerGet => _phoneController;

  void setNameController(value) {
    _nameController = TextEditingController(text: value);
    notifyListeners();
  }

  void setPhoneController(value) {
    _phoneController = TextEditingController(text: value);
    notifyListeners();
  }

  void handleSaveClick(
      bool isEditCase, String currentPhone, String name, String newPhone) {
    if (isEditCase) {
      updateContact(currentPhone, name, newPhone);
    } else {
      addContact(name, newPhone);
    }
  }

  void addContact(String name, String phone) {
    UtilsApp.platform.invokeMethod(
      Constants.HANDLE_ADD_CONTACT,
      {"name": name, "phone": phone},
    );
  }

  void updateContact(String id, String name, String phone) {
    UtilsApp.platform.invokeMethod(
      Constants.HANDLE_UPDATE_CONTACT,
      {"id": id, "name": name, "phone": phone},
    );
  }

  void backToContactsList(BuildContext context) {
    Navigator.of(context).pop();

    ProviderContactsList providerContactsList =
        Provider.of<ProviderContactsList>(context, listen: false);

    providerContactsList.setReceiveContactsList(true);
    providerContactsList.getContactsFromPhone();
  }
}
