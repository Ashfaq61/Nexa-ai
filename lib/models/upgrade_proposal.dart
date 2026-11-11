class UpgradeProposal {
  final String id;
  final String title;
  final String category; // 'Architecture', 'Voice', 'Scouting', 'Memory'
  final String summary;
  final String rationale;
  final String riskAssessment; // 'Low', 'Medium', 'High'
  final String markdownDiff;
  final DateTime createdAt;
  bool isApproved;

  UpgradeProposal({
    required this.id,
    required this.title,
    required this.category,
    required this.summary,
    required this.rationale,
    required this.riskAssessment,
    required this.markdownDiff,
    required this.createdAt,
    this.isApproved = false,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'category': category,
    'summary': summary,
    'rationale': rationale,
    'riskAssessment': riskAssessment,
    'markdownDiff': markdownDiff,
    'createdAt': createdAt.toIso8601String(),
    'isApproved': isApproved,
  };

  factory UpgradeProposal.fromJson(Map<String, dynamic> json) => UpgradeProposal(
    id: json['id'] as String,
    title: json['title'] as String,
    category: json['category'] as String,
    summary: json['summary'] as String,
    rationale: json['rationale'] as String,
    riskAssessment: json['riskAssessment'] as String,
    markdownDiff: json['markdownDiff'] as String,
    createdAt: DateTime.parse(json['createdAt'] as String),
    isApproved: json['isApproved'] as bool? ?? false,
  );
}
