import 'package:flutter/material.dart';
import '../services/memory_service.dart';
import '../theme/app_theme.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final MemoryService _memoryService = MemoryService();

  late TextEditingController _modelController;
  late TextEditingController _baseUrlController;
  bool _safeMode = true;
  double _voiceSpeed = 0.5;
  double _voicePitch = 1.0;
  bool _isLoading = true;

  final List<String> _commonModels = [
    'gemini-1.5-flash',
    'gemini-1.5-pro',
    'gemini-2.0-flash-exp',
    'gemini-2.0-flash',
  ];

  @override
  void initState() {
    super.initState();
    _modelController = TextEditingController();
    _baseUrlController = TextEditingController();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final s = await _memoryService.loadSettings();
    setState(() {
      _modelController.text = s['model'] as String;
      _baseUrlController.text = s['baseUrl'] as String;
      _safeMode = s['safeMode'] as bool;
      _voiceSpeed = (s['voiceSpeed'] as num).toDouble();
      _voicePitch = (s['voicePitch'] as num).toDouble();
      _isLoading = false;
    });
  }

  Future<void> _saveSettings() async {
    await _memoryService.saveSettings(
      model: _modelController.text.trim(),
      baseUrl: _baseUrlController.text.trim(),
      safeMode: _safeMode,
      voiceSpeed: _voiceSpeed,
      voicePitch: _voicePitch,
    );
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Nexus parameters successfully synchronized."),
          backgroundColor: AppTheme.accentNeon,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator(color: AppTheme.primaryNeon)),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("SETTINGS & PROTOCOLS"),
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildSectionHeader("NEURAL CORE CONFIGURATION"),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      "Gemini Model Identifier",
                      style: TextStyle(fontWeight: FontWeight.bold, color: AppTheme.primaryNeon),
                    ),
                    const SizedBox(height: 8),
                    DropdownButtonFormField<String>(
                      value: _commonModels.contains(_modelController.text) ? _modelController.text : null,
                      dropdownColor: AppTheme.surfaceCard,
                      decoration: const InputDecoration(
                        filled: true,
                        fillColor: AppTheme.surfaceDark,
                        border: OutlineInputBorder(),
                      ),
                      items: _commonModels.map((m) {
                        return DropdownMenuItem(value: m, child: Text(m));
                      }).toList(),
                      onChanged: (val) {
                        if (val != null) {
                          setState(() {
                            _modelController.text = val;
                          });
                        }
                      },
                      hint: const Text("Select or enter model"),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      "API Gateway Base URL",
                      style: TextStyle(fontWeight: FontWeight.bold, color: AppTheme.primaryNeon),
                    ),
                    const SizedBox(height: 8),
                    TextField(
                      controller: _baseUrlController,
                      style: const TextStyle(color: AppTheme.textPrimary),
                      decoration: const InputDecoration(
                        filled: true,
                        fillColor: AppTheme.surfaceDark,
                        border: OutlineInputBorder(),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            _buildSectionHeader("AUTONOMY & SAFETY GUARDRAILS"),
            const SizedBox(height: 12),
            Card(
              child: SwitchListTile(
                title: const Text("Strict Safe Mode", style: TextStyle(fontWeight: FontWeight.bold)),
                subtitle: const Text("Prevent unreviewed code modifications and require human cryptographic approval."),
                activeColor: AppTheme.accentNeon,
                value: _safeMode,
                onChanged: (val) {
                  setState(() {
                    _safeMode = val;
                  });
                },
              ),
            ),
            const SizedBox(height: 20),
            _buildSectionHeader("VOCAL SYNTHESIS & ACOUSTICS"),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("Voice Speech Rate (${_voiceSpeed.toStringAsFixed(2)})"),
                    Slider(
                      value: _voiceSpeed,
                      min: 0.1,
                      max: 1.0,
                      divisions: 9,
                      activeColor: AppTheme.primaryNeon,
                      onChanged: (val) => setState(() => _voiceSpeed = val),
                    ),
                    const SizedBox(height: 12),
                    Text("Voice Pitch Frequency (${_voicePitch.toStringAsFixed(2)})"),
                    Slider(
                      value: _voicePitch,
                      min: 0.5,
                      max: 2.0,
                      divisions: 15,
                      activeColor: AppTheme.secondaryNeon,
                      onChanged: (val) => setState(() => _voicePitch = val),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 28),
            ElevatedButton.icon(
              icon: const Icon(Icons.save_rounded),
              label: const Text("SAVE CONFIGURATION"),
              onPressed: _saveSettings,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 12,
        fontWeight: FontWeight.bold,
        letterSpacing: 1.2,
        color: AppTheme.textSecondary,
      ),
    );
  }
}
