import 'package:flutter/material.dart';
import '../services/github_search_service.dart';
import '../services/huggingface_search_service.dart';
import '../services/self_improvement_service.dart';
import '../services/memory_service.dart';
import '../models/tool_report.dart';
import '../models/upgrade_proposal.dart';
import '../theme/app_theme.dart';

class ToolsScreen extends StatefulWidget {
  const ToolsScreen({super.key});

  @override
  State<ToolsScreen> createState() => _ToolsScreenState();
}

class _ToolsScreenState extends State<ToolsScreen> {
  final GitHubSearchService _githubService = GitHubSearchService();
  final HuggingFaceSearchService _hfService = HuggingFaceSearchService();
  final SelfImprovementService _improvementService = SelfImprovementService();
  final MemoryService _memoryService = MemoryService();

  List<ToolReport> _reports = [];
  List<UpgradeProposal> _proposals = [];
  bool _isLoading = false;
  String _statusMessage = "Ready for autonomous scouting & analysis.";

  @override
  void initState() {
    super.initState();
    _loadPersistedData();
  }

  Future<void> _loadPersistedData() async {
    final rep = await _memoryService.loadReports();
    final prop = await _memoryService.loadProposals();
    setState(() {
      _reports = rep;
      _proposals = prop;
    });
  }

  Future<void> _scoutGitHub() async {
    setState(() {
      _isLoading = true;
      _statusMessage = "Scouting GitHub trending AI agents (CrewAI, SmolAgents, LangGraph)...";
    });
    final results = await _githubService.searchTrendingAIAgents();
    setState(() {
      _reports = results;
      _isLoading = false;
      _statusMessage = "Retrieved ${results.length} GitHub AI agents.";
    });
    await _memoryService.saveReports(_reports);
  }

  Future<void> _scoutHFModels() async {
    setState(() {
      _isLoading = true;
      _statusMessage = "Scouting Hugging Face foundation models...";
    });
    final results = await _hfService.searchTrendingModels();
    setState(() {
      _reports = results;
      _isLoading = false;
      _statusMessage = "Retrieved ${results.length} Hugging Face models.";
    });
    await _memoryService.saveReports(_reports);
  }

  Future<void> _scoutHFSpaces() async {
    setState(() {
      _isLoading = true;
      _statusMessage = "Scouting Hugging Face agent spaces...";
    });
    final results = await _hfService.searchTrendingSpaces();
    setState(() {
      _reports = results;
      _isLoading = false;
      _statusMessage = "Retrieved ${results.length} Hugging Face spaces.";
    });
    await _memoryService.saveReports(_reports);
  }

  Future<void> _generateUpgradeProposal() async {
    setState(() {
      _isLoading = true;
      _statusMessage = "Analyzing scouted repositories and evaluating capability gaps...";
    });

    final proposal = await _improvementService.generateUpgradeProposal(_reports);
    setState(() {
      _proposals.insert(0, proposal);
      _isLoading = false;
      _statusMessage = "New upgrade proposal formulated: ${proposal.title}";
    });
    await _memoryService.saveProposals(_proposals);
  }

  void _showDailyReportDialog() {
    final reportMarkdown = _improvementService.compileDailyReport(_reports);
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: AppTheme.surfaceDark,
        title: const Row(
          children: [
            Icon(Icons.assessment_rounded, color: AppTheme.primaryNeon),
            SizedBox(width: 8),
            Text("DAILY INTELLIGENCE REPORT", style: TextStyle(fontSize: 16)),
          ],
        ),
        content: SingleChildScrollView(
          child: SelectableText(
            reportMarkdown,
            style: const TextStyle(fontSize: 13, height: 1.5, color: AppTheme.textPrimary),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("CLOSE", style: TextStyle(color: AppTheme.primaryNeon)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("TOOLS & SELF-IMPROVEMENT"),
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Status card
            Card(
              color: AppTheme.surfaceDark,
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Row(
                  children: [
                    if (_isLoading)
                      const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.primaryNeon),
                      )
                    else
                      const Icon(Icons.satellite_alt_rounded, color: AppTheme.primaryNeon, size: 22),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        _statusMessage,
                        style: const TextStyle(fontSize: 12.5, color: AppTheme.textPrimary),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Action triggers
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                ActionChip(
                  avatar: const Icon(Icons.code_rounded, size: 16),
                  label: const Text("GitHub AI Agents"),
                  backgroundColor: AppTheme.surfaceCard,
                  onPressed: _isLoading ? null : _scoutGitHub,
                ),
                ActionChip(
                  avatar: const Icon(Icons.smart_toy_rounded, size: 16),
                  label: const Text("Hugging Face Models"),
                  backgroundColor: AppTheme.surfaceCard,
                  onPressed: _isLoading ? null : _scoutHFModels,
                ),
                ActionChip(
                  avatar: const Icon(Icons.hub_rounded, size: 16),
                  label: const Text("Hugging Face Spaces"),
                  backgroundColor: AppTheme.surfaceCard,
                  onPressed: _isLoading ? null : _scoutHFSpaces,
                ),
                ActionChip(
                  avatar: const Icon(Icons.upgrade_rounded, size: 16, color: AppTheme.accentNeon),
                  label: const Text("Generate Upgrade Proposal"),
                  backgroundColor: AppTheme.surfaceCard,
                  onPressed: _isLoading ? null : _generateUpgradeProposal,
                ),
                ActionChip(
                  avatar: const Icon(Icons.description_rounded, size: 16, color: AppTheme.secondaryNeon),
                  label: const Text("Create Daily Report"),
                  backgroundColor: AppTheme.surfaceCard,
                  onPressed: _showDailyReportDialog,
                ),
              ],
            ),
            const SizedBox(height: 24),

            // Upgrade Proposals section
            _buildSectionTitle("SAFE UPGRADE PROPOSALS (HUMAN APPROVAL REQUIRED)"),
            const SizedBox(height: 8),
            if (_proposals.isEmpty)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(
                  "No pending proposals. Tap 'Generate Upgrade Proposal' to inspect new capabilities.",
                  style: TextStyle(color: AppTheme.textSecondary, fontSize: 13),
                ),
              )
            else
              ..._proposals.map((prop) => _buildProposalCard(prop)),

            const SizedBox(height: 24),

            // Discovered Tools section
            _buildSectionTitle("SCOUTED AI TARGETS & REPOSITORIES"),
            const SizedBox(height: 8),
            if (_reports.isEmpty)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(
                  "No scouted targets yet. Use the scouting chips above to query GitHub and Hugging Face.",
                  style: TextStyle(color: AppTheme.textSecondary, fontSize: 13),
                ),
              )
            else
              ..._reports.map((r) => _buildReportCard(r)),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 11,
        fontWeight: FontWeight.bold,
        letterSpacing: 1.1,
        color: AppTheme.textSecondary,
      ),
    );
  }

  Widget _buildProposalCard(UpgradeProposal prop) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    prop.title,
                    style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.bold,
                      color: AppTheme.primaryNeon,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: prop.isApproved ? AppTheme.accentNeon.withValues(alpha: 0.2) : AppTheme.danger.withValues(alpha: 0.2),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: prop.isApproved ? AppTheme.accentNeon : AppTheme.danger),
                  ),
                  child: Text(
                    prop.isApproved ? "APPROVED" : "AWAITING HUMAN REVIEW",
                    style: TextStyle(
                      fontSize: 10,
                      fontWeight: FontWeight.bold,
                      color: prop.isApproved ? AppTheme.accentNeon : AppTheme.danger,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(prop.summary, style: const TextStyle(fontSize: 13, color: AppTheme.textPrimary)),
            const SizedBox(height: 8),
            Text(
              "Risk Assessment: ${prop.riskAssessment}",
              style: const TextStyle(fontSize: 12, color: AppTheme.secondaryNeon, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.black,
                borderRadius: BorderRadius.circular(8),
              ),
              child: SelectableText(
                prop.markdownDiff,
                style: const TextStyle(fontFamily: 'monospace', fontSize: 11, color: AppTheme.accentNeon),
              ),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: prop.isApproved ? Colors.grey : AppTheme.accentNeon,
                  ),
                  onPressed: () {
                    setState(() {
                      prop.isApproved = !prop.isApproved;
                    });
                    _memoryService.saveProposals(_proposals);
                  },
                  child: Text(prop.isApproved ? "Revoke Approval" : "Approve Proposed Upgrade"),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildReportCard(ToolReport report) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 6),
      child: ListTile(
        title: Text(
          report.title,
          style: const TextStyle(fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            Text(report.description, maxLines: 2, overflow: TextOverflow.ellipsis),
            const SizedBox(height: 4),
            Text(
              "${report.source.toUpperCase()} • ${report.metricCount} ⭐ / DLs",
              style: const TextStyle(fontSize: 11, color: AppTheme.primaryNeon),
            ),
          ],
        ),
        trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 14),
      ),
    );
  }
}
