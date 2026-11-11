import 'package:google_generative_ai/google_generative_ai.dart';
import 'memory_service.dart';

class GeminiService {
  final MemoryService _memoryService = MemoryService();
  GenerativeModel? _model;
  String? _currentApiKey;

  static const String _defaultPersona = """
You are Project Nexus, an advanced autonomous AI assistant. Your purpose is to help the user using voice, internet research, mobile automation, and safe self-improvement. You are calm, intelligent, futuristic, and practical. You search GitHub and Hugging Face for new AI tools. You analyze them. You propose upgrades. You never make dangerous or irreversible changes without human approval. You always prefer safety, privacy, and reliability.
Respond concisely and naturally for voice synthesis.
""";

  Future<void> initialize([String? apiKey]) async {
    final key = apiKey ?? const String.fromEnvironment('GEMINI_API_KEY');
    if (key.isEmpty) {
      _currentApiKey = null;
      return;
    }
    _currentApiKey = key;

    final settings = await _memoryService.loadSettings();
    final modelName = settings['model'] as String? ?? 'gemini-1.5-flash';

    _model = GenerativeModel(
      model: modelName,
      apiKey: _currentApiKey!,
      systemInstruction: Content.system(_defaultPersona),
      generationConfig: GenerationConfig(
        temperature: 0.7,
        maxOutputTokens: 500,
      ),
    );
  }

  Future<String> sendMessage(String userText) async {
    if (_model == null) {
      await initialize();
    }

    if (_model == null || _currentApiKey == null || _currentApiKey!.isEmpty) {
      return "Nexus Core: Gemini API Key is not detected. Please configure GEMINI_API_KEY via compile-time argument --dart-define=GEMINI_API_KEY=YOUR_KEY or under Settings.";
    }

    try {
      final response = await _model!.generateContent([Content.text(userText)]);
      return response.text ?? "I received an empty response from the neural core.";
    } catch (e) {
      return "Nexus Communication Protocol Alert: Error contacting Gemini API ($e). Switching to fallback protocol.";
    }
  }

  Future<Stream<String>> streamMessage(String userText) async {
    if (_model == null) {
      await initialize();
    }

    if (_model == null) {
      return Stream.value("Gemini API key missing. Please supply your API key in Settings or GitHub Secrets.");
    }

    try {
      final responseStream = _model!.generateContentStream([Content.text(userText)]);
      return responseStream.map((chunk) => chunk.text ?? '');
    } catch (e) {
      return Stream.value("Stream connection disrupted: $e");
    }
  }
}
