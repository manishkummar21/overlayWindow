import 'overlay_window_platform_interface.dart';

class OverlayWindow {
  Future<String?> getPlatformVersion() {
    return OverlayWindowPlatform.instance.getPlatformVersion();
  }

  Future<bool?> checkOverlayPermission() async {
    return OverlayWindowPlatform.instance.checkOverlayPermission();
  }

  Future<bool?> requestPermission() async {
    return OverlayWindowPlatform.instance.requestPermission();
  }

  Future<bool?> showOverlay() {
    return OverlayWindowPlatform.instance.showOverlay();
  }

  Future<bool?> closeOverlay() async {
    return OverlayWindowPlatform.instance.closeOverlay();
  }
}
