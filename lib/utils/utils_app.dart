import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:netivei_israel_test/utils/constants.dart';
import 'package:provider/provider.dart';

import '../presentation/state_management/provider/provider_contacts_list.dart';
import 'responsive_screen.dart';

class UtilsApp {
  static final UtilsApp _singleton = UtilsApp._internal();

  factory UtilsApp() {
    return _singleton;
  }

  UtilsApp._internal();

  static const platform = MethodChannel(Constants.CHANNEL);

  static void getDataFromNative(BuildContext context) {
    ProviderContactsList providerContactsList =
        Provider.of<ProviderContactsList>(context, listen: false);

    UtilsApp.platform.setMethodCallHandler((call) async {
      String callMethod = call.method;

      if (callMethod == Constants.GET_CONTACTS_TO_FLUTTER) {
        providerContactsList.setContactsModelList(providerContactsList
            .convertStringToListContactsModel(call.arguments));
      } else if (callMethod == Constants.DELETE_CONTACT_TO_FLUTTER) {
        providerContactsList.deleteContactFromList();
      }
    });
  }

  static Widget dividerHeight(BuildContext context, double height) {
    return SizedBox(
      height: ResponsiveScreen().heightMediaQuery(context, height),
    );
  }
}
