import 'package:flutter/material.dart';
import '../services/voice_service.dart';
import '../theme/app_theme.dart';

class MicButton extends StatefulWidget {
  final VoiceState voiceState;
  final VoidCallback onPressed;

  const MicButton({
    super.key,
    required this.voiceState,
    required this.onPressed,
  });

  @override
  State<MicButton> createState() => _MicButtonState();
}

class _MicButtonState extends State<MicButton> with SingleTickerProviderStateMixin {
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    )..repeat(reverse: true);

    _pulseAnimation = Tween<double>(begin: 1.0, end: 1.25).animate(
      CurvedAnimation(parent: _pulseController, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _pulseController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bool isListening = widget.voiceState == VoiceState.listening;
    final bool isProcessing = widget.voiceState == VoiceState.processing;
    final bool isSpeaking = widget.voiceState == VoiceState.speaking;

    Color buttonColor = AppTheme.primaryNeon;
    IconData icon = Icons.mic_rounded;

    if (isListening) {
      buttonColor = AppTheme.danger;
      icon = Icons.mic_rounded;
    } else if (isProcessing) {
      buttonColor = AppTheme.secondaryNeon;
      icon = Icons.graphic_eq_rounded;
    } else if (isSpeaking) {
      buttonColor = AppTheme.accentNeon;
      icon = Icons.volume_up_rounded;
    }

    return AnimatedBuilder(
      animation: _pulseAnimation,
      builder: (context, child) {
        final double scale = isListening ? _pulseAnimation.value : 1.0;
        return Transform.scale(
          scale: scale,
          child: Container(
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(
                  color: buttonColor.withValues(alpha: isListening ? 0.6 : 0.3),
                  blurRadius: isListening ? 28 : 14,
                  spreadRadius: isListening ? 8 : 2,
                ),
              ],
            ),
            child: Material(
              color: Colors.transparent,
              child: InkWell(
                onTap: widget.onPressed,
                customBorder: const CircleBorder(),
                child: Ink(
                  width: 84,
                  height: 84,
                  decoration: BoxDecoration(
                    color: buttonColor,
                    shape: BoxShape.circle,
                  ),
                  child: Center(
                    child: isProcessing
                        ? const SizedBox(
                            width: 32,
                            height: 32,
                            child: CircularProgressIndicator(
                              color: Colors.white,
                              strokeWidth: 3,
                            ),
                          )
                        : Icon(
                            icon,
                            size: 40,
                            color: isListening || isProcessing ? Colors.white : Colors.black,
                          ),
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
