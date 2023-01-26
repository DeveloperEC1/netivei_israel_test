import 'package:flutter/material.dart';
import '../../data/models/contacts_model.dart';
import '../utils/responsive_screen.dart';

class PageInfoContact extends StatelessWidget {
  final ContactsModel contactItemVar;

  const PageInfoContact({
    Key? key,
    required this.contactItemVar,
  }) : super(key: key);

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
      title: const Text(
        'Info Contact',
        textAlign: TextAlign.center,
        style: TextStyle(color: Colors.white),
      ),
    );
  }

  Widget _body(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(ResponsiveScreen().widthMediaQuery(context, 8)),
      child: Column(
        children: [
          _textContactItem('Name: ', contactItemVar.name),
          _textContactItem('Phone: ', contactItemVar.phone),
        ],
      ),
    );
  }

  Widget _textContactItem(String title, String contactItemVar) {
    return Row(
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 20),
        ),
        Text(
          contactItemVar,
          style: const TextStyle(
            color: Colors.deepPurple,
            fontSize: 20,
          ),
        ),
      ],
    );
  }
}
