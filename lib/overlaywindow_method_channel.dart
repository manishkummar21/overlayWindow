import 'package:flutter/services.dart';
import 'package:overlaywindow/overlay_window_platform_interface.dart';

class MethodChannelOverlayWindow extends OverlayWindowPlatform {

  final methodChannel = const MethodChannel('overlay_window', JSONMethodCodec());

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> checkOverlayPermission() async {
    return await methodChannel.invokeMethod(
        'checkPermissions');
  }

  @override
  Future<bool> requestPermission() async {
    return await methodChannel.invokeMethod(
        'requestPermissions');
  }

  @override
  Future<bool> showOverlay() async {
    return await methodChannel.invokeMethod(
        'showSystemWindow');
  }

  @override
  Future<bool> closeOverlay() async {
    return await methodChannel.invokeMethod(
        'closeSystemWindow');
  }
}
