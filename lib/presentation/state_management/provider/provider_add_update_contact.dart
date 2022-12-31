import 'package:flutter/material.dart';

class ProviderAddUpdateContact extends ChangeNotifier {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();

  TextEditingController get nameControllerGet => _nameController;

  TextEditingController get phoneControllerGet => _phoneController;
}
