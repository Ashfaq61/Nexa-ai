import 'package:flutter_tts/flutter_tts.dart';
import 'memory_service.dart';

class TextToSpeechService {
  final FlutterTts _tts = FlutterTts();
  final MemoryService _memoryService = MemoryService();
  bool _isPlaying = false;

  bool get isPlaying => _isPlaying;

  Future<void> initialize() async {
    final settings = await _memoryService.loadSettings();
    final double speed = (settings['voiceSpeed'] as num?)?.toDouble() ?? 0.5;
    final double pitch = (settings['voicePitch'] as num?)?.toDouble() ?? 1.0;

    await _tts.setSpeechRate(speed);
    await _tts.setPitch(pitch);
    await _tts.setLanguage('en-US');

    _tts.setStartHandler(() {
      _isPlaying = true;
    });

    _tts.setCompletionHandler(() {
      _isPlaying = false;
    });

    _tts.setErrorHandler((msg) {
      _isPlaying = false;
    });
  }

  Future<void> speak(String text) async {
    if (text.trim().isEmpty) return;
    await stop();
    await _tts.speak(text);
  }

  Future<void> stop() async {
    await _tts.stop();
    _isPlaying = false;
  }
}
