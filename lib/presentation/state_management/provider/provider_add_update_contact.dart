import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../utils/constants.dart';
import '../../../utils/utils_app.dart';
import 'provider_contacts_list.dart';

class ProviderAddUpdateContact extends ChangeNotifier {
  late TextEditingController _nameController = TextEditingController();
  late TextEditingController _phoneController = TextEditingController();
  String _textError = "";

  TextEditingController get nameControllerGet => _nameController;

  TextEditingController get phoneControllerGet => _phoneController;

  String get textErrorGet => _textError;

  void setNameController(value) {
    _nameController = TextEditingController(text: value);
    notifyListeners();
  }

  void setPhoneController(value) {
    _phoneController = TextEditingController(text: value);
    notifyListeners();
  }

  void setTextError(String textError) {
    _textError = textError;
    notifyListeners();
  }

  void handleSaveClick(
      bool isEditCase, String currentPhone, String newName, String newPhone) {
    if (newName.isEmpty || newPhone.isEmpty) {
      setTextError('Please fill in all the fields...');
    } else {
      setTextError('');

      if (isEditCase) {
        updateContact(currentPhone, newName, newPhone);
      } else {
        addContact(newName, newPhone);
      }
    }
  }

  void addContact(String name, String phone) {
    UtilsApp.platform.invokeMethod(
      Constants.HANDLE_ADD_CONTACT,
      {"name": name, "phone": phone},
    );
  }

  void updateContact(String currentPhone, String name, String phone) {
    UtilsApp.platform.invokeMethod(
      Constants.HANDLE_UPDATE_CONTACT,
      {"currentPhone": currentPhone, "name": name, "phone": phone},
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
