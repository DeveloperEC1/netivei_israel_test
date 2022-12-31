import 'package:flutter/material.dart';
import 'package:netivei_israel_test/presentation/state_management/provider/provider_add_update_contact.dart';
import 'package:netivei_israel_test/utils/utils_app.dart';
import 'package:provider/provider.dart';
import '../../data/models/contacts_model.dart';

class PageAddUpdateContact extends StatelessWidget {
  final bool isEditCase;
  final ContactsModel contactItemVar;

  const PageAddUpdateContact({
    Key? key,
    required this.isEditCase,
    required this.contactItemVar,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Consumer<ProviderAddUpdateContact>(
      builder: (context, results, child) {
        return PageAddUpdateContactProv(
          isEditCase: isEditCase,
          contactItemVar: contactItemVar,
        );
      },
    );
  }
}

class PageAddUpdateContactProv extends StatefulWidget {
  final bool isEditCase;
  final ContactsModel contactItemVar;

  const PageAddUpdateContactProv({
    Key? key,
    required this.isEditCase,
    required this.contactItemVar,
  }) : super(key: key);

  @override
  PageAddUpdateContactProvState createState() =>
      PageAddUpdateContactProvState();
}

class PageAddUpdateContactProvState extends State<PageAddUpdateContactProv> {
  late ProviderAddUpdateContact _provider;

  @override
  void initState() {
    super.initState();

    _provider = Provider.of<ProviderAddUpdateContact>(context, listen: false);

    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      _provider.setNameController(
          widget.isEditCase ? widget.contactItemVar.name : '');
      _provider.setPhoneController(
          widget.isEditCase ? widget.contactItemVar.phone : '');
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(),
      body: _body(context),
    );
  }

  PreferredSizeWidget _appBar() {
    return AppBar(
      centerTitle: true,
      backgroundColor: Colors.deepPurple,
      title: Text(
        '${widget.isEditCase ? 'Edit' : 'Add'} Contact',
        textAlign: TextAlign.center,
        style: const TextStyle(color: Colors.white),
      ),
    );
  }

  Widget _body(BuildContext context) {
    return Column(
      children: [
        _form(context),
        UtilsApp.dividerHeight(context, 50),
        _btnSave(),
      ],
    );
  }

  Widget _form(BuildContext context) {
    return Column(
      children: [
        UtilsApp.dividerHeight(context, 20),
        _textFormFieldAddUpdate(_provider.nameControllerGet, TextInputType.text,
            'Please Fill Name...'),
        UtilsApp.dividerHeight(context, 20),
        _textFormFieldAddUpdate(_provider.phoneControllerGet,
            TextInputType.phone, 'Please Fill Phone...'),
      ],
    );
  }

  Widget _btnSave() {
    return ElevatedButton(
      onPressed: () => _provider.handleSaveClick(
        widget.isEditCase,
        widget.contactItemVar.phone,
        _provider.nameControllerGet.text,
        _provider.phoneControllerGet.text,
      ),
      child: const Text(
        'Save',
        textAlign: TextAlign.center,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 15,
        ),
      ),
    );
  }

  Widget _textFormFieldAddUpdate(TextEditingController textEditingController,
      TextInputType textInputType, String hint) {
    return TextFormField(
      textAlignVertical: TextAlignVertical.center,
      controller: textEditingController,
      keyboardType: textInputType,
      decoration: InputDecoration(hintText: hint),
    );
  }
}
