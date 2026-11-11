import 'package:flutter/material.dart';
import '../models/chat_message.dart';
import '../services/voice_service.dart';
import '../services/gemini_service.dart';
import '../services/memory_service.dart';
import '../theme/app_theme.dart';
import '../widgets/mic_button.dart';
import '../widgets/message_bubble.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final VoiceService _voiceService = VoiceService();
  final GeminiService _geminiService = GeminiService();
  final MemoryService _memoryService = MemoryService();
  final TextEditingController _textController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  List<ChatMessage> _messages = [];
  VoiceState _voiceState = VoiceState.idle;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _initServices();
  }

  Future<void> _initServices() async {
    await _voiceService.initialize();
    await _geminiService.initialize();
    final history = await _memoryService.loadChatHistory();
    setState(() {
      _messages = history.isEmpty
          ? [
              ChatMessage(
                id: 'welcome',
                text: "Greetings, Operator. I am Project Nexus. Voice channels and Gemini neural subroutines are online. Tap the mic or type to initiate command sequences.",
                isUser: false,
                timestamp: DateTime.now(),
              )
            ]
          : history;
    });
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  Future<void> _handleUserMessage(String query) async {
    if (query.trim().isEmpty) return;

    final userMsg = ChatMessage(
      id: 'msg_${DateTime.now().millisecondsSinceEpoch}',
      text: query,
      isUser: true,
      timestamp: DateTime.now(),
    );

    setState(() {
      _messages.add(userMsg);
      _isLoading = true;
    });
    _scrollToBottom();
    _textController.clear();

    final replyText = await _geminiService.sendMessage(query);

    final aiMsg = ChatMessage(
      id: 'reply_${DateTime.now().millisecondsSinceEpoch}',
      text: replyText,
      isUser: false,
      timestamp: DateTime.now(),
    );

    setState(() {
      _messages.add(aiMsg);
      _isLoading = false;
    });
    _scrollToBottom();
    await _memoryService.saveChatHistory(_messages);
  }

  void _toggleVoice() {
    if (_voiceState == VoiceState.idle) {
      _voiceService.startVoicePipeline(
        onStateChanged: (state) {
          setState(() {
            _voiceState = state;
          });
        },
        onInteractionCompleted: (userQuery, assistantReply) async {
          final userMsg = ChatMessage(
            id: 'v_user_${DateTime.now().millisecondsSinceEpoch}',
            text: userQuery,
            isUser: true,
            timestamp: DateTime.now(),
          );
          final aiMsg = ChatMessage(
            id: 'v_reply_${DateTime.now().millisecondsSinceEpoch}',
            text: assistantReply,
            isUser: false,
            timestamp: DateTime.now(),
          );
          setState(() {
            _messages.addAll([userMsg, aiMsg]);
          });
          _scrollToBottom();
          await _memoryService.saveChatHistory(_messages);
        },
      );
    } else {
      _voiceService.stop();
      setState(() {
        _voiceState = VoiceState.idle;
      });
    }
  }

  String _getVoiceStateLabel() {
    switch (_voiceState) {
      case VoiceState.listening:
        return "LISTENING... Speak now";
      case VoiceState.processing:
        return "PROCESSING... Querying Gemini Core";
      case VoiceState.speaking:
        return "SPEAKING... Vocal synthesis active";
      default:
        return "TAP TO SPEAK";
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 10,
              height: 10,
              decoration: BoxDecoration(
                color: _voiceState == VoiceState.listening ? AppTheme.danger : AppTheme.accentNeon,
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: AppTheme.primaryNeon.withValues(alpha: 0.6),
                    blurRadius: 8,
                    spreadRadius: 2,
                  ),
                ],
              ),
            ),
            const SizedBox(width: 10),
            const Text("PROJECT NEXUS"),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_sweep_rounded),
            tooltip: "Purge Session History",
            onPressed: () async {
              await _memoryService.clearChatHistory();
              setState(() {
                _messages = [];
              });
            },
          ),
        ],
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Status bar
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: AppTheme.surfaceDark,
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: AppTheme.primaryNeon.withValues(alpha: 0.3)),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(Icons.security, size: 14, color: AppTheme.accentNeon),
                  const SizedBox(width: 8),
                  Text(
                    "AUTONOMOUS AGENT CORE • SAFE-MODE ACTIVE",
                    style: TextStyle(
                      fontSize: 10.5,
                      letterSpacing: 1.0,
                      fontWeight: FontWeight.bold,
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
            ),

            // Chat stream
            Expanded(
              child: _messages.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.hub_rounded, size: 64, color: AppTheme.primaryNeon.withValues(alpha: 0.4)),
                          const SizedBox(height: 16),
                          Text(
                            "Awaiting Voice or Text Input",
                            style: TextStyle(color: AppTheme.textSecondary, fontSize: 16),
                          ),
                        ],
                      ),
                    )
                  : ListView.builder(
                      controller: _scrollController,
                      padding: const EdgeInsets.symmetric(vertical: 8),
                      itemCount: _messages.length,
                      itemBuilder: (context, index) {
                        return MessageBubble(message: _messages[index]);
                      },
                    ),
            ),

            if (_isLoading)
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.primaryNeon),
                    ),
                    const SizedBox(width: 10),
                    Text("Synthesizing response...", style: TextStyle(color: AppTheme.textSecondary, fontSize: 12)),
                  ],
                ),
              ),

            // Voice Orb & Controls
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 12),
              child: Column(
                children: [
                  MicButton(
                    voiceState: _voiceState,
                    onPressed: _toggleVoice,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _getVoiceStateLabel(),
                    style: TextStyle(
                      fontSize: 11,
                      letterSpacing: 1.2,
                      fontWeight: FontWeight.w600,
                      color: _voiceState == VoiceState.listening ? AppTheme.danger : AppTheme.primaryNeon,
                    ),
                  ),
                ],
              ),
            ),

            // Text input bar
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              color: AppTheme.surfaceDark,
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _textController,
                      style: const TextStyle(color: AppTheme.textPrimary),
                      decoration: InputDecoration(
                        hintText: "Type instruction to Nexus...",
                        hintStyle: TextStyle(color: AppTheme.textSecondary.withValues(alpha: 0.6)),
                        filled: true,
                        fillColor: AppTheme.surfaceCard,
                        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(24),
                          borderSide: BorderSide(color: AppTheme.primaryNeon.withValues(alpha: 0.2)),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(24),
                          borderSide: BorderSide(color: AppTheme.primaryNeon.withValues(alpha: 0.2)),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(24),
                          borderSide: const BorderSide(color: AppTheme.primaryNeon),
                        ),
                      ),
                      onSubmitted: _handleUserMessage,
                    ),
                  ),
                  const SizedBox(width: 8),
                  IconButton.filled(
                    style: IconButton.styleFrom(
                      backgroundColor: AppTheme.primaryNeon,
                      foregroundColor: Colors.black,
                    ),
                    icon: const Icon(Icons.arrow_upward_rounded),
                    onPressed: () => _handleUserMessage(_textController.text),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
