import 'package:flutter/material.dart';

class OverlayMain extends StatelessWidget {
  const OverlayMain({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Spacer(),
        Container(
          height: 500,
          width: double.infinity,
          color: Colors.blue,
          child: const Center(
            child: Text("Testing"),
          ),
        )
      ],
    );
  }
}
