import 'package:flutter/material.dart';

void main() {
  //runApp(const MainApp());

  runApp(MyApp(
    title: "Ola Mundo sou eu",
    mensage: "Hello word its me",
  ));
}

class MyApp extends StatelessWidget {
  final String title;
  final String mensage;
  const MyApp({super.key, this.title = '', this.mensage = ''});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text(this.title),
        ),
        body: Center(
          child: Text(
            this.mensage,
            style: TextStyle(color: Colors.black, fontSize: 50),
          ),
        ),
      ),
    );
  }
}
