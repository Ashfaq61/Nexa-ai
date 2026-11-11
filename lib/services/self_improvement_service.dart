import '../models/upgrade_proposal.dart';
import '../models/tool_report.dart';

class SelfImprovementService {
  Future<UpgradeProposal> generateUpgradeProposal(List<ToolReport> currentReports) async {
    final now = DateTime.now();
    final bool hasSmolAgents = currentReports.any((r) => r.title.toLowerCase().contains('smolagent'));

    if (hasSmolAgents) {
      return UpgradeProposal(
        id: 'prop_${now.millisecondsSinceEpoch}',
        title: 'Integrate SmolAgents Python Code-Action Sandbox',
        category: 'Architecture',
        summary: 'Adopt Hugging Face SmolAgents execution protocol for structured code-based tool execution in backend sandbox.',
        rationale: 'Code-based agents execute complex calculations and data filtering in 30% fewer tokens than JSON-based tool calls.',
        riskAssessment: 'Medium (Requires isolated Docker sandbox on backend)',
        markdownDiff: '''
```diff
+ class SmolAgentExecutor:
+     def __init__(self, authorized_tools):
+         self.agent = CodeAgent(tools=authorized_tools, model=gemini_model)
+     def execute_safely(self, task):
+         # Strictly isolated container execution
+         return self.agent.run(task)
```
''',
        createdAt: now,
        isApproved: false,
      );
    }

    return UpgradeProposal(
      id: 'prop_${now.millisecondsSinceEpoch}',
      title: 'Implement Reflexio Telemetry-Driven Self-Correction Loop',
      category: 'Reflection',
      summary: 'Store conversation error traces in local vector memory to self-refine Gemini prompt templates automatically.',
      rationale: 'Reduces conversational hallucinations by dynamically inserting negative examples into system prompts.',
      riskAssessment: 'Low (Client-side memory refinement only; no source code modified)',
      markdownDiff: '''
```diff
  class MemoryService {
+     Future<void> logExecutionCritique(String query, String failureReason) async {
+         final critique = "Avoid previous pitfall: \$failureReason";
+         await appendNegativeConstraint(critique);
+     }
  }
```
''',
      createdAt: now,
      isApproved: false,
    );
  }

  String compileDailyReport(List<ToolReport> reports) {
    final buffer = StringBuffer();
    final dateStr = DateTime.now().toIso8601String().substring(0, 10);
    buffer.writeln("# Project Nexus - Daily Intelligence Brief ($dateStr)");
    buffer.writeln("\nAutonomous scout telemetry collected ${reports.length} AI agent & model targets.\n");

    buffer.writeln("## Discovered AI Agents & Models");
    for (final r in reports) {
      buffer.writeln("- **${r.title}** (${r.source.toUpperCase()}): ${r.description} [${r.url}]");
    }

    buffer.writeln("\n## Safe Self-Improvement Policy Notice");
    buffer.writeln("Rule 8: No packages or code changes will be installed automatically.");
    buffer.writeln("Rule 7: Push and merge operations require explicit operator cryptographic authorization.");

    return buffer.toString();
  }
}
