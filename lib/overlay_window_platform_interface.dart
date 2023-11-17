import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'overlaywindow_method_channel.dart';

abstract class OverlayWindowPlatform extends PlatformInterface {
  OverlayWindowPlatform() : super(token: _token);

  static final Object _token = Object();

  static OverlayWindowPlatform _instance = MethodChannelOverlayWindow();

  /// The default instance of [OverlaywindowPlatform] to use.
  ///
  /// Defaults to [MethodChannelOverlaywindow].
  static OverlayWindowPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OverlaywindowPlatform] when
  /// they register themselves.
  static set instance(OverlayWindowPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> checkOverlayPermission() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> requestPermission() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> showOverlay() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> closeOverlay() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
