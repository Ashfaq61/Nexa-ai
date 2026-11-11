import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/chat_message.dart';
import '../models/tool_report.dart';
import '../models/upgrade_proposal.dart';

class MemoryService {
  static const String _keyHistory = 'nexus_chat_history';
  static const String _keyModel = 'nexus_gemini_model';
  static const String _keyBaseUrl = 'nexus_api_base_url';
  static const String _keySafeMode = 'nexus_safe_mode';
  static const String _keyVoiceSpeed = 'nexus_voice_speed';
  static const String _keyVoicePitch = 'nexus_voice_pitch';
  static const String _keyReports = 'nexus_scouted_reports';
  static const String _keyProposals = 'nexus_upgrade_proposals';

  Future<List<ChatMessage>> loadChatHistory() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_keyHistory);
    if (raw == null) return [];
    try {
      final List decoded = jsonDecode(raw);
      return decoded.map((e) => ChatMessage.fromJson(e)).toList();
    } catch (_) {
      return [];
    }
  }

  Future<void> saveChatHistory(List<ChatMessage> messages) async {
    final prefs = await SharedPreferences.getInstance();
    final list = messages.take(50).map((m) => m.toJson()).toList();
    await prefs.setString(_keyHistory, jsonEncode(list));
  }

  Future<void> clearChatHistory() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyHistory);
  }

  Future<Map<String, dynamic>> loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    return {
      'model': prefs.getString(_keyModel) ?? 'gemini-1.5-flash',
      'baseUrl': prefs.getString(_keyBaseUrl) ?? 'https://generativelanguage.googleapis.com',
      'safeMode': prefs.getBool(_keySafeMode) ?? true,
      'voiceSpeed': prefs.getDouble(_keyVoiceSpeed) ?? 0.5,
      'voicePitch': prefs.getDouble(_keyVoicePitch) ?? 1.0,
    };
  }

  Future<void> saveSettings({
    required String model,
    required String baseUrl,
    required bool safeMode,
    required double voiceSpeed,
    required double voicePitch,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyModel, model);
    await prefs.setString(_keyBaseUrl, baseUrl);
    await prefs.setBool(_keySafeMode, safeMode);
    await prefs.setDouble(_keyVoiceSpeed, voiceSpeed);
    await prefs.setDouble(_keyVoicePitch, voicePitch);
  }

  Future<List<ToolReport>> loadReports() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_keyReports);
    if (raw == null) return [];
    try {
      final List decoded = jsonDecode(raw);
      return decoded.map((e) => ToolReport.fromJson(e)).toList();
    } catch (_) {
      return [];
    }
  }

  Future<void> saveReports(List<ToolReport> reports) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyReports, jsonEncode(reports.map((r) => r.toJson()).toList()));
  }

  Future<List<UpgradeProposal>> loadProposals() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_keyProposals);
    if (raw == null) return [];
    try {
      final List decoded = jsonDecode(raw);
      return decoded.map((e) => UpgradeProposal.fromJson(e)).toList();
    } catch (_) {
      return [];
    }
  }

  Future<void> saveProposals(List<UpgradeProposal> proposals) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyProposals, jsonEncode(proposals.map((p) => p.toJson()).toList()));
  }
}
