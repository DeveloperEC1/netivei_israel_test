import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:netivei_israel_test/presentation/state_management/provider/provider_add_update_contact.dart';
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
    UtilsApp.platform.setMethodCallHandler((call) async {
      String callMethod = call.method;

      if (callMethod == Constants.GET_CONTACTS_TO_FLUTTER) {
        ProviderContactsList providerContactsList =
            Provider.of<ProviderContactsList>(context, listen: false);

        providerContactsList.setContactsModelList(providerContactsList
            .convertStringToListContactsModel(call.arguments));

        providerContactsList.setReceiveContactsList(false);
      } else if (callMethod == Constants.DELETE_CONTACT_TO_FLUTTER) {
        Provider.of<ProviderContactsList>(context, listen: false)
            .deleteContactFromList();
      } else if (callMethod == Constants.BACK_TO_CONTACTS_LIST_TO_FLUTTER) {
        Provider.of<ProviderAddUpdateContact>(context, listen: false)
            .backToContactsList(context);
      }
    });
  }

  static Widget dividerHeight(BuildContext context, double height) {
    return SizedBox(
      height: ResponsiveScreen().heightMediaQuery(context, height),
    );
  }
}
