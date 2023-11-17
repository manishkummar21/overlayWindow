import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:overlaywindow/overlaywindow.dart';
import 'package:overlaywindow_example/overlay_main.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

@pragma("vm:entry-point")
void overlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: OverlayMain(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool _isPermissionThere = false;
  final _overlaywindowPlugin = OverlayWindow();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _overlaywindowPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text('Running on: $_platformVersion\n'),
            TextButton(
                onPressed: () {
                  _overlaywindowPlugin.checkOverlayPermission().then((value) {
                    setState(() {
                      _isPermissionThere = value ?? false;
                    });
                  });
                },
                child: Text("Check Permission + $_isPermissionThere")),
            TextButton(
                onPressed: () {
                  _overlaywindowPlugin.requestPermission();
                },
                child: Text("Request Permission")),
            TextButton(
                onPressed: () {
                  _overlaywindowPlugin.showOverlay();
                },
                child: Text("Show Overlay")),
            TextButton(
                onPressed: () {
                  _overlaywindowPlugin.closeOverlay();
                },
                child: Text("Close Overlay"))
          ],
        ),
      ),
    );
  }
}
