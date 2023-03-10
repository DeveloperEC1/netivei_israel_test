import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../data/models/contacts_model.dart';
import '../../presentation/utils/responsive_screen.dart';
import '../../presentation/utils/shower_pages.dart';
import '../state_management/provider/provider_contacts_list.dart';
import '../utils/utils_app.dart';
import '../widgets/loading_widget.dart';

class PageContactsList extends StatelessWidget {
  const PageContactsList({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Consumer<ProviderContactsList>(
      builder: (context, results, child) {
        return PageContactsListProv();
      },
    );
  }
}

class PageContactsListProv extends StatefulWidget {
  const PageContactsListProv({Key? key}) : super(key: key);

  @override
  PageContactsListProvState createState() => PageContactsListProvState();
}

class PageContactsListProvState extends State<PageContactsListProv> {
  late ProviderContactsList _provider;

  @override
  void initState() {
    super.initState();

    _provider = Provider.of<ProviderContactsList>(context, listen: false);

    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      UtilsApp.getDataFromNative(context);

      _provider.getContactsFromPhone();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: _appBar(),
      body: _contactsList(),
    );
  }

  PreferredSizeWidget _appBar() {
    return AppBar(
      centerTitle: true,
      backgroundColor: Colors.deepPurple,
      title: const Text(
        'Contacts List',
        textAlign: TextAlign.center,
        style: TextStyle(color: Colors.white),
      ),
      actions: <Widget>[
        IconButton(
          icon: const Icon(Icons.add),
          color: Colors.white,
          onPressed: () => {
            ShowerPages.pushPageAddEditContact(context, false, ContactsModel()),
          },
        ),
      ],
    );
  }

  Widget _contactsList() {
    if (_provider.receiveContactsListGet) {
      return const LoadingWidget();
    } else {
      return Padding(
        padding: EdgeInsets.all(ResponsiveScreen().widthMediaQuery(context, 8)),
        child: ListView.separated(
          itemCount: _provider.contactsModelListGet.length,
          itemBuilder: (context, index) {
            return _contactItem(index);
          },
          separatorBuilder: (context, index) => Container(
              height: ResponsiveScreen().widthMediaQuery(context, 15)),
        ),
      );
    }
  }

  Widget _contactItem(int index) {
    var contactItemVar = _provider.contactsModelListGet[index];

    return GestureDetector(
      onTap: () {
        ShowerPages.pushPageInfoContact(context, contactItemVar);
      },
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          _textContactItem(contactItemVar.name),
          Row(
            children: [
              IconButton(
                icon: const Icon(Icons.edit),
                color: Colors.blueAccent,
                onPressed: () => {
                  ShowerPages.pushPageAddEditContact(
                      context,
                      true,
                      ContactsModel.withNamePhone(
                        contactItemVar.name,
                        contactItemVar.phone,
                      )),
                },
              ),
              IconButton(
                icon: const Icon(Icons.delete),
                color: Colors.red,
                onPressed: () => {
                  _provider.setIndexList(index),
                  _provider.deleteContact(contactItemVar.phone),
                },
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _textContactItem(String contactItemVar) {
    return Text(
      contactItemVar,
      style: const TextStyle(
        color: Colors.deepPurple,
        fontSize: 20,
      ),
    );
  }
}
