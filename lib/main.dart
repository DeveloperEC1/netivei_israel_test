import 'package:flutter/material.dart';
import 'package:netivei_israel_test/presentation/state_management/provider/provider_contacts_list.dart';
import 'package:provider/provider.dart';
import 'presentation/pages/page_contacts_list.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider<ProviderContactsList>(
          create: (context) => ProviderContactsList(),
        ),
      ],
      child: const MaterialApp(
        debugShowCheckedModeBanner: false,
        home: SafeArea(
          child: PageContactsList(),
        ),
      ),
    );
  }
}
