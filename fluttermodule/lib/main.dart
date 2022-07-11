import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttermodule/%20views/login.dart';
import 'package:fluttermodule/%20views/menu.dart';
import 'package:fluttermodule/%20views/settings.dart';
import 'package:fluttermodule/data/preferences.dart';
import 'package:shared_preferences/shared_preferences.dart';

import ' views/main_menu.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();
  Prefs.initInstance(prefs);
  String initialRoute =
      (prefs.getString('username')?.isEmpty ?? true) ? '/login' : '/main_menu';
  runApp(MyApp(initialRoute));
}

const platform = MethodChannel('flutter.channel');

class MyApp extends StatelessWidget {
  const MyApp(this.initialRoute, {Key? key}) : super(key: key);

  final String initialRoute;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      // home: LoginView(),
      initialRoute: initialRoute,
      // debugShowCheckedModeBanner: false,
      routes: {
        '/': (context) => LoginView(),
        '/login': (context) => LoginView(),
        '/main_menu': (context) => MainMenu(),
        '/menu': (context) => Menu(),
        '/settings': (context) => Settings(),
      },
      theme: ThemeData(
          // textTheme: TextTheme( ),
          listTileTheme: ListTileThemeData(
              tileColor: Colors.lime,

              // minLeadingWidth: 50,
              // minVerticalPadding: 20,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(50))),
          inputDecorationTheme: InputDecorationTheme(
              border:
                  OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
    );
  }
}
