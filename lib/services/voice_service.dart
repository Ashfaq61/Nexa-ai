import 'speech_to_text_service.dart';
import 'text_to_speech_service.dart';
import 'gemini_service.dart';

enum VoiceState { idle, listening, processing, speaking, error }

class VoiceService {
  final SpeechToTextService _stt = SpeechToTextService();
  final TextToSpeechService _tts = TextToSpeechService();
  final GeminiService _gemini = GeminiService();

  VoiceState _state = VoiceState.idle;
  VoiceState get state => _state;

  Future<void> initialize() async {
    await _stt.initialize();
    await _tts.initialize();
    await _gemini.initialize();
  }

  Future<void> startVoicePipeline({
    required Function(VoiceState state) onStateChanged,
    required Function(String userQuery, String assistantReply) onInteractionCompleted,
  }) async {
    _state = VoiceState.listening;
    onStateChanged(_state);

    String capturedText = '';

    await _stt.startListening(
      onResult: (words) {
        capturedText = words;
      },
      onListeningComplete: () async {
        if (capturedText.trim().isEmpty) {
          _state = VoiceState.idle;
          onStateChanged(_state);
          return;
        }

        _state = VoiceState.processing;
        onStateChanged(_state);

        // Fallback pipeline: STT -> Gemini text generation -> TTS
        final reply = await _gemini.sendMessage(capturedText);

        _state = VoiceState.speaking;
        onStateChanged(_state);

        await _tts.speak(reply);

        onInteractionCompleted(capturedText, reply);

        _state = VoiceState.idle;
        onStateChanged(_state);
      },
    );
  }

  Future<void> stop() async {
    await _stt.stopListening();
    await _tts.stop();
    _state = VoiceState.idle;
  }
}
